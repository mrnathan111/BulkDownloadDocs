package com.uw.css.moodle;

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
    public static String MOODLE_URL="https://docs.moodle.org"; //Starting URL to start scraping
    public static String BASE_URL="https://docs.moodle.org/403/en/Main_page"; //URL for later url strings to build on
    public static String DOCUMENTATION_DIR="./output/documentation/moodle/";  //Where .txt files go
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
            Elements elements = doc.select("div[class=card-footer] a");
            for(Element element: elements){ //list of link elements
                try{
                    String url = MOODLE_URL + element.attr("href");
                    Document doc2 = Jsoup.connect(url).get();
                    Elements elements1 = doc2.select("div[class='mw-category mw-category-columns'] a");
                    for (Element elem1: elements1) { //nested list of links
                        url = MOODLE_URL + elem1.attr("href");
                        System.out.println("*****"+elem1.text()+"*****");
                        String doctext = Jsoup.connect(url).get().select("div[id=mw-content-text]").get(0).text();
                        allTextContent.append(doctext).append("\n").append("\n");
                        count++;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    failed+=1;
                }
            }
            exportTextContentToTxtFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Downloaded "+count);
        System.out.println("Failed "+failed);
    }

    public static String sanitizeProductName(String s){ //Changes string characters that cannot go into a .txt file name
        return s.replaceAll("/","_");
    }

    public static void exportTextContentToTxtFile() throws IOException { //creates .txt files
        Files.createDirectories(Paths.get("./output/documentation/"));
        File directory = new File(DOCUMENTATION_DIR);
        if (! directory.exists()){
            directory.mkdir();
        }
        PrintWriter out = new PrintWriter(new FileWriter(DOCUMENTATION_DIR+"moodle.txt"));
        out.println(allTextContent.toString());
        out.close();
    }
}
