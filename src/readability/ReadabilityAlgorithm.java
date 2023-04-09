package readability;

public abstract class ReadabilityAlgorithm {
    public ReadabilityMetric calculate(final TextStats stats) {
        var score = getScore(stats);
        return new ReadabilityMetric(score, getAge(score), getName());
    }

    protected abstract double getScore(final TextStats stats);
    protected abstract String getName();

    protected int getAge(final double score) {
        var intScore = (int) Math.ceil(score);
        return switch (intScore) {
            case 1,2,3,4,5,6,7,8,9,10,11,12,13 -> intScore + 5;
            default -> 22;
        };
    }

    static class ReadabilityMetric {

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

class AutomatedReadabilityIndex extends ReadabilityAlgorithm {

    @Override
    protected double getScore(final TextStats stats) {
        return 4.71 * stats.characters() / (double) stats.words()
                + 0.5 * stats.words() / (double) stats.sentences()
                - 21.43;
    }

    @Override
    protected String getName() {
        return "Automated Readability Index";
    }
}

class FleschKincaidIndex extends ReadabilityAlgorithm {

    @Override
    protected double getScore(final TextStats stats) {
        return 0.39 * stats.words() / (double) stats.sentences()
                + 11.8 * stats.syllables() / (double) stats.words()
                - 15.59;
    }

    @Override
    protected String getName() {
        return "Flesch–Kincaid readability tests";
    }
}

class SimpleMeasureOfGobbledygookIndex extends ReadabilityAlgorithm {

    @Override
    protected double getScore(final TextStats stats) {
        return 1.043 * Math.sqrt(stats.polysyllables() * 30 / (double) stats.sentences()) + 3.1291;
    }

    @Override
    protected String getName() {
        return "Simple Measure of Gobbledygook";
    }
}

class ColemanLiauIndex extends ReadabilityAlgorithm {

    @Override
    protected double getScore(final TextStats stats) {
        var L = 100 * stats.characters() / (double) stats.words();
        var S = 100 * stats.sentences() / (double) stats.words();
        return 0.0588 * L - 0.296 * S - 15.8;
    }

    @Override
    protected String getName() {
        return "Coleman–Liau index";
    }
}
