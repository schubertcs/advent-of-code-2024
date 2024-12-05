package aoc.day05;

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
import java.util.List;
import java.util.Map;
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
            for (final Map.Entry<String, Set<String>> stringSetEntry : precedenceMap.entrySet()) {
                System.out.println(stringSetEntry.getKey() + "->" + stringSetEntry.getValue() );
            }
            rulesSection = false;
        } else if(rulesSection) {
            final String[] split = line.split("\\|");
            precedenceMap.computeIfAbsent(split[0], k -> new HashSet<>()).add(split[1]);
        } else {
            final List<String> pageOrder = Arrays.asList(line.split(","));
            final Optional<Integer> maybeMiddlePageNumber = getValidMiddlePage(pageOrder);
            if(maybeMiddlePageNumber.isEmpty()) {
                System.out.println("Rechecking: " + pageOrder);
                final int correctedMiddlePage = getCorrectedMiddlePage(pageOrder);
                System.out.println("Corrected: " + correctedMiddlePage);
                middleNumberSum += correctedMiddlePage;
            }
            //System.out.println("Result: " + middlePageValue);
        }
    }

    private int getCorrectedMiddlePage(final List<String> pageOrder) {
        final ArrayList<String> copy = new ArrayList<>(pageOrder);
        copy.sort((a, b) -> {
            if(precedenceMap.getOrDefault(a, Collections.emptySet()).contains(b)) {
                return -1;
            }
            if(precedenceMap.getOrDefault(b, Collections.emptySet()).contains(a)) {
                return 1;
            }
            return 0;
        });
        System.out.println("New Order: " + copy);
        return Integer.parseInt(copy.get((copy.size()-1)/2));
    }

    private Optional<Integer> getValidMiddlePage(final List<String> pageOrder) {
        for (int i = 1; i < pageOrder.size(); i++) {
            final String currentPage = pageOrder.get(i);
            if(precedenceMap.containsKey(currentPage)) {
                final Set<String> followingPages = precedenceMap.get(currentPage);
                for(int j = 0; j < i; j++) {
                    final String previousPage = pageOrder.get(j);
                    if(followingPages.contains(previousPage)) {
                        return Optional.empty();
                    }
                }
            }
        }
        return Optional.of(Integer.parseInt(pageOrder.get((pageOrder.size()-1)/2)));
    }

}
