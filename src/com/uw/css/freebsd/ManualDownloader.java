package com.uw.css.freebsd;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ManualDownloader {
    public static String BASE_URL="https://docs.freebsd.org/en/books/";
    public static String DOCUMENTATION_DIR="./output/documentation/freebsd/";
    public static StringBuilder allTextContent = new StringBuilder(); //Contains all the text for the product, appended as more docs are found

    public static void main(String[] args) {
        getPackagesList();
    }

    private static void getPackagesList() {
        Document doc;
        Integer count = 0;
        Integer failed = 0;
        try {
            //Get Document object after parsing the html from given url.
            doc = Jsoup.connect(BASE_URL).get();
//            Elements elements = doc.select("div[class=book-menu-content] li label + a");
            Elements elements = doc.select("ul[class=documents-list] a");
            for(Element elem1: elements) {
                try {
                    String url = elem1.attr("href");
                    System.out.println("*****" + elem1.text() + "*****");
                    if (url.contains("faq") || url.contains("design-44bsd") || url.contains("dev-model")) {
                        String doctext = Jsoup.connect(url).get().select("div[class=book-content]").get(0).text();
                        allTextContent.append(doctext).append("\n").append("\n");
                        count++;
                    } else {
                        Document doc2 = Jsoup.connect(url).get();
                        Elements elements2 = doc2.select("div[class=book] a[href=book/]");
                        for (Element elem2 : elements2) {
                            String url2 = elem2.attr("href");
                            String doctext = Jsoup.connect(url + url2).get().select("div[class=book-content]").get(0).text();
                            allTextContent.append(doctext).append("\n").append("\n");
                            count++;
                        }
                    }
//                    Document doc2 = Jsoup.connect(url).get();
//                    Elements elements2 = doc2.select("div[class=book-menu-content] li label + a");
//                    if (!elements2.isEmpty()) {
//                        for (Element elem2 : elements2) {
//                            url = elem2.attr("href");
//                            String productName = sanitizeProductName(elem2.text());
//                            System.out.println("*****" + productName + "*****");
//                            String doctext = Jsoup.connect(url).get().select("div[class=book-content]").get(0).text();
//                            exportTextContentToTxtFile(doctext, productName);
//                            count++;
//                        }
//                    } else {
//                        String productName = sanitizeProductName(elem1.text());
//                        System.out.println("*****" + productName + "*****");
//                        String doctext = Jsoup.connect(url).get().select("div[class=book-content]").get(0).text();
//                        exportTextContentToTxtFile(doctext, productName);
//                        count++;
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    failed += 1;
                }
            }
            exportTextContentToTxtFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Downloaded "+count);
        System.out.println("Failed "+failed);
    }

    public static String sanitizeProductName(String s){
        return s.replaceAll("/","_").replaceAll("\\*", "_").replaceAll("-", "_");
    }

    public static void exportTextContentToTxtFile() throws IOException {
        Files.createDirectories(Paths.get("./output/documentation/"));
        File directory = new File(DOCUMENTATION_DIR);
        if (! directory.exists()){
            directory.mkdir();
        }
        PrintWriter out = new PrintWriter(new FileWriter(DOCUMENTATION_DIR+"freebsd.txt"));
        out.println(allTextContent.toString());
        out.close();
    }
}
