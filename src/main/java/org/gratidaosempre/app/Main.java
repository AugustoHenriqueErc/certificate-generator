package org.gratidaosempre.app;

import com.itextpdf.kernel.geom.Path;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.PaddingShorthandResolver;
import org.gratidaosempre.controller.CertificateGenerator;


public class Main {
    public static void main(String  []args) {
        String local, date;
        Path names;


        CertificateGenerator certificateGenerator = new CertificateGenerator(local, date, "names");

    }
}