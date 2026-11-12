package org.gratidaosempre.controller;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CertificateGenerator {
    private Path namesDirectory = Path.of(".out", "names");
    private Path certificatesDirectory;
    private final String local;
    private final String date;
    private final String namesFileName;

    public CertificateGenerator(String local, String date, String namesFileName) {
        if (local == null || local.isBlank()) {
            throw new IllegalArgumentException("Informe um local não vazio.");
        }
        if (!Validator.dateTime(date)) {
            throw new IllegalArgumentException("Informe uma data válida no formato dd/MM/aaaa ou dd-MM-aaaa.");
        }
        if (namesFileName == null || namesFileName.isBlank()) {
            throw new IllegalArgumentException("Informe o arquivo de nomes.");
        }
        this.local = local.trim();
        this.date = date;
        this.namesFileName = namesFileName;
        certificatesDirectory = Path.of(".out", "certificates", fileComponent(this.local),
                Sanitizer.replaceBar(date).replace('-', '_'));
    }

    public void setCertificatesDirectory(String certificatesDirectory) {
        this.certificatesDirectory = Path.of(certificatesDirectory);
    }

    public void setNamesDirectory(String namesDirectory) {
        this.namesDirectory = Path.of(namesDirectory);
    }

    public ArrayList<String> loadNames(String fileName) throws IOException {
        List<String> lines = Files.readAllLines(namesDirectory.resolve(fileName), StandardCharsets.UTF_8);
        ArrayList<String> names = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (i == 0 && line.startsWith("\uFEFF")) {
                line = line.substring(1);
            }
            String name = line.strip();
            if (!name.isEmpty()) {
                names.add(name);
            }
        }
        if (names.isEmpty()) {
            throw new IllegalArgumentException("O arquivo não contém nomes.");
        }
        return names;
    }

    public int generate() throws IOException {
        List<String> names = loadNames(namesFileName);
        generateCertificates(names);
        return names.size();
    }

    public void generateCertificates(List<String> names) throws IOException {
        List<Path> destinations = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            Path destination = certificatesDirectory.resolve(String.format(Locale.ROOT, "%04d_%s.pdf",
                    i + 1, fileComponent(names.get(i))));
            if (Files.exists(destination)) {
                throw new IOException("O arquivo já existe: " + destination);
            }
            destinations.add(destination);
        }
        Files.createDirectories(certificatesDirectory);
        for (int i = 0; i < names.size(); i++) {
            try (OutputStream output = Files.newOutputStream(destinations.get(i),
                    StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
                 PdfWriter writer = new PdfWriter(output);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {
                document.add(new Paragraph(names.get(i)));
                document.add(new Paragraph(local));
                document.add(new Paragraph(date));
            }
        }
    }

    private static String fileComponent(String value) {
        String component = Sanitizer.replaceSpace(value).replaceAll("[\\\\/:*?\"<>|\\p{Cntrl}]", "_")
                .replaceAll("[. ]+$", "_");
        return component.isBlank() ? "_" : component;
    }
}
