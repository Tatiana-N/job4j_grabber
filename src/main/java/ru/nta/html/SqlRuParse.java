package ru.nta.html;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.nta.api.Parse;
import ru.nta.model.Post;
import ru.nta.utils.SqlRuDateTimeParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SqlRuParse implements Parse<Post> {
    @Override
    public List<Post> list(String link) {
        List<Post> posts = new ArrayList<>();
        Document doc;
        try {
            doc = Jsoup.connect(link).get();
            Elements row = doc.select(".postslisttopic");
            row.forEach(r -> posts.add(detail(r.child(0))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return posts;
    }

    public Post detail(Element ref) {
        String linkThis = ref.attr("href");
        String name = ref.text();
        Document doc;
        String text = null;
        String reference = null;
        String timeString = null;
        try {
            doc = Jsoup.connect(linkThis).get();

            Elements msgBody = doc.select(".msgBody");
            Elements msgFooter = doc.select(".msgFooter");
            String footerText = msgFooter.get(0).text();
            // text
            text = msgBody.get(1).text();
            // link
            reference = msgBody.get(0).child(0).attr("href");
            // time
            timeString = footerText.substring(0, footerText.indexOf("["));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Post(0, reference, text, name, new SqlRuDateTimeParser().parse(timeString));
    }

}