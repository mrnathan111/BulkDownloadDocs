package com.uw.css.opensuse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ManualDownloader {
    public static String BASE_URL="https://doc.opensuse.org"; //Starting URL to start scraping
    public static String DOCUMENTATION_DIR="./output/documentation/opensuse/"; //Where .txt files go
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
            Elements element1 = doc.select("div[class=row] div[class=card-img-overlay]");  //Elements elements = doc.select("div[class=\"homepage-section homepage-section-user-docs\"] a");
            for (Element elem1 : element1) {//Main List
                try {
                    String url = elem1.select("a[href]").first().attr("href"); //first html link
                    if (!url.contains("#")) { //if more links to go (#==the same link, so ignore things)
                        Document doc2 = Jsoup.connect(url).get();
                        Elements element2;
                        if (url.contains("/leap/archive/")) {
                            element2 = doc2.select("div[class=toc] > ul > li > span a");
                        }
                        else {
                            element2 = doc2.select("div[class=toc] > dl > dt a");
                        }
                        if (!element2.isEmpty()) {
                            for (Element elem2: element2) {
                                int lastIndex = url.lastIndexOf('/');
                                String tempString = url.substring(0, lastIndex + 1);
                                url = tempString + elem2.select("a[href]").first().attr("href");
                                if (!url.contains("#")) {
                                    Document doc3 = Jsoup.connect(url).get();
                                    Elements element3;
                                    if (url.contains("/leap/archive/")) {
                                        element3 = doc3.select("div[class=toc] > ul > li > span a");
                                    }
                                    else {
                                        element3 = doc3.select("div[class=toc] > dl > dt a");
                                    }
                                    if (!element3.isEmpty()) {
                                        for (Element elem3: element3) {
                                            int lastIndex2 = url.lastIndexOf('/');
                                            String tempString2 = url.substring(0, lastIndex2 + 1);
                                            url = tempString2 + elem3.select("a[href]").first().attr("href");
                                            if (!url.contains("#")) {
                                                Document doc4 = Jsoup.connect(url).get();
                                                Elements element4;
                                                if (url.contains("/leap/archive/")) {
                                                    element4 = doc4.select("div[class=toc] > ul > li > span a");
                                                }
                                                else {
                                                    element4 = doc4.select("div[class=toc] > dl > dt a");
                                                }
                                                if (!element4.isEmpty()) {
                                                    for (Element elem4: element4) {
                                                        int lastIndex3 = url.lastIndexOf('/');
                                                        String tempString3 = url.substring(0, lastIndex3 + 1);
                                                        url = tempString3 + elem4.select("a[href]").first().attr("href");
                                                        if (!url.contains("#")) {
                                                            Document doc5 = Jsoup.connect(url).get();
                                                            Elements element5;
                                                            if (url.contains("/leap/archive/")) {
                                                                element5 = doc5.select("div[class=toc] > ul > li > span a");
                                                            }
                                                            else {
                                                                element5 = doc5.select("div[class=toc] > dl > dt a");
                                                            }
                                                            if (!element5.isEmpty()) {
                                                                for (Element elem5: element5) {
                                                                    int lastIndex4 = url.lastIndexOf('/');
                                                                    String tempString4 = url.substring(0, lastIndex4 + 1);
                                                                    url = tempString4 + elem5.select("a[href]").first().attr("href");
                                                                    if (!url.contains("#")) {
                                                                        Document doc6 = Jsoup.connect(url).get();
                                                                        Elements element6;
                                                                        if (url.contains("/leap/archive/")) {
                                                                            element6 = doc6.select("div[class=toc] > ul > li > span a");
                                                                        }
                                                                        else {
                                                                            element6 = doc6.select("div[class=toc] > dl > dt a");
                                                                        }
                                                                        if (!element6.isEmpty()) {
                                                                            for (Element elem6: element6) {
                                                                                int lastIndex5 = url.lastIndexOf('/');
                                                                                String tempString5 = url.substring(0, lastIndex5 + 1);
                                                                                url = tempString5 + elem6.select("a[href]").first().attr("href");
                                                                                if (!url.contains("#")) {
                                                                                    System.out.println("*****" + sanitizeProductName(elem6.text()) + "*****");
                                                                                    String doctext;
                                                                                    if (url.contains("/leap/archive/")) {
                                                                                        doctext = Jsoup.connect(url).get().select("article[class=documentation]").get(0).text();
                                                                                    }
                                                                                    else {
                                                                                        doctext = Jsoup.connect(url).get().select("div[class=documentation]").get(0).text();
                                                                                    }
                                                                                    allTextContent.append(doctext).append("\n").append("\n");
                                                                                    count++;
                                                                                }
                                                                                else {
                                                                                    System.out.println("*****" + sanitizeProductName(elem5.text()) + "*****");
                                                                                    String doctext;
                                                                                    if (url.contains("/leap/archive/")) {
                                                                                        doctext = Jsoup.connect(url).get().select("article[class=documentation]").get(0).text();
                                                                                    }
                                                                                    else {
                                                                                        doctext = Jsoup.connect(url).get().select("div[class=documentation]").get(0).text();
                                                                                    }
                                                                                    allTextContent.append(doctext).append("\n").append("\n");
                                                                                    count++;
                                                                                    break;
                                                                                }
                                                                            }
                                                                        }
                                                                        else {
                                                                            System.out.println("*****" + sanitizeProductName(elem5.text()) + "*****");
                                                                            String doctext;
                                                                            if (url.contains("/leap/archive/")) {
                                                                                doctext = Jsoup.connect(url).get().select("article[class=documentation]").get(0).text();
                                                                            }
                                                                            else {
                                                                                doctext = Jsoup.connect(url).get().select("div[class=documentation]").get(0).text();
                                                                            }
                                                                            allTextContent.append(doctext).append("\n").append("\n");
                                                                            count++;
                                                                        }
                                                                    }
                                                                    else {
                                                                        System.out.println("*****" + sanitizeProductName(elem4.text()) + "*****");
                                                                        String doctext;
                                                                        if (url.contains("/leap/archive/")) {
                                                                            doctext = Jsoup.connect(url).get().select("article[class=documentation]").get(0).text();
                                                                        }
                                                                        else {
                                                                            doctext = Jsoup.connect(url).get().select("div[class=documentation]").get(0).text();
                                                                        }
                                                                        allTextContent.append(doctext).append("\n").append("\n");
                                                                        count++;
                                                                        break;
                                                                    }
                                                                }
                                                            }
                                                            else {
                                                                System.out.println("*****" + sanitizeProductName(elem4.text()) + "*****");
                                                                String doctext;
                                                                if (url.contains("/leap/archive/")) {
                                                                    doctext = Jsoup.connect(url).get().select("article[class=documentation]").get(0).text();
                                                                }
                                                                else {
                                                                    doctext = Jsoup.connect(url).get().select("div[class=documentation]").get(0).text();
                                                                }
                                                                allTextContent.append(doctext).append("\n").append("\n");
                                                                count++;
                                                            }
                                                        }
                                                        else {
                                                            System.out.println("*****" + sanitizeProductName(elem3.text()) + "*****");
                                                            String doctext;
                                                            if (url.contains("/leap/archive/")) {
                                                                doctext = Jsoup.connect(url).get().select("article[class=documentation]").get(0).text();
                                                            }
                                                            else {
                                                                doctext = Jsoup.connect(url).get().select("div[class=documentation]").get(0).text();
                                                            }
                                                            allTextContent.append(doctext).append("\n").append("\n");
                                                            count++;
                                                            break;
                                                        }
                                                    }
                                                }
                                                else {
                                                    System.out.println("*****" + sanitizeProductName(elem3.text()) + "*****");
                                                    String doctext;
                                                    if (url.contains("/leap/archive/")) {
                                                        doctext = Jsoup.connect(url).get().select("article[class=documentation]").get(0).text();
                                                    }
                                                    else {
                                                        doctext = Jsoup.connect(url).get().select("div[class=documentation]").get(0).text();
                                                    }
                                                    allTextContent.append(doctext).append("\n").append("\n");
                                                    count++;
                                                }
                                            }
                                            else {
                                                System.out.println("*****" + sanitizeProductName(elem2.text()) + "*****");
                                                String doctext;
                                                if (url.contains("/leap/archive/")) {
                                                    doctext = Jsoup.connect(url).get().select("article[class=documentation]").get(0).text();
                                                }
                                                else {
                                                    doctext = Jsoup.connect(url).get().select("div[class=documentation]").get(0).text();
                                                }
                                                allTextContent.append(doctext).append("\n").append("\n");
                                                count++;
                                                break;
                                            }
                                        }
                                    }
                                    else {
                                        System.out.println("*****" + sanitizeProductName(elem2.text()) + "*****");
                                        String doctext;
                                        if (url.contains("/leap/archive/")) {
                                            doctext = Jsoup.connect(url).get().select("article[class=documentation]").get(0).text();
                                        }
                                        else {
                                            doctext = Jsoup.connect(url).get().select("div[class=documentation]").get(0).text();
                                        }
                                        allTextContent.append(doctext).append("\n").append("\n");
                                        count++;
                                    }
                                }
                                else {
                                    System.out.println("*****" + sanitizeProductName(elem1.select("h5").text()) + "*****");
                                    String doctext;
                                    if (url.contains("/leap/archive/")) {
                                        doctext = Jsoup.connect(url).get().select("article[class=documentation]").get(0).text();
                                    }
                                    else {
                                        doctext = Jsoup.connect(url).get().select("div[class=documentation]").get(0).text();
                                    }
                                    allTextContent.append(doctext).append("\n").append("\n");
                                    count++;
                                    break;
                                }
                            }
                        }
                        else {
                            System.out.println("*****" + sanitizeProductName(elem1.select("h5").text()) + "*****");
                            String doctext = Jsoup.connect(url).get().select("article[class=documentation]").get(0).text();
                            allTextContent.append(doctext).append("\n").append("\n");
                            count++;
                        }
                    } else {
                        System.out.println("*****" + sanitizeProductName(elem1.select("h5").text()) + "*****");
                        String doctext = Jsoup.connect(url).get().select("article[class=documentation]").get(0).text();
                        allTextContent.append(doctext).append("\n").append("\n");
                        count++;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    failed += 1;
                }
            }
            exportTextContentToTxtFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Downloaded "+count);
        System.out.println("Failed "+failed);
    }

    public static String sanitizeProductName(String s){  //Changes string characters that cannot go into a .txt file name
        return s.replaceAll("/","_");
    }

    public static void exportTextContentToTxtFile() throws IOException { //creates .txt files
        Files.createDirectories(Paths.get("./output/documentation/"));
        File directory = new File(DOCUMENTATION_DIR);
        if (! directory.exists()){
            directory.mkdir();
        }
        PrintWriter out = new PrintWriter(new FileWriter(DOCUMENTATION_DIR+"leap.txt"));
        out.println(allTextContent.toString());
        out.close();
    }
}