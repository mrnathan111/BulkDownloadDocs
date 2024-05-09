package com.uw.css.autodesk;

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
    public static String BASE_URL="https://help.autodesk.com/";
    public static String DOCUMENTATION_DIR="./output/documentation/autodesk/";

    public static void main(String[] args) {
        getPackagesList();
    }

    private static void getPackagesList() {
        Document doc;
        Integer count = 0;
        Integer failed = 0;
        try {
            doc = Jsoup.connect(BASE_URL).get();
            Elements elements = doc.select("div[id=ui-x-p-product-list] li > a:first-child");
            for(Element element: elements){
                try{
                    String url = element.attr("href");
                    String productName = sanitizeProductName(element.text());
                    System.out.println("*****"+productName+"*****");
                    String doctext = Jsoup.connect(BASE_URL + url).get().select("div[class=mw-bodytext]").get(0).text();
                    exportTextContentToTxtFile(doctext, productName);
                    count+=1;
                }catch (Exception e){
                    e.printStackTrace();
                    failed+=1;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        System.out.println("Downloaded "+count);
        System.out.println("Failed "+failed);
    }

    public static String sanitizeProductName(String s){
        return s.replaceAll("/","_");
    }

    public static void exportTextContentToTxtFile(String text,String product) throws IOException {
        Files.createDirectories(Paths.get("./output/documentation/"));
        File directory = new File(DOCUMENTATION_DIR);
        if (! directory.exists()){
            directory.mkdir();
        }
        PrintWriter out = new PrintWriter(new FileWriter(DOCUMENTATION_DIR+product+".txt"));
        out.println(text);
        out.close();
    }

    public static void exportContentToTxtFile(String manualUrl,String product) throws IOException {
        Files.createDirectories(Paths.get("./output/documentation/"));
        File directory = new File(DOCUMENTATION_DIR);
        if (! directory.exists()){
            directory.mkdir();
        }
        InputStream inputStream = null;
        try {
            inputStream = new URL(manualUrl).openStream();
            Files.copy(inputStream, Paths.get(DOCUMENTATION_DIR+product+".txt"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
