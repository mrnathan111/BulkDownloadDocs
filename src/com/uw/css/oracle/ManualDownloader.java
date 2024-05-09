package com.uw.css.oracle;

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
    public static String BASE_URL="https://docs.oracle.com/en/browseall.html"; //Starting URL to start scraping
    public static String DOCUMENTATION_DIR="./output/documentation/oracle/"; //Where .txt files go

    public static void main(String[] args) {
        getPackagesList();
    }

    private static void getPackagesList() {
        Document doc;
        Integer count = 0;
        Integer failed = 0;
        try {
            //Get Document object after parsing the html from given url.
            doc = Jsoup.connect(BASE_URL).get(); //connect to the starting URL
            Elements elements = doc.select("div[class='container azDisplayContainer'] a");
            for(Element element: elements){ //list of url elements
                try{
                    String url = element.attr("href");
                    String productName = sanitizeProductName(element.text());
                    System.out.println("*****"+productName+"*****");
                    Document doc2 = Jsoup.connect(url).get();
                    Elements books = doc2.select("div[class='ohc-sidebar hidden-xs'] a");
                    for (Element elem: books){ //list of nested elements links
                        if (elem.text().contains("Books")){ //docs are only in links with Books in it
                            String newUrl = elem.attr("href");
                            newUrl = url.replace("index.html", newUrl);
                            Document doc3 = Jsoup.connect(newUrl).get();
                            Elements finallinks = doc3.select("div[class=book] a");
                            for (Element el: finallinks){
                                Elements nameDoc = doc3.select("div[class=h4]");
                                for (Element name: nameDoc) {
                                    String prodNameDoc = sanitizeProductName(name.text());
                                    if (el.attr("href").contains("pdf")) {
                                        String pdfLink = el.attr("href");
                                        if (!el.attr("href").contains("docs.oracle")) {
                                            pdfLink = newUrl.replace("books.html", pdfLink);
                                        }
                                        String prodNameDocFinal = productName + "-" + prodNameDoc;
                                        exportContentToTxtFile(pdfLink, prodNameDocFinal);
                                        count++;
                                    }
                                }
                            }
                        }
                    }
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

    public static String sanitizeProductName(String s){  //Changes string characters that cannot go into a .txt file name
        return s.replaceAll("/","_");
    }

    public static void exportContentToTxtFile(String manualUrl,String product) throws IOException { //creates .pdf files
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
