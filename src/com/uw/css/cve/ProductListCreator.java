package com.uw.css.cve;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ProductListCreator {
    public static List<Product> getProductList(String url,Integer vendorId){
        Document doc;
        List<Product> products = new ArrayList<>();
        System.out.println(vendorId);
        try {
            String baseUrl = "https://www.cvedetails.com";
            //Get Document object after parsing the html from given url.
            doc = Jsoup.connect(url).get();
           // String vendorName = doc.select(cssQuery)
//            Elements elements = doc.select("div[id=pagingt]").get(0).getElementsByTag("a");
            Elements elements = doc.select("div[class=paging] a");
            int pages = elements.size();
            for(int i=1;i<=pages;i++){
                String url1 = "https://www.cvedetails.com/product-list/product_type-/vendor_id-"+vendorId+"/firstchar-/page-"+i+"/products.html";
                doc = Jsoup.connect(url1).get();
//                Elements productByVendor = doc.select("table[class=listtable] tr");
                Elements productByVendor = doc.select("table[class='my-2 table table-striped table-hover table-borderless  '] tr");
                int size = productByVendor.size();
                for(int j=2;j<size;j++){
                    Element element = productByVendor.get(j);
                    Elements children = element.getElementsByTag("a");
                    String text = children.get(0).text();
                    String href = baseUrl + children.get(3).attr("href");  //Changed it from 2 -> 3
                    Integer productId = Integer.valueOf(href.split("/")[5].split("-")[1]);
                    Integer numVulnerabilities = Integer.valueOf(children.get(3).text());
                    String vendor = href.split("/")[6].split("-")[0];
                    String productType = element.getElementsByTag("td").get(3).text();
                    Product product = new Product("\""+text+"\"",productId,vendorId,href,numVulnerabilities, vendor,productType);
                    products.add(product);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return products;
    }
    public static void run() {
        Integer firstVendor = 7043;
        Integer lastVendor = 7044;//Max = 34713
        Integer vendorId;

        for(vendorId = firstVendor; vendorId <= lastVendor; vendorId++){
            String url = "https://www.cvedetails.com/product-list/vendor_id-" + Integer.toString(vendorId);
            List<Product> products = getProductList(url,vendorId);
            if(products.size() > 0){
                try{
                    String vendor = String.valueOf(products.get(0).vendorId);
                    exportProductsToCsv(products,vendor);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }
    }

    private static void exportProductsToCsv(List<Product> products,String vendor) {
        try {
            Files.createDirectories(Paths.get("./output/vendors/"));
            PrintWriter out = new PrintWriter(new FileWriter("./output/vendors/"+vendor+".csv"));

            out.println("ProductName,URL,Num_Vulnerabilities,productId,vendorId,vendorName,productType");
            for(Product product : products) {
                out.println(product.toString());
            }

            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
