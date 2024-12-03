package aoc.day03;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
                .getResource("day03/input1").toURI());

        try (final Stream<String> lines = Files.lines(path)) {
            int total = lines.map(this::processLine).mapToInt(Integer::intValue).sum();
            System.out.println(total);
        }
    }

    private static final Pattern MUL_PATTERN = Pattern.compile("mul\\((?<a>\\d+),(?<b>\\d+)\\)|do\\(\\)|don't\\(\\)");
    private boolean activated = true;

    private int processLine(final String line) {
        final Matcher matcher = MUL_PATTERN.matcher(line);
        int sum = 0;
        while(matcher.find()) {
            if(matcher.group().equals("do()")) {
                activated = true;
            } else if (matcher.group().equals("don't()")) {
                activated = false;
            } else if (activated && matcher.group().startsWith("mul(")) {
                int a = Integer.parseInt(matcher.group("a"));
                int b = Integer.parseInt(matcher.group("b"));
                sum += a*b;
            }
        }
        return sum;
    }

}
