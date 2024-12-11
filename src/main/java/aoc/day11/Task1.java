package aoc.day11;

import aoc.common.Pair;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
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
                .getResource("day11/input1").toURI());

        try (final Stream<String> lines = Files.lines(path)) {
            final List<String> rows = lines.toList();
            final String singleRow = rows.get(0);
            final String[] values = singleRow.split(" ");
            final long startTime = System.currentTimeMillis();
            System.out.println(Arrays.stream(values).map(v -> applyRules(v, 75)).mapToLong(l -> l).sum());
            final long endTime = System.currentTimeMillis();
            System.out.println("Took " + (endTime-startTime) + " ms");
        }
    }

    private long applyRules(final String value, final int iterations) {
        final List<String> valuesList = new LinkedList<>();
        valuesList.add(value);
        return applyRules(valuesList, iterations);
    }

    private static final Map<Pair<List<String>, Integer>, Long> memoization = new HashMap<>();

    private long applyRules(final List<String> values, final int iterations) {
        if(iterations == 0) {
            return values.size();
        }
        final Pair<List<String>, Integer> key = new Pair<>(values, iterations);
        if(memoization.containsKey(key)) {
            return memoization.get(key);
        }
        long sum = 0;
        for (final String value : values) {
            final List<String> nextStones = applyRules(value);
            for (final String nextStone : nextStones) {
                sum += applyRules(nextStone, iterations-1);
            }
        }
        memoization.put(key, sum);
        return sum;
    }

    private List<String> applyRules(final String value) {
        final List<String> result = new ArrayList<>();

        if(value.equals("0")) {
            result.add("1");
        } else if(value.length() % 2 == 0) {
            final String firstHalf = removeLeadingZeros(value.substring(0, value.length() / 2));
            final String secondHalf = removeLeadingZeros(value.substring(value.length()/2));

            result.add(firstHalf);
            result.add(secondHalf);
        } else {
            final long longValue = Long.parseLong(value);
            result.add(Long.toString(longValue*2024));
        }
        return result;
    }

    private String removeLeadingZeros(final String input) {
        return Integer.valueOf(input).toString();
    }
}
