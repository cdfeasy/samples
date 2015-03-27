package spark;

import org.eclipse.jetty.io.UncheckedIOException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by d.asadullin on 05.03.2015.
 */
public class CsvReader {
    private Reader source;

    public CsvReader(Reader source) {
        this.source = source;
    }

    List<List<String>> readRecords() {
        try (BufferedReader reader = new BufferedReader(source)) {
            final AtomicInteger i = new AtomicInteger();
            return reader.lines().filter(line -> i.incrementAndGet() % 2 == 1)
                    .map(line -> Arrays.asList(line.split(";")))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static long readAllLineFromAllFilesRecursively(String path, String extension) {
        final AtomicLong size=new AtomicLong(0);
        try (final Stream<Path> pathStream = Files.walk(Paths.get(path), FileVisitOption.FOLLOW_LINKS)) {
            pathStream
                    .filter((p) -> !p.toFile().isDirectory() && p.toFile().getAbsolutePath().endsWith(extension))
                    .forEach(p -> size.addAndGet(fileLinesToList(p)));

        } catch (final IOException e) {
        }
        return size.longValue();
    }

    private static long fileLinesToList(final Path file) {
        try (Stream<String> stream = Files.lines(file, Charset.defaultCharset())) {
            return stream
                    .map(String::trim)
                    .filter(s -> !s.isEmpty()).count();
        } catch (final IOException e) {
        }
        return 0;
    }


    public static void main(String[] args) {
//        String s = "bla1;bla2;bla3;\n1;2;3\n4;5;6\n7;8;9\n10,11,12";
//
//        StringReader sr = new StringReader(s);
//        CsvReader csvReader = new CsvReader(sr);
//        System.out.println(csvReader.readRecords());

      //  List<String> s=readAllLineFromAllFilesRecursively("C:\\work\\ibs-git","java");
        System.out.println(readAllLineFromAllFilesRecursively("C:\\work\\ibs-git","java"));

    }

}
