package com.uw.css.wireshark;

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
    public static String BASE_URL="https://www.wireshark.org/docs/"; //Starting URL to start scraping
    public static String WIRESHARK_URL="https://www.wireshark.org"; //used for fileName cleaning in Sanitization func
    public static String DOCUMENTATION_DIR="./output/documentation/wireshark/"; //Where .txt files go
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
            doc = Jsoup.connect(BASE_URL).get(); //connect to the starting URL
            Elements elements = doc.select("div[class=main-container] a"); //Main list of elements
            for(Element element: elements){
                try{
                    String url = element.attr("href");
                    if (url.equals("wsug_html_chunked") || url.equals("man-pages/") || url.equals("relnotes/") || url.equals("../security/") || url.equals("/export.html")
                            || url.equals("wsdg_html_chunked")) { //only accepts desired target links
                        url = sanitizeUrl(url); //Clean up href's so they all are the full http url, not just a snippet
                        if (url.contains("chunked")) { //url's with chunked follow the same structure
                            Document doc2 = Jsoup.connect(url).get();
                            Elements elements2 = doc2.select("div[class=toc] a");
                            for(Element elem2: elements2){ //nested list of elements (links)
                                try{
                                    String docUrl = url + "/" + elem2.attr("href"); //existingURL + newHREF = finalURL
                                    System.out.println("*****"+elem2.text()+"*****");
                                    String doctext = Jsoup.connect(docUrl).get().select("div[class=section]").get(0).text();
                                    allTextContent.append(doctext).append("\n").append("\n");
                                    count+=1;
                                }catch (Exception e){
                                    e.printStackTrace();
                                    failed+=1;
                                }
                            }
                        }
                        else if (url.contains("security") || url.contains("export")) { //url's with security and export follow the same structure
                            String productName = sanitizeProductName(element.text());
                            System.out.println("*****"+productName+"*****");
                            String doctext = Jsoup.connect(url).get().select("div[class=main-container]").get(0).text();
                            allTextContent.append(doctext).append("\n").append("\n");
                            count++;
                        }
                        else {
                            Document doc3 = Jsoup.connect(url).get();
                            Elements element3 = doc3.select("div[class=main-container] a:not([href*=https])");
                            for (Element elem3: element3) {
                               System.out.println("*****"+elem3.text()+"*****");
                               String newUrl = sanitizeUrl(elem3.attr("href"));
                               String doctext;
                               if (newUrl.contains("relnotes")) { //doc text is in different HTML elements given different urls
                                   doctext = Jsoup.connect(newUrl).get().select("div[class=main-container]").get(0).text();
                               } else {
                                   doctext = Jsoup.connect(newUrl).get().select("div[id=content]").get(0).text();
                               }
                                allTextContent.append(doctext).append("\n").append("\n");
                                count++;
                            }
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

    public static String sanitizeProductName(String s){ //Changes string characters that cannot go into a .txt file name
        return s.replaceAll("\\?","_").replaceAll("/", "_");
    }
    public static String sanitizeUrl(String s){ //Turns partial newURL (HREF) to final complete URL
        if (s.contains("..")) {
            s = WIRESHARK_URL + s.replaceAll("..", "");
        }
        else if (s.contains("export") || s.contains("relnotes/wireshark-")) {
            s = WIRESHARK_URL + s;
        }
        else if (s.contains("chunked") || s.equals("relnotes/") || s.equals("man-pages/")){
            s = BASE_URL + s;
        }
        else {
            s = BASE_URL + "man-pages/" + s;
        }
        return s;
    }
    public static void exportTextContentToTxtFile() throws IOException { //creates .txt files
        Files.createDirectories(Paths.get("./output/documentation/"));
        File directory = new File(DOCUMENTATION_DIR);
        if (! directory.exists()){
            directory.mkdir();
        }
        PrintWriter out = new PrintWriter(new FileWriter(DOCUMENTATION_DIR + "wireshark.txt"));
        out.println(allTextContent.toString());
        out.close();
    }
}