package org.gratidaosempre;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.io.*;

public class Main {
    public static final String DEST = "./tests";
    public static PdfWriter pdfWriter ;
    public static PdfDocument pdf;

    public static void main(String args[]) throws IOException {
        //Creating multiple pdfs
        String[] names ={"Augusto", "Douglas", "Maria", "Matheus", "Gustavo"};
        pdfWriter = new PdfWriter(DEST);
        pdf = new PdfDocument(pdfWriter);
        for(int i = 0; i < names.length; i++) {
            Document document = new Document(pdf);
            document.add(new Paragraph(names[i]));
            document.close();
        }
    }
}