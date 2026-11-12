package org.gratidaosempre.app;

import org.gratidaosempre.controller.CertificateGenerator;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        System.exit(run(args));
    }

    public static int run(String[] args) {
        if (args.length == 1 && args[0].equals("--help")) {
            printUsage();
            return 0;
        }
        if (args.length < 3 || args.length > 5) {
            printUsage();
            return 2;
        }
        try {
            CertificateGenerator generator = new CertificateGenerator(args[0], args[1], args[2]);
            if (args.length >= 4) {
                generator.setNamesDirectory(args[3]);
            }
            if (args.length == 5) {
                generator.setCertificatesDirectory(args[4]);
            }
            int count = generator.generate();
            System.out.println(count + " certificados gerados.");
            return 0;
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return 2;
        } catch (IOException e) {
            System.err.println("Falha ao gerar certificados: " + e.getMessage());
            return 1;
        }
    }

    private static void printUsage() {
        System.out.println("Uso: <local> <data dd/MM/aaaa ou dd-MM-aaaa> <arquivo de nomes> "
                + "[diretório de nomes] [diretório de saída]");
    }
}
