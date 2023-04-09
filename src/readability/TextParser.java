package readability;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.regex.Pattern;

record TextStats(int characters, int words, int sentences,
                 int syllables, int polysyllables) {

    @Override
    public String toString() {
        return String.format("""
                        Words: %d
                        Sentences: %d
                        Characters: %d
                        Syllables: %d
                        Polysyllables: %d
                        """,
                this.words, this.sentences, this.characters,
                this.syllables, this.polysyllables);
    }
}

public class TextParser {
    private final String text;
    private final TextStats textStats;

    private TextParser(final String fileName) throws IOException {
        this.text = new String(Files.readAllBytes(Path.of(fileName)));

        var characters = text.replaceAll("\\s","").length();
        var words = (int) Arrays.stream(text.split("\\s")).count();
        var sentences = (int) Arrays.stream(text.split("[.!?]\\s*")).count();
        var syllables = getNumberOfSyllables(text);
        var polysyllables = getNumberOfPolysyllables(text);
        this.textStats = new TextStats(characters, words, sentences, syllables, polysyllables);
    }

    public static TextParser createTextParser(final String fileName) throws IOException {
        return new TextParser(fileName);
    }

    public TextStats getTextStats() {
        return textStats;
    }

    public String getText() {
        return text;
    }

    private int getNumberOfSyllables(final String text) {
        return Arrays.stream(text.split("[.,?!]?\\s"))
                .map(String::toLowerCase)
                .mapToInt(TextParser::getSyllablesCount)
                .sum();
    }

    private int getNumberOfPolysyllables(final String text) {
        // The 'polysyllable' is the number of words with more than 2 syllables.
        return Arrays.stream(text.split("[.,?!]?\\s"))
                .mapToInt(word -> {
                    var syllablesCount = getSyllablesCount(word);
                    return syllablesCount > 2 ? 1 : 0;
                })
                .sum();
    }

    private static int getSyllablesCount(String word) {
        // 1. Count the number of vowels in the word.
        var allVowelsPattern = Pattern.compile("[aeiouy]");
        var allVowelsMatcher = allVowelsPattern.matcher(word);
        var syllablesCount = (int) allVowelsMatcher.results().count();

        // 2. Do not count double-vowels (for example, "rain" has 2 vowels but only 1 syllable).
        var doubleVowelsPattern = Pattern.compile("[aeiouy]{2}");
        var doubleVowelsMatcher = doubleVowelsPattern.matcher(word);
        if (doubleVowelsMatcher.find()) {
            int doubleVowelsCount = 0, idx = 0;
            while (doubleVowelsMatcher.find(idx)) {
                doubleVowelsCount++;
                idx = doubleVowelsMatcher.start() + 1;
            }
            syllablesCount -= doubleVowelsCount;
        }

        // 3. If the last letter in the word is 'e' do not count it as a vowel
        // (for example, "side" has 1 syllable).
        var endingPattern = Pattern.compile(".*e");
        if (endingPattern.matcher(word).matches()) {
            syllablesCount--;
        }

        // 4. If at the end it turns out that the word contains 0 vowels,
        // then consider this word as a 1-syllable one.
        if (syllablesCount <= 0) {
            syllablesCount = 1;
        }
        return syllablesCount;
    }
}
