package aoc.day10;

import aoc.common.Direction;
import aoc.common.Tuple;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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
                .getResource("day10/input1").toURI());


        try (final Stream<String> lines = Files.lines(path)) {
            final List<String> rows = lines.toList();
            final Map<Tuple, Node> nodeMapping = new HashMap<>();

            final MountainMap mountainMap = new MountainMap(rows);
            for (final Tuple location : mountainMap) {
                // initialize nodeMapping
                final Node currentNode = new Node(mountainMap.getLevel(location));
                nodeMapping.put(location, currentNode);
            }

            final List<Node> startingNodes = new ArrayList<>();
            for (final Tuple location : mountainMap) {
                final Node currentNode = nodeMapping.get(location);
                if(currentNode.level == 0) {
                    startingNodes.add(currentNode);
                }

                for (final Direction direction : Arrays.asList(Direction.UP, Direction.RIGHT, Direction.DOWN, Direction.LEFT)) {
                    final Optional<Tuple> neighbourLocation = mountainMap.getNeighbour(location, direction);
                    if(neighbourLocation.isPresent()) {
                        final Node neighbour = nodeMapping.get(neighbourLocation.get());
                        if(neighbour.level == currentNode.level+1) {
                            currentNode.addNeighbour(neighbour);
                        }
                    }
                }
            }
            int numberOfTrails = 0;
            for (final Node startingNode : startingNodes) {
                final Set<Node> finalNodesReached = determineFinalNodesReached(startingNode);
                numberOfTrails += finalNodesReached.size();
            }
            System.out.println(numberOfTrails);
        }
    }

    private Set<Node> determineFinalNodesReached(final Node startingNode) {
        final Set<Node> finalNodesReached = new HashSet<>();
        final List<Node> neighboursToTry = new ArrayList<>(startingNode.getNeighbours());
        while(!neighboursToTry.isEmpty()) {
            final Node neighbour = neighboursToTry.remove(0);
            if(neighbour.isFinalLevel()) {
                finalNodesReached.add(neighbour);
            }
            finalNodesReached.addAll(determineFinalNodesReached(neighbour));
        }
        return finalNodesReached;
    }


    static class MountainMap implements Iterable<Tuple> {
        private final List<List<Integer>> map = new ArrayList<>();
        MountainMap(final List<String> rows) {
            rows.forEach(r -> {
                final List<Integer> rowList = Arrays.stream(r.split("")).map(Integer::parseInt).toList();
                map.add(rowList);
            });
        }

        public int getLevel(final Tuple location) {
            if(!isInBounds(location)) {
                throw new RuntimeException("Should not happen, received " + location);
            }

            return map.get(location.y).get(location.x);
        }

        public Optional<Tuple> getNeighbour(final Tuple location, final Direction direction) {
            final Tuple neighbourLocation = direction.getNext(location);
            if(isInBounds(neighbourLocation)) {
                return Optional.of(neighbourLocation);
            }

            return Optional.empty();
        }

        private boolean isInBounds(final Tuple location) {
            return location.y >= 0 && location.y < map.size() && location.x >= 0 && location.x < map.get(location.y).size();
        }

        public void print() {
            for (final List<Integer> integers : map) {
                for (final Integer integer : integers) {
                    System.out.print(integer);
                }
                System.out.println();
            }
        }

        @Override
        public Iterator<Tuple> iterator() {
            return new ListIterator(map);
        }
    }

    public static class ListIterator implements Iterator<Tuple> {
        private final List<List<Integer>> listOfLists;
        private int outerIndex; // To track the index of the outer list
        private int innerIndex; // To track the index of the inner list

        // Constructor
        public ListIterator(List<List<Integer>> listOfLists) {
            this.listOfLists = listOfLists;
            outerIndex = 0;
            innerIndex = 0;
        }

        @Override
        public boolean hasNext() {
            if(innerIndex >= listOfLists.get(outerIndex).size()) {
                outerIndex++;
                innerIndex = 0;
            }
            return outerIndex < listOfLists.size();
        }

        @Override
        public Tuple next() {
            if (!hasNext()) {
                throw new NoSuchElementException("No more elements in the list.");
            }

            return new Tuple(outerIndex, innerIndex++);
        }
    }

    static class Node {
        private final List<Node> neighbours = new ArrayList<>();
        private final int level;

        Node(final int level) {
            this.level = level;
        }

        public List<Node> getNeighbours() {
            return Collections.unmodifiableList(neighbours);
        }

        public int getLevel() {
            return level;
        }

        public void addNeighbour(final Node neighbour) {
            neighbours.add(neighbour);
        }

        public boolean isFinalLevel() {
            return level == 9;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "level=" + level +
                    '}';
        }
    }
}
