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
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
        final Set<Tuple> antinodes = new HashSet<>();

        for (final String frequency : map.getFrequencies()) {
            final List<Tuple> antennaLocations = map.getAntennaLocations(frequency);

            for (int i = 0; i < antennaLocations.size(); i++) {
                final Tuple tuple1 = antennaLocations.get(i);
                for (int j = i + 1; j < antennaLocations.size(); j++) {
                    final Tuple tuple2 = antennaLocations.get(j);

                    final Tuple antinode1 = new Tuple(tuple1.x + (tuple2.x - tuple1.x) * 2, tuple1.y + (tuple2.y - tuple1.y) * 2);
                    final Tuple antinode2 = new Tuple(tuple2.x + (tuple1.x - tuple2.x) * 2, tuple2.y + (tuple1.y - tuple2.y) * 2);
                    System.out.println(frequency + ": " + tuple1 + " + " + tuple2 + " -> " + antinode1 + "; " + antinode2);

                    if(map.contains(antinode1)) {
                        antinodes.add(antinode1);
                    }

                    if(map.contains(antinode2)) {
                        antinodes.add(antinode2);
                    }
                }
            }
        }

        return antinodes;
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

            for (final Map.Entry<String, List<Tuple>> stringListEntry : antennas.entrySet()) {
                System.out.println(stringListEntry.getKey() + " -> " + stringListEntry.getValue());
            }
        }
    }
}
