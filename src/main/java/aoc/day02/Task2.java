package aoc.day02;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Task2 {

    public static void main(String[] args) {
        try {
            new Task2().run();
        } catch (final URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() throws URISyntaxException, IOException {
        final Path path = Paths.get(getClass().getClassLoader()
                .getResource("day02/input1").toURI());

        try (final Stream<String> lines = Files.lines(path)) {
            final long fineCount = lines.map(l -> l.split("\\s+")).filter(this::isFine).count();
            System.out.println(fineCount);
        }
    }

    private boolean isFine(final String[] input) {
        final List<Integer> inputList = Arrays.stream(input).map(Integer::parseInt).toList();
        return isFineInner(inputList, true);
    }

    private boolean isFineInner(List<Integer> input, boolean dampenerAvailable) {
        System.out.println("Testing " + input);
        final List<Integer> steps = new ArrayList<>();
        for(int i = 1; i < input.size(); i++) {
            steps.add(input.get(i) - input.get(i-1));
        }
        final float listSignum = Math.signum(steps.get(0));
        final boolean isFine = steps.stream().allMatch(s -> Math.abs(s) >= 1 && Math.abs(s) <= 3 && Math.signum(s) == listSignum);
        if(isFine) {
            System.out.println("Safe");
            return true;
        }

        if(!dampenerAvailable) {
            System.out.println("Unsafe");
            return false;
        }

        for (int i = 0; i < input.size(); i++) {
            final List<Integer> copy = new ArrayList<>();
            for(int j = 0; j < input.size(); j++) {
                if (j != i) {
                    copy.add(input.get(j));
                }
            }
            if(isFineInner(copy, false)) {
                System.out.println("Safe");
                return true;
            }
        }
        System.out.println("Unsafe");
        return false;

        /*
        for (int i = 0; i < input.length; i++) {
            final String[] copy = new String[input.length-1];
            for(int j = 0; j < copy.length; j++) {
                int offset = 0;
                if (j == i) {
                    offset = 1;
                }
                copy[j] = input[i+offset];
            }
            boolean reducedIsFine = isFine(copy);
            if (reducedIsFine) {
                System.out.println("Safe");
                return true;
            }
        }
        System.out.println("UnSafe");
        return false;*/
    }
}
