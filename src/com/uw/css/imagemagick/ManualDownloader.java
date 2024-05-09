package com.uw.css.imagemagick;

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
    public static String BASE_URL="https://imagemagick.org";
    public static String DOCUMENTATION_DIR="./output/documentation/imagemagick/";
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
            doc = Jsoup.connect(BASE_URL+"/script/index.php").get();
            Elements elements = doc.select("div[class=pre-scrollable] td a");
            for(Element element: elements){
                try{
                    String extractedUrl = element.attr("href");
                    if (extractedUrl.contains("Usage") || extractedUrl.contains("script")) { //ignore links to the couple of random websites
                        System.out.println("*****" + sanitizeProductName(element.text()) + "*****");
                        if (extractedUrl.contains("script")) { //Detects href's that leave out the baseURL
                            String doctext = Jsoup.connect(BASE_URL + extractedUrl).get().select("div[class=magick-header]").get(0).text();
                            allTextContent.append(doctext).append("\n").append("\n");
                        }
                        else if (extractedUrl.contains("Usage")) { //Detects href's that include the full url
                            String doctext = Jsoup.connect(extractedUrl).get().select("div[class=magick-header]").get(0).text();
                            allTextContent.append(doctext).append("\n").append("\n");
                        }
                    }
                    count+=1;
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

    public static String sanitizeProductName(String s){
        return s.replaceAll("/","_");
    }

    public static void exportTextContentToTxtFile() throws IOException {
        Files.createDirectories(Paths.get("./output/documentation/"));
        File directory = new File(DOCUMENTATION_DIR);
        if (! directory.exists()){
            directory.mkdir();
        }
        PrintWriter out = new PrintWriter(new FileWriter(DOCUMENTATION_DIR+"imagemagick.txt"));
        out.println(allTextContent.toString());
        out.close();
    }
}

