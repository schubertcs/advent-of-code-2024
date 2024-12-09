package aoc.day08;

import aoc.common.Tuple;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
                .getResource("day08/input1").toURI());

        try (final Stream<String> lines = Files.lines(path)) {
            final List<String> rows = lines.toList();
            final CityMap map = new CityMap(rows.size(), rows.get(0).length());
            for (int y = 0; y < rows.size(); y++) {
                final String row = rows.get(y);
                final String[] entries = row.split("");
                for (int x = 0; x < entries.length; x++) {
                    final String entry = entries[x];
                    if (!entry.equals(".")) {
                        map.addAntenna(x, y, entry);
                    }
                }
            }
            final Set<Tuple> antinodes = determineAntinodes(map);
            map.printMap(antinodes);
            System.out.println(antinodes.size());
        }
    }

    private Set<Tuple> determineAntinodes(final CityMap map) {
        final Set<LinearEquation> equations = new LinkedHashSet<>();

        for (final String frequency : map.getFrequencies()) {
            final List<Tuple> antennaLocations = map.getAntennaLocations(frequency);

            for (int i = 0; i < antennaLocations.size(); i++) {
                final Tuple tuple1 = antennaLocations.get(i);
                for (int j = i + 1; j < antennaLocations.size(); j++) {
                    final Tuple tuple2 = antennaLocations.get(j);
                    equations.add(new LinearEquation(tuple1, tuple2));
                }
            }
        }

        final Set<Tuple> antinodes = new HashSet<>();
        for(int y = 0; y < map.mapHeight; y++) {
            for (int x = 0; x < map.mapWidth; x++) {
                final Tuple position = new Tuple(x, y);
                for (final LinearEquation equation : equations) {
                    if(equation.isSolution(position)) {
                        antinodes.add(position);
                    }
                }
            }
        }

        return antinodes;
    }

    static class LinearEquation {
        private final Tuple a;
        private final Tuple b;

        public LinearEquation(final Tuple a, final Tuple b) {
            this.a = a;
            this.b = b;
        }

        public boolean isSolution(final Tuple point) {
            return a.x*(b.y - point.y) + b.x*(point.y - a.y) + point.x*(a.y - b.y) == 0;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final LinearEquation that = (LinearEquation) o;
            return Objects.equals(a, that.a) && Objects.equals(b, that.b);
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b);
        }
    }

    static class CityMap {
        private final int mapWidth;
        private final int mapHeight;
        private final Map<String, List<Tuple>> antennas = new HashMap<>();

        public CityMap(final int mapHeight, final int mapWidth) {
            this.mapWidth = mapWidth;
            this.mapHeight = mapHeight;
        }

        public void addAntenna(final int x, final int y, final String frequency) {
            final Tuple antennaLocation = new Tuple(x, y);
            antennas.computeIfAbsent(frequency, k -> new ArrayList<>()).add(antennaLocation);
        }

        public int getMapWidth() {
            return mapWidth;
        }

        private int getMapHeight() {
            return mapHeight;
        }

        public boolean contains(final Tuple location) {
            return location.x >= 0 && location.x < mapWidth && location.y >= 0 && location.y < mapHeight;
        }

        public Set<String> getFrequencies() {
            return antennas.keySet();
        }

        public List<Tuple> getAntennaLocations(final String frequency) {
            return Collections.unmodifiableList(antennas.get(frequency));
        }

        private Optional<String> findAntenna(final Tuple location) {
            for (final Map.Entry<String, List<Tuple>> stringListEntry : antennas.entrySet()) {
                if(stringListEntry.getValue().contains(location)) {
                    return Optional.of(stringListEntry.getKey());
                }
            }
            return Optional.empty();
        }

        public void printMap(final Set<Tuple> antinodes) {
            for(int y = 0; y < this.mapHeight; y++) {
                for(int x = 0; x < this.mapWidth; x++) {
                    final Tuple location = new Tuple(x, y);
                    System.out.print(findAntenna(location).or(() -> {if(antinodes.contains(location)){return Optional.of("#");} else {return Optional.empty();}}).orElse("."));
                }
                System.out.println();
            }
        }
    }
}
