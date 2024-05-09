package com.uw.css.linux;

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
    public static String BASE_URL = "https://www.kernel.org/doc/html/v4.12/"; //base URL for later url strings to build on
    public static String START_URL = "https://www.kernel.org/doc/html/v4.12/index.html"; //Starting URL to start scraping
    public static String DOCUMENTATION_DIR = "./output/documentation/linux/"; //Where .txt files go
    public static StringBuilder allTextContent = new StringBuilder(); //Contains all the text for the product, appended as more docs are found

    public static void main(String[] args) {
        getPackagesList();
    }

    private static void getPackagesList() {
        Document doc;
        Integer count = 0;
        Integer failed = 0;
        try {
            doc = Jsoup.connect(START_URL).get(); //connect to the starting URL
            Elements elements = doc.select("div.rst-content li.toctree-l1 a:first-child");
            for (Element element : elements) { //list of link elements
                try {
                    String url = element.attr("href");
                    url = BASE_URL + url;
                    if (!url.contains("translations")) { //ignore the different language translation pages
                        Document doc2 = Jsoup.connect(url).get();
                        Elements element2 = doc2.select("div.rst-content li.toctree-l1 a:first-child");
                        if (!element2.isEmpty()) {
                            for (Element elem2 : element2) {
                                int lastIndex = url.lastIndexOf('/');
                                String tempString = url.substring(0, lastIndex + 1);
                                url = tempString + elem2.attr("href");
                                Document doc3 = Jsoup.connect(url).get();
                                Elements element3 = doc3.select("div.rst-content li.toctree-l1 a:first-child");
                                if (!element3.isEmpty()) {
                                    for (Element elem3 : element3) {
                                        int lastIndex3 = url.lastIndexOf('/');
                                        String tempString3 = url.substring(0, lastIndex3 + 1);
                                        url = tempString3 + elem3.attr("href");
                                        Document doc4 = Jsoup.connect(url).get();
                                        Elements element4 = doc4.select("div.rst-content li.toctree-l1 a:first-child");
                                        if (!element4.isEmpty()) {
                                            for (Element elem4 : element4) {
                                                int lastIndex4 = url.lastIndexOf('/');
                                                String tempString4 = url.substring(0, lastIndex4 + 1);
                                                url = tempString4 + elem4.attr("href");
                                                Document doc5 = Jsoup.connect(url).get();
                                                Elements element5 = doc5.select("div.rst-content li.toctree-l1 a:first-child");
                                                if (!element5.isEmpty()) {
                                                    for (Element elem5 : element5) {
                                                        int lastIndex5 = url.lastIndexOf('/');
                                                        String tempString5 = url.substring(0, lastIndex5 + 1);
                                                        url = tempString5 + elem5.attr("href");
                                                        Document doc6 = Jsoup.connect(url).get();
                                                        Elements element6 = doc6.select("div.rst-content li.toctree-l1 a:first-child");
                                                        if (!element6.isEmpty()) {
                                                            for (Element elem6 : element6) {
                                                                int lastIndex6 = url.lastIndexOf('/');
                                                                String tempString6 = url.substring(0, lastIndex6 + 1);
                                                                url = tempString6 + elem6.attr("href");
                                                                System.out.println("*****" + sanitizeProductName(elem6.text()) + "*****");
                                                                String doctext = Jsoup.connect(url).get().select("div[class=section]").get(0).text();
                                                                allTextContent.append(doctext).append("\n").append("\n");
                                                                exportTextContentToTxtFile();
                                                                count++;
                                                            }
                                                        } else {
                                                            System.out.println("*****" + sanitizeProductName(elem5.text()) + "*****");
                                                            String doctext = Jsoup.connect(url).get().select("div[class=section]").get(0).text();
                                                            allTextContent.append(doctext).append("\n").append("\n");
                                                            exportTextContentToTxtFile();
                                                            count++;
                                                        }

                                                    }
                                                } else {
                                                    System.out.println("*****" + sanitizeProductName(elem4.text()) + "*****");
                                                    String doctext = Jsoup.connect(url).get().select("div[class=section]").get(0).text();
                                                    allTextContent.append(doctext).append("\n").append("\n");
                                                    exportTextContentToTxtFile();
                                                    count++;
                                                }
                                            }
                                        } else {
                                            System.out.println("*****" + sanitizeProductName(elem3.text()) + "*****");
                                            String doctext = Jsoup.connect(url).get().select("div[class=section]").get(0).text();
                                            allTextContent.append(doctext).append("\n").append("\n");
                                            exportTextContentToTxtFile();
                                            count++;
                                        }
                                    }
                                } else {
                                    System.out.println("*****" + sanitizeProductName(elem2.text()) + "*****");
                                    String doctext = Jsoup.connect(url).get().select("div[class=section]").get(0).text();
                                    allTextContent.append(doctext).append("\n").append("\n");
                                    exportTextContentToTxtFile();
                                    count++;
                                }
                            }
                        }
                    }
                } catch(Exception e){
                    e.printStackTrace();
                    failed += 1;
                    System.out.println("FAILED++++++++++++++++");
                }
                    }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Downloaded " + count);
        System.out.println("Failed " + failed);
    }

    public static String sanitizeProductName(String s) { //Changes string characters that cannot go into a .txt file name
        return s.replaceAll("/", "_");
    }

    public static void exportTextContentToTxtFile() throws IOException { //creates .txt files
        Files.createDirectories(Paths.get("./output/documentation/"));
        File directory = new File(DOCUMENTATION_DIR);
        if (!directory.exists()) {
            directory.mkdir();
        }
        PrintWriter out = new PrintWriter(new FileWriter(DOCUMENTATION_DIR + "linux kernel.txt"));
        out.println(allTextContent.toString());
        out.close();
    }
}