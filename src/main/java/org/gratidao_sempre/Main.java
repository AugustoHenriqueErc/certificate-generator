package org.gratidao_sempre;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.io.*;

public class Main {
    public static final String DEST = "/home/augusto-henrique/Documents/certificado_digital_vailton/certificateGenerator/pdfs/test_Itextpdf";

    public static void main(String args[]) throws IOException {
        PdfDocument pdf = new PdfDocument(new PdfWriter(DEST));
        Document document = new Document(pdf);
        String line = "Hello! Welcome to iTextPdf";
        document.add(new Paragraph(line));
        document.close();

        System.out.println("Awesome PDF just got created.");
    }
}