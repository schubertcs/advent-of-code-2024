package aoc.day04;

import aoc.common.Direction;
import aoc.common.Tuple;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
                .getResource("day04/input1").toURI());

        final Grid grid = new Grid();
        try (final Stream<String> lines = Files.lines(path)) {
            lines.forEach(grid::addLine);
        }

        int counter = 0;
        for(int y = 0; y < grid.getNumberOfRows(); y++) {
            for(int x = 0; x < grid.getNumberOfColumns(y); x++) {
                for (final Direction direction : Direction.values()) {
                    if(grid.checkWord("XMAS", new Tuple(x, y), direction)) {
                        counter++;
                    }
                }
            }
        }

        System.out.println(counter);
    }

}

