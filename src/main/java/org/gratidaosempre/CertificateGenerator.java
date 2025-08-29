package org.gratidaosempre;

import com.itextpdf.kernel.pdf.PdfDocument;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class CertificateGenerator {
private static final String NAMES_DIRECTORY = "./names";
private static final String CERTIFICATES_DIRECTORY = "./certificates";

    public static void addLocalDate(String LocalDate, PdfDocument pdfDocument) {

    }

    public static void addName(String LocalDate, PdfDocument pdfDocument) {

    }
    public static List<String> loadNames(String fileName) throws IOException {
        File file = new File(NAMES_DIRECTORY);
        List<String> names = new ArrayList<>();

        if (!file.canRead()) {
            throw new IOException("Cannot read file " + file.getName());
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                names.add(line);
            }
        } catch (IOException e) {
            throw new IOException("Failed to read the file " + e.getMessage());
        }
        return names;
    }

    public static void generateCertificates(){

    }

}
