package com.uw.css.seamonkey;

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
    public static String BASE_URL = "https://www.seamonkey-project.org/doc/";//Starting URL to start scraping
    public static String DOCUMENTATION_DIR = "./output/documentation/seamonkey/"; //Where .txt files go
    public static StringBuilder allTextContent = new StringBuilder(); //Contains all the text for the product, appended as more docs are found

    public static void main(String[] args) {
        getPackagesList();
    }

    private static void getPackagesList() {
        Document doc;
        Integer count = 0;
        Integer failed = 0;
        try {
            doc = Jsoup.connect(BASE_URL).get(); //connect to the starting URL
            Elements elements = doc.select("div[id=mainContent] a");
            for (Element element : elements) { //list of url elements
                try {
                    String url = element.attr("href");
                    if (!url.contains("community") && !url.contains("#") && !url.contains("http") && !url.contains("2.0") && !url.contains("1.1")) { //deny links that aren't documentation
                        String doctext = Jsoup.connect(BASE_URL + url).get().select("div[id=mainContent]").get(0).text();
                        allTextContent.append(doctext).append("\n").append("\n");
                        count += 1;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    failed += 1;
                }
            }
            exportTextContentToTxtFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Downloaded " + count);
        System.out.println("Failed " + failed);
    }

    public static void exportTextContentToTxtFile() throws IOException { //create .txt files
        Files.createDirectories(Paths.get("./output/documentation/"));
        File directory = new File(DOCUMENTATION_DIR);
        if (!directory.exists()) {
            directory.mkdir();
        }
        PrintWriter out = new PrintWriter(new FileWriter(DOCUMENTATION_DIR + "seamonkey.txt"));
        out.println(allTextContent.toString());
        out.close();
    }
}