package org.gratidaosempre.app;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    @TempDir
    Path directory;

    @Test
    void generatesFromCommandLineArguments() throws IOException {
        Files.writeString(directory.resolve("names.txt"), "Ana");
        Path output = directory.resolve("pdfs");
        assertEquals(0, Main.run(new String[]{"Belo Horizonte", "04/11/2026", "names.txt",
                directory.toString(), output.toString()}));
        assertTrue(Files.exists(output.resolve("0001_Ana.pdf")));
    }

    @Test
    void rejectsInvalidDateBeforeAccessingFiles() {
        assertEquals(2, Main.run(new String[]{"Local", "31/11/2026", "missing.txt"}));
    }

    @Test
    void reportsMissingFileAsIoFailure() {
        assertEquals(1, Main.run(new String[]{"Local", "04/11/2026", "missing.txt", directory.toString()}));
    }

    @Test
    void supportsHelpAndRejectsMissingArguments() {
        assertEquals(0, Main.run(new String[]{"--help"}));
        assertEquals(2, Main.run(new String[]{}));
    }
}
