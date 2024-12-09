package aoc.day04;

import aoc.common.Direction;
import aoc.common.Tuple;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
                .getResource("day04/input1").toURI());

        final Grid grid = new Grid();
        try (final Stream<String> lines = Files.lines(path)) {
            lines.forEach(grid::addLine);
        }

        final Map<Tuple, List<Grid.FoundWord>> crosses = new HashMap<>();
        for (int y = 0; y < grid.getNumberOfRows(); y++) {
            for (int x = 0; x < grid.getNumberOfColumns(y); x++) {
                for (final Direction direction : Arrays.asList(Direction.DOWN_RIGHT, Direction.DOWN_LEFT, Direction.UP_LEFT, Direction.UP_RIGHT)) {
                    final Optional<Grid.FoundWord> maybeFoundWord = grid.findWord("MAS", new Tuple(x, y), direction);
                    if(maybeFoundWord.isPresent()) {
                        final Grid.FoundWord foundWord = maybeFoundWord.get();
                        final Tuple middle = foundWord.getDirection().getNext(foundWord.getStart());
                        crosses.computeIfAbsent(middle, t -> new ArrayList<>()).add(foundWord);
                    }
                }
            }
        }
        int counter = 0;
        for (final Map.Entry<Tuple, List<Grid.FoundWord>> tupleListEntry : crosses.entrySet()) {
            if(tupleListEntry.getValue().size() == 2) {
                counter++;
            }
        }
        System.out.println(counter);
    }
}