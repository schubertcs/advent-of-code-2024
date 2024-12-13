package aoc.day13;

import aoc.common.Tuple;
import com.google.ortools.Loader;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Task2 {

    public static void main(String[] args) {
        try {
            Loader.loadNativeLibraries();
            new Task2().run();
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
                if (line.isEmpty() || i == list.size() - 1) {
                    final Equation equation = Equation.build(problemLines);
                    equations.add(equation);
                    problemLines.clear();
                }
            }
        }

        final long totalCost = equations.stream().map(this::solveEquation).filter(Optional::isPresent).map(Optional::get).map(t -> 3 * t.x + 1 * t.y).mapToLong(l -> l).sum();
        System.out.println(totalCost);
    }

    private Optional<LongTuple> solveEquation(final Equation equation) {
        final MPSolver solver = MPSolver.createSolver("SCIP");

        final MPVariable aFactor = solver.makeIntVar(0, Long.MAX_VALUE, "a");
        final MPVariable bFactor = solver.makeIntVar(0, Long.MAX_VALUE, "b");

        final MPConstraint xConstraint = solver.makeConstraint(equation.resultVector.x, equation.resultVector.x, "xConstraint");
        xConstraint.setCoefficient(aFactor, equation.vectorA.x);
        xConstraint.setCoefficient(bFactor, equation.vectorB.x);

        final MPConstraint yConstraint = solver.makeConstraint(equation.resultVector.y, equation.resultVector.y, "yConstraint");
        yConstraint.setCoefficient(aFactor, equation.vectorA.y);
        yConstraint.setCoefficient(bFactor, equation.vectorB.y);

        final MPObjective objective = solver.objective();
        objective.setCoefficient(aFactor, 3);
        objective.setCoefficient(bFactor, 1);
        objective.setMinimization();

        final MPSolver.ResultStatus result = solver.solve();
        if (result.equals(MPSolver.ResultStatus.OPTIMAL)) {
            final long aValue = Math.round(aFactor.solutionValue());
            final long bValue = Math.round(bFactor.solutionValue());
            return Optional.of(new LongTuple(aValue, bValue));
        }

        return Optional.empty();
    }

    static class Equation {

        private final Tuple vectorA;
        private final Tuple vectorB;
        private final LongTuple resultVector;

        Equation(final Tuple vectorA, final Tuple vectorB, final LongTuple resultVector) {
            this.vectorA = vectorA;
            this.vectorB = vectorB;
            this.resultVector = resultVector;
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
            final LongTuple actualResultVector = new LongTuple(resultVector.x + 10000000000000L, resultVector.y + 10000000000000L);

            return new Equation(vectorA, vectorB, actualResultVector);
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

    static class LongTuple {
        public final long x;
        public final long y;

        public LongTuple(final long x, final long y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final LongTuple tuple = (LongTuple) o;
            return x == tuple.x && y == tuple.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }

        @Override
        public String toString() {
            return "Tuple{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

}
