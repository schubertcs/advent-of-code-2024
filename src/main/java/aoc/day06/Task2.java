package aoc.day06;

import aoc.common.Direction;
import aoc.common.Pair;
import aoc.common.Tuple;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
                .getResource("day06/input1").toURI());

        try (final Stream<String> lines = Files.lines(path)) {
            final List<String> rows = lines.toList();
            for (final String row : rows) {
                final List<String> entries = Arrays.asList(row.split(""));
                final int guardColumn = entries.indexOf("^");
                if (guardColumn != -1) {
                    entries.set(guardColumn, ".");
                    initialGuardLocation = new Tuple(guardColumn, guardMap.size());
                    currentGuardLocation = initialGuardLocation;
                }
                guardMap.add(entries);
            }
        }
        final Set<Tuple> visitedLocations = letGuardRun().get();
        printMap(visitedLocations);

        int loopCounter = 0;

        final Set<Tuple> potentialObstacles = new HashSet<>(visitedLocations);
        potentialObstacles.add(currentGuardLocation);
        for (final Tuple potentialObstacle : potentialObstacles) {
            guardMap.get(potentialObstacle.y).set(potentialObstacle.x, "O");
            currentGuardLocation = initialGuardLocation;
            direction = initialDirection;
            //System.out.println(potentialObstacle);
            //printMap(Collections.emptySet());

            final Optional<?> maybeResult =  letGuardRun();
            if(maybeResult.isEmpty()) {
                System.out.println("Loop detected!");
                loopCounter++;
            }

            guardMap.get(potentialObstacle.y).set(potentialObstacle.x, ".");
        }
        System.out.println(loopCounter);
    }


    private final List<List<String>> guardMap = new ArrayList<>();
    private Tuple initialGuardLocation;
    private Direction initialDirection = Direction.UP;
    private Tuple currentGuardLocation;
    private Direction direction = Direction.UP;

    private Optional<Set<Tuple>> letGuardRun() {
        final Set<Tuple> visitedLocations = new HashSet<>();
        final Set<Pair<Tuple, Direction>> turns = new HashSet<>();
        while (true) {
            final Tuple newLocation = direction.getNext(currentGuardLocation);
            if (isOutOfBounds(newLocation)) {
                return Optional.of(visitedLocations);
            }
            final Pair<Tuple, Direction> pair = new Pair<>(currentGuardLocation, direction);
            if (turns.contains(pair)) {
                return Optional.empty();
            }
            if (isObstacle(newLocation)) {
                turns.add(pair);
                direction = direction.turn90DegreesRight();
            } else {
                visitedLocations.add(currentGuardLocation);
                currentGuardLocation = newLocation;
            }
        }
    }

    private boolean isObstacle(final Tuple location) {
        final String field = guardMap.get(location.y).get(location.x);
        return field.equals("#") || field.equals("O");
    }

    private boolean isOutOfBounds(final Tuple location) {
        if (location.y >= guardMap.size() || location.y < 0) {
            return true;
        }

        final List<String> row = guardMap.get(location.y);
        return location.x >= row.size() || location.x < 0;
    }

    private void printMap(final Set<Tuple> visitedLocations) {
        for (int i = 0; i < guardMap.size(); i++) {
            final List<String> row = guardMap.get(i);
            for (int j = 0; j < row.size(); j++) {
                final Tuple printLocation = new Tuple(j, i);
                if (printLocation.equals(currentGuardLocation)) {
                    System.out.print(guardIcon());
                } else if(visitedLocations.contains(printLocation)) {
                    System.out.print("X");
                } else {
                    System.out.print(row.get(j));
                }
            }
            System.out.println();
        }
    }

    private String guardIcon() {
        return switch (direction) {
            case RIGHT -> ">";
            case DOWN -> "v";
            case LEFT -> "<";
            case UP -> "^";
            default -> "";
        };
    }
}
