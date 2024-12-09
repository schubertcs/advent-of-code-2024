package aoc.day09;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
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
                .getResource("day09/input1").toURI());

        try (final Stream<String> lines = Files.lines(path)) {
            final List<String> rows = lines.toList();
            final String row = rows.get(0);
            final List<Integer> entries = Arrays.stream(row.split("")).map(Integer::parseInt).toList();
            final int sum = entries.stream().mapToInt(i -> i).sum();
            final Integer[] files = new Integer[sum];

            int idCounter = 0;
            int filesCounter = 0;
            for (int i = 0; i < entries.size(); i++) {
                final int entry = entries.get(i);
                final Integer value;
                if(i % 2 == 0) {
                    // file
                    value = idCounter;
                    idCounter++;
                } else {
                    // space
                    value = null;
                }
                for (int j = 0; j < entry; j++) {
                    files[filesCounter + j] = value;
                }
                filesCounter += entry;
            }

            int spacePointer = getNextSpace(0, files);
            int lastFilePointer = getLastFile(files.length-1, files);

            while(spacePointer < lastFilePointer) {
                files[spacePointer] = files[lastFilePointer];
                files[lastFilePointer] = null;
                lastFilePointer = getLastFile(lastFilePointer, files);
                spacePointer = getNextSpace(spacePointer, files);
            }

            System.out.println(Arrays.toString(files));
            long checksum = 0;
            for (int i = 0; i < files.length; i++) {
                final Integer fileId = files[i];
                if(fileId != null) {
                    checksum += i * (long)fileId;
                }
            }

            System.out.println(checksum);
        }
    }

    int getNextSpace(int currentSpacePointer, final Integer[] files) {
        for(int i = currentSpacePointer; i < files.length; i++) {
            if(files[i] == null) {
                return i;
            }
        }
        return Integer.MAX_VALUE;
    }

    int getLastFile(int currentLastFilePointer, final Integer[] files) {
        for(int i = currentLastFilePointer; i >= 0; i--) {
            if(files[i] != null) {
                return i;
            }
        }
        return Integer.MIN_VALUE;
    }
}
