package com.uw.css.ffmpeg;

import com.uw.css.cve.Product;
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
    public static String BASE_URL="http://ffmpeg.org/";
    public static String DOCUMENTATION_DIR="./output/documentation/ffmpeg/";

    public static void main(String[] args) {
        getPackagesList();
    }

    private static void getPackagesList() {
        Document doc;
        Integer count = 0;
        Integer failed = 0;
        try {
            //Get Document object after parsing the html from given url.
            doc = Jsoup.connect(BASE_URL+"documentation.html").get();
            Elements elements = doc.select("div[class=col-md-6] a");
            for(int i=0;i<22;i++){
                Element element = elements.get(i);
                String productName = element.text();
                String url = element.attr("href");
                try {
                    Document documentation = Jsoup.connect(BASE_URL + url).get();
                    System.out.println("*****"+productName+"*****");
                    String doctext = documentation.select("div[id=page-content-wrapper]").get(0).text();
                    exportTextContentToTxtFile(doctext,productName);
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

    public static void exportTextContentToTxtFile(String text,String product) throws IOException {
        Files.createDirectories(Paths.get("./output/documentation/"));
        File directory = new File(DOCUMENTATION_DIR);
        if (! directory.exists()){
            directory.mkdir();
        }
        try {
            PrintWriter out = new PrintWriter(new FileWriter(DOCUMENTATION_DIR+product+".txt"));

            out.println(text);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
