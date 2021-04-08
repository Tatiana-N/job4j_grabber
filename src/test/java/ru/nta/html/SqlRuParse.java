package ru.nta.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.stream.Collectors;

public class SqlRuParse {
    public static void main(String[] args) throws Exception {
        for (int j = 1; j <= 5; j++) {
            Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers/" + j).get();
            Elements row = doc.select(".postslisttopic");
            List<Element> style = doc.select(".altCol").stream().filter(t -> t.attr("style").equals("text-align:center")).collect(Collectors.toList());
            for (int i = 0; i < row.size(); i++) {
                Element href = row.get(i).child(0);
                System.out.println(href.attr("href"));
                System.out.println(href.text());
                System.out.println(style.get(i).text());
            }
        }
    }
}