package org.gratidaosempre.app;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.gratidaosempre.controller.CertificateGenerator;

import java.io.*;

public class Main {
    public static void main(String args[]) throws IOException {

        CertificateGenerator certificateGenerator = new CertificateGenerator("Belo Horizonte", "01/10/2025", "names");

    }
}