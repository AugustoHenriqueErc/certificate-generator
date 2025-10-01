package org.gratidaosempre.controller;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class CertificateGenerator {
    private static String NAMES_DIRECTORY;
    private static String CERTIFICATES_DIRECTORY;
    private String local;
    private String date;

    public CertificateGenerator(String local, String date, String namesFileName) {
        this.local = local;
        this.date = date;

        NAMES_DIRECTORY = ".out/names";
        CERTIFICATES_DIRECTORY = ".out/certificates/" + Sanitizer.replaceSpace(local) + "/" + Sanitizer.replaceBar(date);
        //Generate directories of doesn't exist
        new File(CERTIFICATES_DIRECTORY).mkdirs();
        new File(NAMES_DIRECTORY).mkdirs();
        try {
            generateCertificates(loadNames(namesFileName));
        } catch (IOException e) {
            System.err.println("CANNOT READ FILE " + namesFileName + " CANNOT GENERATE CERTIFICATES");
        }
    }

    private String getDate(){
        return date;
    }

    private String getLocal(){
        return local;
    }

    private void addDate(String name) {

    }

    private void addLocal(String local) {

    }

    private void addName(String name) {

    }

    //Returns List of names within the names folder
    public static ArrayList<String> loadNames(String fileName) throws IOException {
        File file = new File(NAMES_DIRECTORY + "/" + fileName);
        ArrayList<String> names = new ArrayList<>();

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

    public static void generateCertificates(List<String> names) {
        File directory = new File(CERTIFICATES_DIRECTORY);

        try {
            PdfWriter writer = new PdfWriter(directory);
            for (int i = 0; i < names.size(); i++) {
                PdfDocument pdf = new PdfDocument(writer);
                Document document = new Document(pdf);
                document.add(new Paragraph(names.get(i)));

                document.close();
                System.out.println("PDF gerado com sucesso em: " + CERTIFICATES_DIRECTORY);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
