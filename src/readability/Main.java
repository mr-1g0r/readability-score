package readability;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static final List<ReadabilityAlgorithm> ALL_READABILITY_ALGORITHMS = List.of(
            new AutomatedReadabilityIndex(), new FleschKincaidIndex(),
            new SimpleMeasureOfGobbledygookIndex(), new ColemanLiauIndex());

    public static void main(String[] args) {
        try {
            final var parser = TextParser.createTextParser(args[0]);
            System.out.printf("The text is:\n%s\n\n%s\n", parser.getText(), parser.getTextStats());

            System.out.println("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
            var avgAge = getAlgorithms().stream()
                    .map(algorithm -> algorithm.calculate(parser.getTextStats()))
                    .peek(System.out::println)
                    .mapToInt(ReadabilityAlgorithm.ReadabilityMetric::getAge)
                    .average()
                    .orElseThrow();
            System.out.printf("\nThis text should be understood in average by %.2f-year-olds.", avgAge);
        } catch (IOException e) {
            System.out.println("Error: Cannot open the file '" + args[0] + "'");
        }
    }

    static List<ReadabilityAlgorithm> getAlgorithms() {
        var scanner = new Scanner(System.in);
        return switch (scanner.next().trim().toLowerCase()) {
            case "ari" -> List.of(ALL_READABILITY_ALGORITHMS.get(0));
            case "fk" -> List.of(ALL_READABILITY_ALGORITHMS.get(1));
            case "smog" -> List.of(ALL_READABILITY_ALGORITHMS.get(2));
            case "cl" -> List.of(ALL_READABILITY_ALGORITHMS.get(3));
            default -> ALL_READABILITY_ALGORITHMS;
        };
    }
}
