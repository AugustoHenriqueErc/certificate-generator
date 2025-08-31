package org.gratidaosempre.controller;

import com.itextpdf.kernel.pdf.PdfDocument;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class CertificateGenerator {
private static String NAMES_DIRECTORY ;
private static String CERTIFICATES_DIRECTORY;
private String local;
private String date;
    public CertificateGenerator(String local, String date, String fileName) {
        this.local = local;
        this.date = date;
        NAMES_DIRECTORY = "./names";
        CERTIFICATES_DIRECTORY = "./certificates/" + Sanitizer.replaceSpace(local) + "/" + Sanitizer.replaceBar(date);
        try{
            generateCertificates(loadNames(fileName));
        }catch (IOException e) {
            System.err.println("CANNOT READ FILE" + fileName + " CANNOT GENERATE CERTIFICATES");
        }
    }
     private void addDate(String name) {

    }
    private void addLocal(String local) {

    }

    private void addName(String name) {

    }
    //Returns List of names within the names folder
    public static List<String> loadNames(String fileName) throws IOException {
        File file = new File(NAMES_DIRECTORY + "/" + fileName);
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

    public static void generateCertificates(List<String> names){
        File directory = new File(CERTIFICATES_DIRECTORY);


    }

}
