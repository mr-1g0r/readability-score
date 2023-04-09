package readability;

@FunctionalInterface
public interface ReadabilityAlgorithm {
    ReadabilityMetric calculate(TextStats stats);

    default int getAge(final double score) {
        var intScore = (int) Math.ceil(score);
        return switch (intScore) {
            case 1,2,3,4,5,6,7,8,9,10,11,12,13 -> intScore + 5;
            case 14 -> 22;
            default -> ReadabilityMetric.AGE_UNDEFINED;
        };
    }

    class ReadabilityMetric {
        public static final int AGE_UNDEFINED = -1;

        private final double score;
        private final int age;
        private final String name;

        public ReadabilityMetric(double score, int age, final String name) {
            this.score = score;
            this.age = age;
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        @Override
        public String toString() {
            return String.format("%s: %.2f (about %d-year-olds).",
                    this.name, this.score, this.age);
        }
    }
}

class AutomatedReadabilityIndex implements ReadabilityAlgorithm {

    @Override
    public ReadabilityMetric calculate(final TextStats stats) {
        var score = 4.71 * stats.characters() / (double) stats.words()
                + 0.5 * stats.words() / (double) stats.sentences()
                - 21.43;
        return new ReadabilityMetric(score, getAge(score), "Automated Readability Index");
    }
}

class FleschKincaidIndex implements ReadabilityAlgorithm {

    @Override
    public ReadabilityMetric calculate(final TextStats stats) {
        var score = 0.39 * stats.words() / (double) stats.sentences()
                + 11.8 * stats.syllables() / (double) stats.words()
                - 15.59;
        return new ReadabilityMetric(score, getAge(score), "Flesch–Kincaid readability tests");
    }
}

class SimpleMeasureOfGobbledygookIndex implements ReadabilityAlgorithm {

    @Override
    public ReadabilityMetric calculate(final TextStats stats) {
        var score = 1.043 * Math.sqrt(stats.polysyllables() * 30 / (double) stats.sentences()) + 3.1291;
        return new ReadabilityMetric(score, getAge(score), "Simple Measure of Gobbledygook");
    }
}

class ColemanLiauIndex implements ReadabilityAlgorithm {

    @Override
    public ReadabilityMetric calculate(final TextStats stats) {
        var L = 100 * stats.characters() / (double) stats.words();
        var S = 100 * stats.sentences() / (double) stats.words();
        var score = 0.0588 * L - 0.296 * S - 15.8;
        return new ReadabilityMetric(score, getAge(score), "Coleman–Liau index");
    }
}
