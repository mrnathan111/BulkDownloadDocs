package com.uw.css.firefox;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.regex.*;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ManualDownloader {
    public static String BASE_URL="https://firefox-source-docs.mozilla.org/"; //Starting URL to start scraping
    public static String DOCUMENTATION_DIR="./output/documentation/firefox/"; //Where .txt files go
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
            Elements elements = doc.select("div[itemprop=articleBody] li[class=toctree-l1] > a");
            for(Element element: elements){ //list of element links
                try{
                    String url = element.attr("href");
                    url = BASE_URL + url;
                    System.out.println("*****"+sanitizeProductName(element.text())+"*****");
                    String doctext = Jsoup.connect(url).get().select("div[itemprop=articleBody]").get(0).text();
                    allTextContent.append(doctext).append("\n").append("\n");
                    exportTextContentToTxtFile();
                    count+=1;
                    Document doc2 = Jsoup.connect(url).get();
                    Elements elements1 = doc2.select("div[itemprop=articleBody] li[class=toctree-l1] > a");
                    String mainUrl = url;
                    if (!elements1.isEmpty()) {
                        for (Element elem1: elements1) { //possible nested list of links
                            try {
                                url = newUrl(mainUrl, elem1.attr("href"));
                                System.out.println("-------" + sanitizeProductName(elem1.text()) + "-------");
                                doctext = Jsoup.connect(url).get().select("div[itemprop=articleBody]").get(0).text();
                                allTextContent.append(doctext).append("\n").append("\n");
                                exportTextContentToTxtFile();
                                count++;
                                Document doc3 = Jsoup.connect(url).get();
                                Elements elements2 = doc3.select("div[itemprop=articleBody] li[class=toctree-l1] > a");
                                String main2Url = url;
                                if (!elements2.isEmpty()) {
                                    for (Element elem2: elements2) {
                                        try {
                                            url = newUrl(main2Url, elem2.attr("href"));
                                            System.out.println("///////////" + sanitizeProductName(elem2.text()) + "//////////");
                                            doctext = Jsoup.connect(url).get().select("div[itemprop=articleBody]").get(0).text();
                                            allTextContent.append(doctext).append("\n").append("\n");
                                            exportTextContentToTxtFile();
                                            count++;
                                        }catch (Exception e){
                                            e.printStackTrace();
                                            failed+=1;
                                        }
                                    }
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                                failed+=1;
                            }
                        }
                    }
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

    public static String sanitizeProductName(String s){ //Changes string characters that cannot go into a .txt file name
        return s.replaceAll("/","_");
    }

    public static String newUrl(String existingUrl, String newHref){ //fixes partial url to complete url
            String pattern = "[A-Za-z0-9\\-_]+\\.html";
            String target = "";
            if (newHref.contains("..")) {
                target = BASE_URL + newHref.replace("../", "");
            } else if ((newHref.contains("/") && newHref.indexOf("/") > 0) || newHref.matches(pattern)){
                String temp = existingUrl;
                String lastSeg = temp.substring(existingUrl.lastIndexOf("/") + 1);
                target = existingUrl.replaceFirst(Pattern.quote(lastSeg) + "$", newHref);
            }
            return target;
    }

    public static void exportTextContentToTxtFile() throws IOException { //creates .txt files
        Files.createDirectories(Paths.get("./output/documentation/"));
        File directory = new File(DOCUMENTATION_DIR);
        if (! directory.exists()){
            directory.mkdir();
        }
        PrintWriter out = new PrintWriter(new FileWriter(DOCUMENTATION_DIR+"firefox.txt"));
        out.println(allTextContent.toString());
        out.close();
    }
}
