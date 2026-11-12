package org.gratidaosempre.controller;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class CertificateGeneratorTest {
    @TempDir
    Path directory;

    private CertificateGenerator generator(String contents) throws IOException {
        Path input = directory.resolve("input");
        Files.createDirectories(input);
        Files.writeString(input.resolve("names.txt"), contents, StandardCharsets.UTF_8);
        CertificateGenerator generator = new CertificateGenerator("Belo Horizonte", "04/11/2026", "names.txt");
        generator.setNamesDirectory(input.toString());
        generator.setCertificatesDirectory(directory.resolve("output").toString());
        return generator;
    }

    @Test
    void generatesIndependentPdfsWithConfiguredDirectoriesAndUtf8Names() throws IOException {
        CertificateGenerator generator = generator("\uFEFF Ana Cláudia \r\n\r\nJoão Pedro\r\nAna Cláudia\r\n");
        assertFalse(Files.exists(directory.resolve("output")));
        assertEquals(3, generator.generate());
        String[] files = {"0001_Ana_Cláudia.pdf", "0002_João_Pedro.pdf", "0003_Ana_Cláudia.pdf"};
        String[] names = {"Ana Cláudia", "João Pedro", "Ana Cláudia"};
        for (int i = 0; i < files.length; i++) {
            try (PdfDocument pdf = new PdfDocument(new PdfReader(directory.resolve("output").resolve(files[i]).toString()))) {
                assertEquals(1, pdf.getNumberOfPages());
                String text = PdfTextExtractor.getTextFromPage(pdf.getPage(1));
                assertTrue(text.contains(names[i]));
                assertTrue(text.contains("Belo Horizonte"));
                assertTrue(text.contains("04/11/2026"));
            }
        }
    }

    @Test
    void rejectsExistingDestinationBeforeWritingAnyPdf() throws IOException {
        CertificateGenerator generator = generator("Ana\nJoão");
        Path output = directory.resolve("output");
        Files.createDirectories(output);
        Path existing = output.resolve("0002_João.pdf");
        Files.writeString(existing, "original");
        assertThrows(IOException.class, generator::generate);
        assertEquals("original", Files.readString(existing));
        assertFalse(Files.exists(output.resolve("0001_Ana.pdf")));
    }

    @Test
    void rejectsEmptyInputWithoutCreatingOutput() throws IOException {
        CertificateGenerator generator = generator("\uFEFF\n \n");
        assertThrows(IllegalArgumentException.class, generator::generate);
        assertFalse(Files.exists(directory.resolve("output")));
    }

    @Test
    void missingInputDoesNotCreateInputOrOutputDirectory() {
        CertificateGenerator generator = new CertificateGenerator("Local", "04-11-2026", "missing.txt");
        generator.setNamesDirectory(directory.resolve("missing").toString());
        generator.setCertificatesDirectory(directory.resolve("output").toString());
        assertThrows(IOException.class, generator::generate);
        assertFalse(Files.exists(directory.resolve("missing")));
        assertFalse(Files.exists(directory.resolve("output")));
    }

    @Test
    void configurationsAreIndependentAcrossInstances() throws IOException {
        CertificateGenerator first = generator("Ana");
        CertificateGenerator second = new CertificateGenerator("Outro local", "05-11-2026", "names.txt");
        second.setNamesDirectory(directory.resolve("missing").toString());
        second.setCertificatesDirectory(directory.resolve("other-output").toString());
        assertEquals(1, first.generate());
        assertFalse(Files.exists(directory.resolve("other-output")));
    }
}
