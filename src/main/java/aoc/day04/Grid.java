package aoc.day04;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

class Grid {
    private final List<List<String>> grid = new ArrayList<>();
    private static final String EMPTY = ".";

    public void addLine(final String line) {
        final List<String> list = Arrays.asList(line.split(""));
        grid.add(list);
    }

    public boolean checkWord(String wordToFind, Tuple start, Direction direction) {
        return findWord(wordToFind, start, direction).isPresent();
    }

    public Optional<FoundWord> findWord(String wordToFind, Tuple start, Direction direction) {
        final StringBuilder foundWord = new StringBuilder();
        for (Tuple current = start;
             foundWord.length() < wordToFind.length() && wordToFind.startsWith(foundWord.toString());
             current = direction.getNext(current)) {
            foundWord.append(getEntry(current));
        }
        if (foundWord.toString().equals(wordToFind)) {
            return Optional.of(new FoundWord(start, direction));
        } else {
            return Optional.empty();
        }
    }

    public String getEntry(final Tuple point) {
        if (point.y < 0 || point.y >= grid.size()) {
            return EMPTY;
        }
        final List<String> row = grid.get(point.y);
        if (point.x < 0 || point.x >= row.size()) {
            return EMPTY;
        }
        return row.get(point.x);
    }

    public int getNumberOfRows() {
        return grid.size();
    }

    public int getNumberOfColumns(final int y) {
        return grid.get(y).size();
    }

    public static class FoundWord {
        private final Tuple start;
        private final Direction direction;

        public FoundWord(final Tuple start, final Direction direction) {
            this.start = start;
            this.direction = direction;
        }

        public Tuple getStart() {
            return start;
        }

        public Direction getDirection() {
            return direction;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final FoundWord foundWord = (FoundWord) o;
            return Objects.equals(start, foundWord.start) && direction == foundWord.direction;
        }

        @Override
        public int hashCode() {
            return Objects.hash(start, direction);
        }
    }
}
