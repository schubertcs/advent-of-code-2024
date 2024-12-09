package aoc.common;

import java.util.Arrays;
import java.util.List;

public enum Direction {
    RIGHT(1, 0),
    DOWN(0, 1),
    LEFT(-1, 0),
    UP(0, -1),
    DOWN_RIGHT(1, 1, Arrays.asList(Direction.DOWN, Direction.RIGHT)),
    DOWN_LEFT(-1, 1, Arrays.asList(Direction.DOWN, Direction.LEFT)),
    UP_LEFT(-1, -1, Arrays.asList(Direction.UP, Direction.LEFT)),
    UP_RIGHT(1, -1, Arrays.asList(Direction.UP, Direction.RIGHT));

    private final int xOffset;
    private final int yOffset;

    Direction(final int xOffset, final int yOffset) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }
    Direction(final int xOffset, final int yOffset, List<Direction> diagonalSplit) {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    public Tuple getNext(final Tuple current) {
        return new Tuple(current.x + xOffset, current.y + yOffset);
    }
    public Direction turn90DegreesRight() {
        return switch (this) {
            case RIGHT -> DOWN;
            case DOWN -> LEFT;
            case LEFT -> UP;
            case UP -> RIGHT;
            case DOWN_RIGHT -> DOWN_LEFT;
            case DOWN_LEFT -> UP_LEFT;
            case UP_LEFT -> UP_RIGHT;
            case UP_RIGHT -> DOWN_RIGHT;
        };
    }
}
