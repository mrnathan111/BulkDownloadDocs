package com.uw.css.redhat;

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
    public static String BASE_URL="https://access.redhat.com/products/?product-tab=glossary";//Starting URL to start scraping
    public static String DOCUMENTATION_DIR="./output/documentation/redhat/"; //Where .txt files go
    public static String REDHAT_URL="https://access.redhat.com";
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
            Elements elements = doc.select("div[class=productLink]");
            for(Element element: elements){ //list of doc links
                try{
                    boolean isProduct = !(element.select("a:contains(Product Info)").isEmpty());
                    if (isProduct) {
                        Elements linkElement = element.select("a[href*=documentation]"); //accessed the html element containing url and then name separately
                        String url = linkElement.attr("href");
                        Elements nameElement = element.getElementsByTag("h3");
                        String productName = nameElement.text();
                        System.out.println("*****" + productName + "*****");
                        Document doc2 = Jsoup.connect(url).get();
                        Elements elements2 = doc2.select("ul[class='dropdown-menu dropdown-menu-right'] a:contains(single)");
                        for (Element elem2 : elements2) {
                            try {
                                String docUrl = REDHAT_URL + elem2.attr("href");
                                Document doc3 = Jsoup.connect(docUrl).get();
                                String doctext = doc3.select("div[class='doc-wrapper pvof-doc__wrapper j-superdoc__content-wrapper']").get(0).text();
                                allTextContent.append(doctext).append("\n").append("\n");
                                count++;
                            } catch (Exception e) {
                                e.printStackTrace();
                                failed++;
                            }
                        }
                        exportTextContentToTxtFile(productName);
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

    public static void exportTextContentToTxtFile(String product) throws IOException { //creates .txt files
        Files.createDirectories(Paths.get("./output/documentation/"));
        String prefix = "Red Hat ";
        if (product.startsWith(prefix)) {
            product = product.substring(prefix.length()).trim();
        }
        File directory = new File(DOCUMENTATION_DIR);
        if (! directory.exists()){
            directory.mkdir();
        }
        PrintWriter out = new PrintWriter(new FileWriter(DOCUMENTATION_DIR+product+".txt"));
        out.println(allTextContent.toString());
        out.close();
    }
}
