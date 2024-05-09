package com.uw.css.oracleCloud;

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
    public static String BASE_URL="https://docs.oracle.com/en-us"; //URL used to form later urls as a beginning of the string
    public static String START_URL="https://docs.oracle.com/en-us/iaas/Content/home.htm"; //Starting URL to start scraping
    public static String DOCUMENTATION_DIR="./output/documentation/oracleCloud/";  //Where .txt files go
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
            doc = Jsoup.connect(START_URL).get();  //connect to the starting URL
            Elements elements = doc.select("div[class='uk-grid uk-grid-large'] a");
            for(Element element: elements){ //List of url elements
                try{
                    if (element.attr("href").contains("iaas")) { //Docs are only under url's with iaas
                        String url = element.attr("href");
                        Document doc2;
                        doc2 = Jsoup.connect(BASE_URL + url).get();
                        url = BASE_URL + url;
                        Elements newElements = doc2.select("div[class='vl-content vl-content-max-width'] a");
                        for (Element elem: newElements){
                            String newUrl = elem.attr("href");
                            url = url.replaceFirst("index.html",newUrl);
                            System.out.println("*****"+elem.text()+"*****");
                            String doctext = Jsoup.connect(url).get().select("div[class='vl-content vl-content-max-width'] p").get(0).text();
                            allTextContent.append(doctext).append("\n").append("\n");
                            count++;
                        }
                }
                }catch (Exception e){
                    e.printStackTrace();
                    failed+=1;
                }
            }
            exportTextContentToTxtFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Downloaded "+count);
        System.out.println("Failed "+failed);
    }

    public static String sanitizeProductName(String s){  //Changes string characters that cannot go into a .txt file name
        return s.replaceAll("/","_").replaceAll("\\?", "");
    }

    public static void exportTextContentToTxtFile() throws IOException { //creates .txt files
        Files.createDirectories(Paths.get("./output/documentation/"));
        File directory = new File(DOCUMENTATION_DIR);
        if (! directory.exists()){
            directory.mkdir();
        }
        PrintWriter out = new PrintWriter(new FileWriter(DOCUMENTATION_DIR + "oracleCloud.txt"));
        out.println(allTextContent.toString());
        out.close();
    }
}
