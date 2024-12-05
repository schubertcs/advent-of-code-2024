package aoc.day05;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
                .getResource("day05/input1").toURI());

        try (final Stream<String> lines = Files.lines(path)) {
            lines.forEach(this::processLine);
        }
        System.out.println(middleNumberSum);
    }

    private boolean rulesSection = true;
    private Map<String, Set<String>> precedenceMap = new HashMap<>();
    private int middleNumberSum = 0;

    private void processLine(final String line) {
        if(line.trim().isEmpty()) {
            //for (final Map.Entry<String, Set<String>> stringSetEntry : precedenceMap.entrySet()) {
                //System.out.println(stringSetEntry.getKey() + "->" + stringSetEntry.getValue() );
            //}
            rulesSection = false;
        } else if(rulesSection) {
            final String[] split = line.split("\\|");
            precedenceMap.computeIfAbsent(split[0], k -> new HashSet<>()).add(split[1]);
        } else {
            final List<String> pageOrder = Arrays.asList(line.split(","));
            //System.out.println("Checking: " + pageOrder);
            final int middlePageValue = getValidMiddlePage(pageOrder);
            //System.out.println("Result: " + middlePageValue);
            middleNumberSum += middlePageValue;
        }
    }

    private int getValidMiddlePage(final List<String> pageOrder) {
        for (int i = 1; i < pageOrder.size(); i++) {
            final String currentPage = pageOrder.get(i);
            if(precedenceMap.containsKey(currentPage)) {
                final Set<String> followingPages = precedenceMap.get(currentPage);
                for(int j = 0; j < i; j++) {
                    final String previousPage = pageOrder.get(j);
                    if(followingPages.contains(previousPage)) {
                        return 0;
                    }
                }
            }
        }
        return Integer.parseInt(pageOrder.get((pageOrder.size()-1)/2));
    }

}
