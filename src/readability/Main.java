package readability;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        var text = getText(args[0]);
        var stats = getTextStats(text);
        var score = getScore(stats);
        var gradeAge = getGradeAge(score);

        System.out.printf("\n%s\n", stats);
        System.out.printf("The score is: %.2f\n", score);
        System.out.printf("The text should be understood by %s year-olds", gradeAge);
    }

    static String getText(final String fileName) {
        var result = new StringBuffer();
        try {
            result.append(new String(Files.readAllBytes(Path.of(fileName))));
        } catch (IOException e) {
            System.out.println("Error: Cannot open the file '" + fileName + "'");
        }

        System.out.println("The text is:");
        System.out.println(result);

        return result.toString();
    }

    static TextStats getTextStats(final String text) {
        var characters = text.replaceAll("\\s","").length();
        var words = (int) Arrays.stream(text.split("\\s")).count();
        var sentences = (int) Arrays.stream(text.split("[.!?]\\s*")).count();

        return new TextStats(characters, words, sentences);
    }

    static double getScore(final TextStats stats) {
        return 4.71 * (double) stats.characters() / (double) stats.words()
                + 0.5 * (double) stats.words() / (double) stats.sentences()
                - 21.43;
    }

    static String getGradeAge(double score) {
        int intScore = (int) Math.ceil(score);
        return switch (intScore) {
            case 1,2,3,4,5,6,7,8,9,10,11,12,13 ->
                    String.format("%d-%d", intScore + 4, intScore + 5);
            case 14 -> "18-22";
            default -> "n/a";
        };
    }

    record TextStats(int characters, int words, int sentences) {

        @Override
        public String toString() {
            return String.format("Words: %d\nSentences: %d\nCharacters: %d\n",
                    this.words, this.sentences, this.characters);
        }
    }
}
