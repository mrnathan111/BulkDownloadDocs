package com.uw.css.fortinet;

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
    public static String BASE_URL="https://docs.fortinet.com";//Starting URL to start scraping
    public static String DOCUMENTATION_DIR="./output/documentation/fortinet/"; //Where .txt files go
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
            Elements elements1 = doc.select("div[class=home-quicklinks product-quicklinks] a");
            for(Element elem1: elements1){ //main list of links
                try{
                    String url = BASE_URL + elem1.attr("href");
                    String productName = sanitizeProductName(elem1.text());
                    System.out.println("*****"+productName+"*****");
                    Document doc2 = Jsoup.connect(url).get();
                    Elements elements2 = doc2.select("div[class=product-card-title] a:first-child");
                    for (Element elem2: elements2) { //nested list of links
                        try {
                            url = BASE_URL + elem2.attr("href");
//                            String guideName = sanitizeProductName(elem2.text());
//                            System.out.println("//////"+guideName+"//////");
                            Document doc3 = Jsoup.connect(url).get();
                            Elements element3 = doc3.select("div[class=contents py-md-2] li[class*=leaf] > a");
                            for (Element elem3 : element3) { //last nested list of links
                                try {
                                        url = BASE_URL + elem3.attr("href");
                                        String doctext = Jsoup.connect(url).get().select("div[id=content] div[role=main]").get(0).text();
                                        allTextContent.append(doctext).append("\n").append("\n");
                                        exportTextContentToTxtFile(productName);
                                        count++;
                                        System.out.println(productName);
                                        System.out.println(count);
                                } catch (Exception e){
                                    e.printStackTrace();
                                    failed+=1;
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Downloaded "+count);
        System.out.println("Failed "+failed);
    }

    public static String sanitizeProductName(String s){ //Changes string characters that cannot go into a .txt file name
        return s.replaceAll("/","_").replaceAll("\\?", "_");
    }

    public static void exportTextContentToTxtFile(String product) throws IOException { //creates .txt files
        Files.createDirectories(Paths.get("./output/documentation/"));
        File directory = new File(DOCUMENTATION_DIR);
        if (! directory.exists()){
            directory.mkdir();
        }
        PrintWriter out = new PrintWriter(new FileWriter(DOCUMENTATION_DIR+product+".txt"));
        out.println(allTextContent.toString());
        out.close();
    }
}