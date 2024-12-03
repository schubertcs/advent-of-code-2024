package aoc.day01;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                .getResource("day01/input1").toURI());

        final List<Integer> firstList = new ArrayList<>();
        final Map<Integer, Integer> counterMap = new HashMap<>();

        try (final Stream<String> lines = Files.lines(path)) {
            lines.map(line -> line.split("\\s+")).forEach(a -> {
                firstList.add(Integer.parseInt(a[0]));
                final int secondValue = Integer.parseInt(a[1]);
                counterMap.put(secondValue, counterMap.getOrDefault(secondValue, 0) + 1);
            });
        }
        int total = 0;
        for (final Integer firstElement : firstList) {
            total += firstElement * counterMap.getOrDefault(firstElement, 0);
        }

        System.out.println(total);
    }

}
