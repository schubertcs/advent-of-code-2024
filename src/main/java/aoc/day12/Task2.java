package aoc.day12;

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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
        // determine border plots
        final List<Pair<Tuple, Direction>> borderPlots = new ArrayList<>();
        for (final Tuple p : region.containedPlots) {
            for (final Direction direction : DIRECTIONS) {
                final Tuple neighbourPlot = direction.getNext(p);
                if (!map.isInBounds(neighbourPlot) || !map.getPlotType(neighbourPlot).equals(region.type)) {
                    borderPlots.add(new Pair<>(p, direction));
                }
            }
        }

        final Set<Pair<Tuple, Direction>> borderPlotsToCheck = new LinkedHashSet<>(borderPlots);

        long sideCounter = 0;
        while(!borderPlotsToCheck.isEmpty()) {
            final Pair<Tuple, Direction> plotToCheck = borderPlotsToCheck.iterator().next();
            sideCounter++;
            borderPlotsToCheck.remove(plotToCheck);

            final Tuple rightNeighbour = plotToCheck.y.turn90DegreesRight().getNext(plotToCheck.x);
            Pair<Tuple, Direction> rightBorder = new Pair<>(rightNeighbour, plotToCheck.y);
            while(borderPlotsToCheck.contains(rightBorder)) {
                borderPlotsToCheck.remove(rightBorder);

                final Tuple nextRightNeighbour = plotToCheck.y.turn90DegreesRight().getNext(rightBorder.x);
                rightBorder = new Pair<>(nextRightNeighbour, plotToCheck.y);
            }

            final Tuple leftNeighbour = plotToCheck.y.turn90DegreesLeft().getNext(plotToCheck.x);
            Pair<Tuple, Direction> leftBorder = new Pair<>(leftNeighbour, plotToCheck.y);
            while(borderPlotsToCheck.contains(leftBorder)) {
                borderPlotsToCheck.remove(leftBorder);

                final Tuple nextLeftNeighbour = plotToCheck.y.turn90DegreesLeft().getNext(leftBorder.x);
                leftBorder = new Pair<>(nextLeftNeighbour, plotToCheck.y);
            }
        }

        return sideCounter * region.containedPlots.size();
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
