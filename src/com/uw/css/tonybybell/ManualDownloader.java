package com.uw.css.tonybybell;

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
    public static String BASE_URL="https://gtkwave.sourceforge.net"; //Starting URL to start scraping
    public static String DOCUMENTATION_DIR="./output/documentation/tonybybell/"; //Where .txt files go

    public static void main(String[] args) {
        getPackagesList();
    }

    private static void getPackagesList() {
        Document doc;
        Integer count = 0;
        Integer failed = 0;
        try {
            doc = Jsoup.connect(BASE_URL).get(); //connect to the starting URL
            Elements elements = doc.select("p a"); //List of links
            for(Element element: elements){
                if (elements.indexOf(element) == 2){ //Only get the 3rd link (where the product docs are
                    String url = element.attr("href");
                    String productName = "GTKWave"; //Manually created productName
                    System.out.println("*****"+productName+"*****");
                    String pdfUrl = url.replaceFirst(".", BASE_URL); //Filled URL
                    exportContentToTxtFile(pdfUrl, productName);
                    count+=1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            failed+=1;
        }
        System.out.println("Downloaded "+count);
        System.out.println("Failed "+failed);
    }

    public static String sanitizeProductName(String s){ //Changes string characters that cannot go into a .txt file name
        return s.replaceAll("/","_");
    }

    public static void exportContentToTxtFile(String manualUrl,String product) throws IOException { //creates pdf file
        Files.createDirectories(Paths.get("./output/documentation/"));
        File directory = new File(DOCUMENTATION_DIR);
        if (! directory.exists()){
            directory.mkdir();
        }
        InputStream inputStream = null;
        try {
            inputStream = new URL(manualUrl).openStream();
            Files.copy(inputStream, Paths.get(DOCUMENTATION_DIR+product+".pdf"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
