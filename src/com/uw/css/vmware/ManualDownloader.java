package com.uw.css.vmware;

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
    public static String BASE_URL="https://docs.vmware.com/allproducts.html"; //Starting URL to start scraping
    public static String DOCUMENTATION_DIR="./output/documentation/vmware/"; //Where .txt files go

    public static void main(String[] args) {
        getPackagesList();
    }

    private static void getPackagesList() {
        Document doc;
        Integer count = 0;
        Integer failed = 0;
        try {
            doc = Jsoup.connect(BASE_URL).get(); //connect to the starting URL
            Elements elements = doc.select("div[class='col-12 vertical-stack-container p-0 mt-3'] a");
            for(Element element: elements){ //Main elements list (urls)
                try{
                    String url = element.attr("href"); //URL of documentation
                    String productName = sanitizeProductName(element.text()); //Clean up fileName
                    System.out.println("*****"+productName+"*****");
                    String doctext = Jsoup.connect(url).get().select("div[class='body conbody']").get(0).text(); //retrieve text
                    exportTextContentToTxtFile(doctext,productName); //create file
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

    public static String sanitizeProductName(String s){ //Changes string characters that cannot go into a .txt file name
        return s.replaceAll("/","_");
    }

    public static void exportTextContentToTxtFile(String text,String product) throws IOException { //creates the .txt file
        Files.createDirectories(Paths.get("./output/documentation/"));
        File directory = new File(DOCUMENTATION_DIR);
        if (! directory.exists()){
            directory.mkdir();
        }
        PrintWriter out = new PrintWriter(new FileWriter(DOCUMENTATION_DIR+product+".txt"));
        out.println(text);
        out.close();
    }
}
