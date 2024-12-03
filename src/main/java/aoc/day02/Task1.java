package aoc.day02;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
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
                .getResource("day02/input1").toURI());

        try (final Stream<String> lines = Files.lines(path)) {
            final long fineCount = lines.map(l -> l.split("\\s+")).filter(this::isFine).count();
            System.out.println(fineCount);
        }
    }

    private boolean isFine(final String[] input) {
        final List<Integer> inputList = Arrays.stream(input).map(Integer::parseInt).toList();
        final List<Integer> steps = new ArrayList<>();
        for(int i = 1; i < inputList.size(); i++) {
            steps.add(inputList.get(i) - inputList.get(i-1));
        }
        final float listSignum = Math.signum(steps.get(0));
        return steps.stream().allMatch(s -> Math.abs(s) >= 1 && Math.abs(s) <= 3 && Math.signum(s) == listSignum);
    }

}
