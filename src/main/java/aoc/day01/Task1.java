package aoc.day01;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.PriorityQueue;
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
                .getResource("day01/input1").toURI());

        final PriorityQueue<Integer> firstList = new PriorityQueue<>();
        final PriorityQueue<Integer> secondList = new PriorityQueue<>();

        try (final Stream<String> lines = Files.lines(path)) {
            lines.map(line -> line.split("\\s+")).forEach(a -> {
                firstList.add(Integer.parseInt(a[0]));
                secondList.add(Integer.parseInt(a[1]));
            });
        }

        int sum = 0;
        while (!firstList.isEmpty()) {
            int firstElement = firstList.poll();
            int secondElement = secondList.poll();

            sum += Math.abs(firstElement-secondElement);
        }

        System.out.println(sum);
    }

}
