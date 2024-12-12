package aoc.day12;

import aoc.common.Direction;
import aoc.common.Tuple;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
                .getResource("day12/input1").toURI());

        final GardenPlotMap map = new GardenPlotMap();
        try (final Stream<String> lines = Files.lines(path)) {
            lines.forEach(map::addLine);
        }

        final Set<Tuple> coordinatesToCheck = new LinkedHashSet<>();
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                coordinatesToCheck.add(new Tuple(x, y));
            }
        }

        System.out.println("Created " + coordinatesToCheck.size() + " coordinates.");

        final Set<Tuple> alreadyGrouped = new HashSet<>();
        final List<PlotRegion> regions = new ArrayList<>();
        for (final Tuple coordinate : coordinatesToCheck) {
            if(!alreadyGrouped.contains(coordinate)) {
                final PlotRegion region = determineRegion(map, coordinate);
                regions.add(region);
                alreadyGrouped.addAll(region.containedPlots);
            }
        }

        System.out.println("Determined " + regions.size() + " regions.");

        final long totalPrice = regions.stream().map(r -> calculateFencingPrice(map, r)).mapToLong(l -> l).sum();

        System.out.println(totalPrice);
    }

    private long calculateFencingPrice(final GardenPlotMap map, final PlotRegion region) {
        final long counter = region.containedPlots.stream().map(p -> {
            long borderCounter = 0;

            for (final Direction direction : Arrays.asList(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT)) {
                final Tuple next = direction.getNext(p);
                if(!map.isInBounds(next) || (map.isInBounds(next) && !map.getPlotType(next).equals(region.type))) {
                    borderCounter++;
                }
            }
            return borderCounter;
        }).mapToLong(l -> l).sum();
        return counter * region.containedPlots.size();
    }

    private static final List<Direction> DIRECTIONS = Arrays.asList(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT);

    private PlotRegion determineRegion(final GardenPlotMap map, final Tuple coordinate) {
        final Set<Tuple> plotsToCheck = new LinkedHashSet<>();
        plotsToCheck.add(coordinate);
        final String type = map.getPlotType(coordinate);

        final Set<Tuple> checkedPlots = new HashSet<>();

        while(!plotsToCheck.isEmpty()) {
            final Tuple plotToCheck = plotsToCheck.iterator().next();

            for (final Direction direction : DIRECTIONS) {
                final Tuple next = direction.getNext(plotToCheck);
                if(!checkedPlots.contains(next) && !plotsToCheck.contains(next) && map.isInBounds(next) && type.equals(map.getPlotType(next))) {
                    plotsToCheck.add(next);
                }
            }
            checkedPlots.add(plotToCheck);
            plotsToCheck.remove(plotToCheck);
        }
        return new PlotRegion(type, new ArrayList<>(checkedPlots));
    }

    static class PlotRegion {
        final String type;
        final List<Tuple> containedPlots;

        PlotRegion(final String type, final List<Tuple> containedPlots) {
            this.type = type;
            this.containedPlots = containedPlots;
        }

        @Override
        public String toString() {
            return "PlotRegion{" +
                    "type='" + type + '\'' +
                    ", containedPlots=" + containedPlots +
                    '}';
        }
    }

    static class GardenPlotMap {
        final List<List<String>> map = new ArrayList<>();

        public void addLine(final String line) {
            map.add(Arrays.asList(line.split("")));
        }

        public int getHeight() {
            return map.size();
        }

        public int getWidth() {
            return map.get(0).size();
        }

        public boolean isInBounds(final Tuple next) {
            return next.y >= 0 && next.y < getHeight() && next.x >= 0 && next.x < getWidth();
        }

        public String getPlotType(final Tuple coordinate) {
            return map.get(coordinate.y).get(coordinate.x);
        }
    }

}
