package com.uw.css.gnu;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ManualDownloader {
    public static String GNU_URL="https://www.gnu.org/manual/manual.html";
    public static String BASE_URL="https://www.gnu.org";
    public static String DOCUMENTATION_DIR="./output/documentation/gnu/";

    public static void main(String[] args) {
        getPackagesList();
    }

    private static void getPackagesList() {
        Document doc;
        Integer count = 0;
        Integer failed = 0;
        try {
            //Get Document object after parsing the html from given url.
            doc = Jsoup.connect(GNU_URL).get();
            Elements elements = doc.select("div[class=category] dt a");
            for(Element element: elements){
                String productName = sanitizeProductName(element.text());
                String url = element.attr("href");
                System.out.println("*****" + productName + "*****");
                try {
                    Document documentation = Jsoup.connect(BASE_URL + url).get();
                    Elements links = documentation.getElementsByTag("a");
                    for(Element link:links){
                        String linkUrl = link.attr("href");
                        if(linkUrl.endsWith(".txt")){
                            exportContentToTxtFile(BASE_URL+url+linkUrl,productName);
                        }
                    }
                    count+=1;
                }catch (Exception e){
                    e.printStackTrace();
                    failed+=1;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Downloaded "+count);
        System.out.println("Failed "+failed);
    }

    public static String sanitizeProductName(String s){
        return s.replaceAll(" ","_");
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
        InputStream inputStream = null;
        try {
            inputStream = new URL(manualUrl).openStream();
            Files.copy(inputStream, Paths.get(DOCUMENTATION_DIR+product+".txt"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
