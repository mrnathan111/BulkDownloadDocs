package com.uw.css.drupal;

import org.jsoup.Connection;
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
    public static String BASE_URL="https://www.drupal.org";
    public static String DOCUMENTATION_DIR="./output/documentation/drupal/";

    public static void main(String[] args) {
        getPackagesList();
    }

    private static void getPackagesList() {
        Document doc;
        Integer count = 0;
        Integer failed = 0;
        try {
            //Get Document object after parsing the html from given url.
            File input = new File("src/com/uw/css/drupal/drupal.html");
            doc = Jsoup.parse(input, "UTF-8", BASE_URL+"/docs/");
            Elements elements = doc.select("div[class=pane-content] section").get(6).select("ul a");
            for(Element element: elements){
                try{
                    String url = element.attr("href");
                    String productName = sanitizeProductName(element.text());
                    System.out.println("*****"+productName+"*****");
//                    Connection connection1 = null;
//                    if(connection1==null){
//                        connection1 = Jsoup.connect(BASE_URL + url);
//                        connection1.userAgent("Mozilla/5.0");
//                        connection1.referrer(BASE_URL + url);
//                        connection1.header("x-newrelic-id","XAcEV19XGwcCUldTDwg=");
//                        connection1.header("origin","https://www.drupal.org");
//                    }
//                    Document contributedModule = connection1.get();
                    Document contributedModule = Jsoup.parse(post2Parse("curl "+BASE_URL+url));
                    String doctext = "";
                    doctext+=contributedModule.select("div[class=pane-content] p").text();
                    Elements submodules = contributedModule.select("div[class=pane-content] section h2 a");
                    for(Element submodule: submodules){
                        Document submoduleText = Jsoup.parse(post2Parse("curl "+BASE_URL + submodule.attr("href")));
                        doctext += submoduleText.select("div[class=pane-content]").get(6).text();
                    }
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

    public static String sanitizeProductName(String s){
        return s.replaceAll("/","_");
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
        File directory = new File(DOCUMENTATION_DIR);
        if (! directory.exists()){
            directory.mkdir();
        }
        InputStream inputStream = null;
        try {
            inputStream = new URL(manualUrl).openStream();
            Files.copy(inputStream, Paths.get(DOCUMENTATION_DIR+product+".txt"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String post2Parse(String command) {

        String outputString;

        System.out.println(command);
        String result="";
        Process curlProc;
        try {
            curlProc = Runtime.getRuntime().exec(command);

            DataInputStream curlIn = new DataInputStream(
                    curlProc.getInputStream());

            while ((outputString = curlIn.readLine()) != null) {
                result+=outputString;
            }

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return result;
    }
}
