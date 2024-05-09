package com.uw.css.cpanel;

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
    public static String BASE_URL="https://docs.cpanel.net/cpanel/";//Starting URL to start scraping
    public static String DOCUMENTATION_DIR="./output/documentation/cpanel/"; //Where .txt files go
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
            Elements elements = doc.select("div[class=see-all-btn] a");
            for(Element element: elements){ //list of link elements
                try{
                    String url = element.attr("href");
                    Document doc2;
                    doc2 = Jsoup.connect(url).get();
                    Elements realElements = doc2.select("div[class='container articles-single-list'] h4 a");
                    for (Element elem: realElements){ //nested list of links
                        String finalUrl = elem.attr("href");
                        System.out.println("*****"+sanitizeProductName(elem.text())+"*****");
                        String doctext = Jsoup.connect(finalUrl).get().select("div[class=col-md-9]").get(0).text();
                        allTextContent.append(doctext).append("\n").append("\n");
                        count+=1;
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

    public static String sanitizeProductName(String s){  //Changes string characters that cannot go into a .txt file name
        return s.replaceAll("/","_");
    }

    public static void exportTextContentToTxtFile() throws IOException { //creates .txt files
        Files.createDirectories(Paths.get("./output/documentation/"));
        File directory = new File(DOCUMENTATION_DIR);
        if (! directory.exists()){
            directory.mkdir();
        }
        PrintWriter out = new PrintWriter(new FileWriter(DOCUMENTATION_DIR+"cpanel.txt"));
        out.println(allTextContent.toString());
        out.close();
    }
}
