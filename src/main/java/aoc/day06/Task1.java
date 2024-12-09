package aoc.day06;

import aoc.common.Direction;
import aoc.common.Tuple;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
                .getResource("day06/input1").toURI());

        try (final Stream<String> lines = Files.lines(path)) {
            final List<String> rows = lines.collect(Collectors.toList());
            for (final String row : rows) {
                final List<String> entries = Arrays.asList(row.split(""));
                final int guardColumn = entries.indexOf("^");
                if (guardColumn != -1) {
                    entries.set(guardColumn, ".");
                    guardLocation = new Tuple(guardColumn, guardMap.size());
                }
                guardMap.add(entries);
            }
        }
        letGuardRun();
        long numberOfReachedFields = guardMap.stream().map(l -> l.stream().filter(s -> s.equals("X")).count()).mapToLong(i -> i).sum() + 1;
        System.out.println(guardMap);
        System.out.println(guardLocation);
        System.out.println(numberOfReachedFields);
    }


    private final List<List<String>> guardMap = new ArrayList<>();
    private Tuple guardLocation;
    private Direction direction = Direction.UP;

    private void letGuardRun() {
        while(true) {
            final Tuple newLocation = direction.getNext(guardLocation);
            if(isOutOfBounds(newLocation)) {
                return;
            }
            if(isObstacle(newLocation)) {
                direction = direction.turn90DegreesRight();
            } else {
                guardMap.get(guardLocation.y).set(guardLocation.x, "X");
                guardLocation = newLocation;
            }
        }
    }

    private boolean isObstacle(final Tuple location) {
        final String field = guardMap.get(location.y).get(location.x);
        return field.equals("#");
    }

    private boolean isOutOfBounds(final Tuple location) {
        if (location.y >= guardMap.size() || location.y < 0) {
            return true;
        }

        final List<String> row = guardMap.get(location.y);
        return location.x >= row.size() || location.x < 0;
    }
}
