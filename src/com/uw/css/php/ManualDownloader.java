package com.uw.css.php;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ManualDownloader {
    public static String BASE_URL="https://www.php.net/manual/en/"; //Starting URL to start scraping
    public static String DOCUMENTATION_DIR="./output/documentation/php/"; //Where .txt files go
    public static StringBuilder allTextContent = new StringBuilder(); //Contains all the text for the product, appended as more docs are found

    public static void main(String[] args) {
        getPackagesList();
    }

    private static void getPackagesList() {
        Document doc;
        Integer count = 0;
        Integer failed = 0;
        try {
            doc = Jsoup.connect(BASE_URL).get();  //connect to the starting URL
            Elements mainList = doc.select("ul[class=chunklist chunklist_set] > li > a:first-child"); //List of elements
            for(Element elem1: mainList){
                try{
                    String url = elem1.attr("href"); //linkName.php
                    String nextQuery = url.replaceAll("\\.php$", ""); //NextQuery is always the HTML element of the next nested list
                    Document doc2 = Jsoup.connect(BASE_URL + url).get();
                    Elements secList = doc2.select("div[id=" + nextQuery + "] > ul > li > a");
                    if (!secList.isEmpty()) {
                        for (Element elem2: secList) {
                            url = elem2.attr("href");
                            nextQuery = url.replaceAll("\\.php$", "");
                            Document doc3 = Jsoup.connect(BASE_URL + url).get();
                            Elements thirdList = doc3.select("div[id=" + nextQuery + "] > ul > li > a, div[id=" + nextQuery + "] > div > ul > li > a");
                            if (!thirdList.isEmpty()) {
                                for (Element elem3: thirdList) {
                                    url = elem3.attr("href");
                                    nextQuery = url.replaceAll("\\.php$", "");
                                    Document doc4 = Jsoup.connect(BASE_URL + url).get();
                                    Elements fourthList = doc4.select("div[id=" + nextQuery + "] > ul > li > a, div[id=" + nextQuery + "] > div > ul > li > a");
                                    if (!fourthList.isEmpty()) {
                                        for (Element elem4: fourthList) {
                                            url = elem4.attr("href");
                                            nextQuery = url.replaceAll("\\.php$", "");
                                            Document doc5 = Jsoup.connect(BASE_URL + url).get();
                                            Elements fifthList = doc5.select("div[id=" + nextQuery + "] > ul > li > a, div[id=" + nextQuery + "] > div > ul > li > a");
                                            if (!fifthList.isEmpty()) {
                                                for (Element elem5: fifthList) {
                                                    url = elem5.attr("href");
                                                    nextQuery = url.replaceAll("\\.php$", "");
                                                    Document doc6 = Jsoup.connect(BASE_URL + url).get();
                                                    Elements sixthList = doc6.select("div[id=" + nextQuery + "] > ul > li > a, div[id=" + nextQuery + "] > div > ul > li > a");
                                                    if (!sixthList.isEmpty()) {
                                                        for (Element elem6: sixthList) {
                                                            url = elem6.attr("href");
                                                            nextQuery = url.replaceAll("\\.php$", "");
                                                            System.out.println("*****"+sanitizeProductName(elem6.text())+"*****");
                                                            String doctext = Jsoup.connect(BASE_URL + url).get().select("div[id=" + nextQuery + "]").get(0).text();
                                                            allTextContent.append(doctext).append("\n").append("\n");
                                                            count++;
                                                        }
                                                    }
                                                    else {
                                                        System.out.println("*****"+sanitizeProductName(elem5.text())+"*****");
                                                        String doctext = Jsoup.connect(BASE_URL + url).get().select("div[id=" + nextQuery + "]").get(0).text();
                                                        allTextContent.append(doctext).append("\n").append("\n");
                                                        count++;
                                                    }

                                                }
                                            }
                                            else {
                                                System.out.println("*****"+sanitizeProductName(elem4.text())+"*****");
                                                String doctext = Jsoup.connect(BASE_URL + url).get().select("div[id=" + nextQuery + "]").get(0).text();
                                                allTextContent.append(doctext).append("\n").append("\n");
                                                count++;
                                            }
                                        }
                                    }
                                    else {
                                        System.out.println("*****"+sanitizeProductName(elem3.text())+"*****");
                                        String doctext = Jsoup.connect(BASE_URL + url).get().select("div[id=" + nextQuery + "]").get(0).text();
                                        allTextContent.append(doctext).append("\n").append("\n");
                                        count++;
                                    }
                                }
                            }
                            else {
                                System.out.println("*****"+sanitizeProductName(elem2.text())+"*****");
                                String doctext = Jsoup.connect(BASE_URL + url).get().select("div[id=" + nextQuery + "]").get(0).text();
                                allTextContent.append(doctext).append("\n").append("\n");
                                count++;
                            }
                        }
                    }
                    else {
                        System.out.println("*****"+sanitizeProductName(elem1.text())+"*****");
                        String doctext = Jsoup.connect(BASE_URL + url).get().select("div[id=legalnotice]").get(0).text();
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
            failed+=1;
        }
        System.out.println("Downloaded "+count);
        System.out.println("Failed "+failed);
    }

    public static String sanitizeProductName(String s){  //Changes string characters that cannot go into a .txt file name
        return s.replaceAll("\\?","").replaceAll("/", "_").replaceAll(":", "_");
    }

    public static void exportTextContentToTxtFile() throws IOException { //create .txt files
        Files.createDirectories(Paths.get("./output/documentation/"));
        File directory = new File(DOCUMENTATION_DIR);
        if (! directory.exists()){
            directory.mkdir();
        }
        PrintWriter out = new PrintWriter(new FileWriter(DOCUMENTATION_DIR+"php.txt"));
        out.println(allTextContent.toString());
        out.close();
    }
}
