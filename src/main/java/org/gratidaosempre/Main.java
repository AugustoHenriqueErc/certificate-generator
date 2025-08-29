package org.gratidaosempre;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.io.*;

public class Main {
    public static final String DEST = "/home/augusto-henrique/Documents/certificado_digital_vailton/certificateGenerator/pdfs/test_Itextpdf/sample.pdf";
    public static PdfWriter pdfWriter ;
    public static PdfDocument pdf;

    public static void main(String args[]) throws IOException {
        pdfWriter = new PdfWriter(DEST);
        pdf = new PdfDocument(pdfWriter);
        Document document = new Document(pdf);
        String line1 = "Hello! This is the new PDF document sample!";
        String line2 = "This is the second line of the PDF document sample";
        document.add(new Paragraph(line1));
        document.add(new Paragraph(line2));
        document.close();

        System.out.println("Awesome PDF just got created.");
    }
}