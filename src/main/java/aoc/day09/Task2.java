package aoc.day09;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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



            //printFiles(files);

            File lastFilePointer = getLastFile(files.length-1, files);

            while(lastFilePointer.startIndex > 0) {
                //System.out.println("LastFile: " + lastFilePointer);
                Optional<File> maybeSpace = findSpace(lastFilePointer, files);
                if(maybeSpace.isPresent()) {
                    final File space = maybeSpace.get();
                   // System.out.println("Space: " + space);
                    for(int i = 0; i < space.getSize(); i++){
                        //System.out.println(lastFilePointer.startIndex + i);
                        files[space.startIndex + i] = files[lastFilePointer.startIndex + i];
                        files[lastFilePointer.startIndex + i] = null;
                    }
                }
                //printFiles(files);
                File nextLastFile = getLastFile(lastFilePointer.startIndex-1, files);
                if(nextLastFile.equals(lastFilePointer)) {
                    break;
                }
                lastFilePointer = nextLastFile;
            }







            //printFiles(files);



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

    Optional<File> findSpace(final File lastFile, final Integer[] files) {
        int currentSpacePointer = 0;
        while(currentSpacePointer < lastFile.startIndex) {
            final Optional<File> maybeNextSpace = getNextSpace(currentSpacePointer, lastFile.startIndex, files);
            if(maybeNextSpace.isEmpty()) {
                return Optional.empty();
            }
            final File nextSpace = maybeNextSpace.get();
            if(nextSpace.getSize() >= lastFile.getSize()) {
                return Optional.of(new File(null, nextSpace.startIndex, nextSpace.startIndex + lastFile.getSize() - 1));
            }
            currentSpacePointer = nextSpace.endIndex + 1;
        }

        return Optional.empty();
    }

    Optional<File> getNextSpace(int currentSpacePointer, int pointerLimit, final Integer[] files) {
        int nextSpaceStart = currentSpacePointer;
        for(int i = currentSpacePointer; i < pointerLimit; i++) {
            if(files[i] == null) {
                nextSpaceStart = i;
                break;
            }
        }

        if(files[nextSpaceStart] != null) {
            return Optional.empty();
        }

        int nextSpaceEnd = nextSpaceStart;
        do{
            nextSpaceEnd++;
        } while(files[nextSpaceEnd] == null);
        final File someSpace = new File(null, nextSpaceStart, nextSpaceEnd-1);
        //System.out.println("Some Space: " + someSpace);
        return Optional.of(someSpace);
    }

    File getLastFile(int currentLastFilePointer, final Integer[] files) {
        int lastFileEnd = currentLastFilePointer;
        for(int i = currentLastFilePointer; i >= 0; i--) {
            if(files[i] != null) {
                lastFileEnd = i;
                break;
            }
        }
        int lastFileId = files[lastFileEnd];
        int lastFileStart = lastFileEnd;
        while(files[lastFileStart] != null && files[lastFileStart] == lastFileId && lastFileStart > 0){
            lastFileStart--;
        }
        return new File(lastFileId, lastFileStart+1, lastFileEnd);
    }

    void printFiles(Integer[] files) {
        for (final Integer file : files) {
            if(file == null) {
                System.out.print(".");
            } else {
                System.out.print(file);
            }
        }
        System.out.println();
    }

    static class File {
        private final Integer id;
        private final int startIndex;
        private final int endIndex;

        File(final Integer id, final int startIndex, final int endIndex) {
            this.id = id;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public Integer getId() {
            return id;
        }

        public boolean isSpace() {
            return id == null;
        }

        public boolean isFile() {
            return id != null;
        }

        public int getSize() {
            return endIndex - startIndex + 1;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final File file = (File) o;
            return startIndex == file.startIndex && endIndex == file.endIndex && Objects.equals(id, file.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, startIndex, endIndex);
        }

        @Override
        public String toString() {
            return "File{" +
                    "id=" + id +
                    ", startIndex=" + startIndex +
                    ", endIndex=" + endIndex +
                    '}';
        }
    }
}
