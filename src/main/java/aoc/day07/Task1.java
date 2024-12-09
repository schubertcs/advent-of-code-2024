package aoc.day07;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
                .getResource("day07/input1").toURI());

        try (final Stream<String> lines = Files.lines(path)) {
            final List<OperationlessEquation> equations = lines.map(OperationlessEquation::build).toList();
            long resultSum = 0;
            for (final OperationlessEquation equation : equations) {
                if(bruteForce(equation)) {
                    resultSum += equation.result;
                }
            }
            System.out.println(resultSum);
        }
    }

    private boolean bruteForce(final OperationlessEquation operationlessEquation) {
        final int numberOfOperators = operationlessEquation.getNumberOfOperatorsRequired();
        final long permutationLimit = ((int)Math.pow(2, numberOfOperators))-1;
        System.out.println(operationlessEquation);

        for(int i = 0; i <= permutationLimit; i++) {
            final List<Operation> operations = convertToOperations(i);
            prependAdditions(operations, numberOfOperators);
            if(operationlessEquation.isFulfilled(operations)){
                System.out.println(operations);
                System.out.println("success");
                return true;
            }
        }
        return false;
    }

    private void prependAdditions(final List<Operation> operations, final long targetSize) {
        while(operations.size() != targetSize){
            operations.add(0, Operation.ADDITION);
        }
    }

    private List<Operation> convertToOperations(final int permutation) {
        final String binaryString = Integer.toBinaryString(permutation);
        final List<Operation> operations = new ArrayList<>(binaryString.length());
        for (final char c : binaryString.toCharArray()) {
            if(c == '1') {
                operations.add(Operation.MULTIPLICATION);
            } else {
                operations.add(Operation.ADDITION);
            }
        }
        return operations;
    }

    static class OperationlessEquation {
        private final List<Integer> operands;
        private final long result;

        OperationlessEquation(final List<Integer> operands, final long result) {
            this.operands = operands;
            this.result = result;
        }

        static OperationlessEquation build(final String line) {
            final String[] split = line.split(":");
            final long result = Long.parseLong(split[0]);
            final List<Integer> operands = Arrays.stream(split[1].trim().split(" ")).map(Integer::parseInt).collect(Collectors.toList());
            return new OperationlessEquation(operands, result);
        }

        public int getNumberOfOperatorsRequired() {
            return operands.size() - 1;
        }

        public boolean isFulfilled(final List<Operation> operations) {
            long currentValue = operands.get(0);

            for (int i = 0; i < operations.size(); i++) {
                final Operation operation = operations.get(i);
                final long secondOperand = operands.get(i + 1);
                currentValue = operation.apply(currentValue, secondOperand);
            }

            return currentValue == result;
        }

        @Override
        public String toString() {
            return "OperationlessEquation{" +
                    "operands=" + operands +
                    ", result=" + result +
                    '}';
        }
    }

    enum Operation {
        ADDITION {
            @Override
            long apply(final long a, final long b) {
                return a+b;
            }
        },
        MULTIPLICATION {
            @Override
            long apply(final long a, final long b) {
                return a*b;
            }
        };

        abstract long apply(long a, long b);
    }
}
