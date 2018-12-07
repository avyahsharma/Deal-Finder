// Avyah Sharma

package com.journaldev.jsoup;

import java.io.IOException;
import java.util.Scanner;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

public class ProductFinder {
    public static final String GOOGLE_SEARCH_URL = "https://www.google.com/search";

    public static void main(String[] args) throws IOException {
        // Enter what you want to purchase
        Scanner scanner = new Scanner(System.in);
        System.out.println("What would you like to purchase?");
        String searchTerm = scanner.nextLine();
        searchTerm = "purchase" + searchTerm;
        System.out.println("Please enter the number of results. Example: 5 10 20");
        int num = scanner.nextInt();
        scanner.close();

        String searchURL = GOOGLE_SEARCH_URL + "?q="+searchTerm+"&num="+num;
        //without proper User-Agent, we will get 403 error
        Document doc = Jsoup.connect(searchURL).userAgent("Mozilla/5.0").get();

        //below will print HTML data, save it to a file and open in browser to compare
        //System.out.println(doc.html());

        //If google search results HTML change the <h3 class="r" to <h3 class="r1"
        Elements results = doc.select("h3.r > a");
        ArrayList<String> URLs = new ArrayList<>();

        for (Element result : results) {
            String linkHref = result.attr("href");
            String URL = (linkHref.substring(6, linkHref.indexOf("&")));
            URL = URL.substring(1, URL.length()-1);
            URLs.add(URL);
        }

        // Filters URLs of malicious sites
        ProductFinder.filter(URLs);

        // Outputs prices
        for (int i = 0; i < URLs.size(); i++) {
            try {
                Document document = Jsoup.connect(URLs.get(i)).get();
                Elements spans = document.select(".contentInfoPriceOrder");

                for (Element span : spans) {
                    System.out.println(span.child(0).text());
                }
            } catch (final java.net.SocketTimeoutException e) {
                System.out.print("Connection timed out. No prices are listed on websites.");
                System.exit(0);
            }
        }
    }

    public static ArrayList<String> filter(ArrayList<String> URLs) {
        int i = 0;
        while (i < URLs.size()) {
            URLs.set(i, Jsoup.clean(URLs.get(i), Whitelist.basic()));
            i++;
        }

        return URLs;
    }
}
