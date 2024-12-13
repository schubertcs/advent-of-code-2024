package aoc.day13;

import aoc.common.Tuple;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Task1 {

    public static void main(String[] args) {
        try {
            new Task1().run();
        } catch (final URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() throws URISyntaxException, IOException {
        final Path path = Paths.get(getClass().getClassLoader()
                .getResource("day13/input1").toURI());

        final List<Equation> equations = new ArrayList<>();
        try (final Stream<String> lines = Files.lines(path)) {
            final List<String> list = lines.toList();
            final List<String> problemLines = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                final String line = list.get(i);
                problemLines.add(line);
                if (line.isEmpty() || i == list.size()-1) {
                    final Equation equation = Equation.build(problemLines);
                    equations.add(equation);
                    problemLines.clear();
                }
            }
        }

        int totalCost = equations.stream().map(this::solveEquation).filter(Optional::isPresent).map(Optional::get).map(t -> 3*t.x + 1*t.y).mapToInt(i -> i).sum();
        System.out.println(totalCost);
    }

    private Optional<Tuple> solveEquation(final Equation equation) {
        int aFactor = 0;
        int bFactor = 100;

        while(aFactor <= 100 && bFactor >= 0) {
            final Tuple factor = new Tuple(aFactor, bFactor);
            final Tuple currentResult = equation.calculateLeftSide(factor);
            if(equation.isFulfilledBy(currentResult)) {
                return Optional.of(factor);
            }
            if(equation.needsLess(currentResult)) {
                bFactor--;
            } else {
                aFactor++;
            }
        }

        return Optional.empty();
    }

    static class Equation {

        private final Tuple vectorA;
        private final Tuple vectorB;
        private final Tuple resultVector;

        Equation(final Tuple vectorA, final Tuple vectorB, final Tuple resultVector) {
            this.vectorA = vectorA;
            this.vectorB = vectorB;
            this.resultVector = resultVector;
        }

        public Tuple calculateLeftSide(final Tuple factor) {
            return new Tuple(factor.x*vectorA.x + factor.y*vectorB.x, factor.x*vectorA.y + factor.y*vectorB.y);
        }

        public boolean isFulfilledBy(final Tuple result) {
            return result.equals(resultVector);
        }

        public boolean needsLess(final Tuple result) {
            return result.x > resultVector.x || result.y > resultVector.y;
        }

        @Override
        public String toString() {
            return "Equation{" +
                    "vectorA=" + vectorA +
                    ", vectorB=" + vectorB +
                    ", resultVector=" + resultVector +
                    '}';
        }

        private static Equation build(final List<String> problemLines) {
            final Tuple vectorA = extractVector(problemLines.get(0));
            final Tuple vectorB = extractVector(problemLines.get(1));
            final Tuple resultVector = extractVector(problemLines.get(2));

            return new Equation(vectorA, vectorB, resultVector);
        }

        private static final Pattern NUMBERS = Pattern.compile("\\d+");
        private static Tuple extractVector(final String line) {
            final Matcher matcher = NUMBERS.matcher(line);
            final int x = retrieveNextMatch(matcher);
            final int y = retrieveNextMatch(matcher);
            return new Tuple(x, y);
        }

        private static int retrieveNextMatch(final Matcher matcher) {
            matcher.find();
            return Integer.parseInt(matcher.group());
        }
    }

}
