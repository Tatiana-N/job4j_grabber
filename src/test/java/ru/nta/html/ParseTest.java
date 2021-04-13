package ru.nta.html;

import org.junit.Assert;
import org.junit.Test;
import ru.nta.api.Parse;
import ru.nta.model.Post;

import java.util.List;

public class ParseTest {

    @Test
    public void list() {
        Parse sqlRuParse = new SqlRuParse();
        for (int i = 0; i < 5; i++) {
            List<Post> pages = sqlRuParse.list("https://www.sql.ru/forum/job-offers/" + (i + 1));
            Assert.assertEquals(pages.size(), 53);
        }
    }

    @Test
    public void detail() {
        Parse sqlRuParse = new SqlRuParse();
            List<Post> pages1 = sqlRuParse.list("https://www.sql.ru/forum/job-offers/1");
            List<Post> pages2 = sqlRuParse.list("https://www.sql.ru/forum/job-offers/2");
            List<Post> pages3 = sqlRuParse.list("https://www.sql.ru/forum/job-offers/3");
            Assert.assertTrue(pages1.stream().anyMatch(el->el.getName().equals("Разработчик T-SQL (Москва, удалёнка после сетапа), 210К - 260К gross")));
            Assert.assertTrue(pages2.stream().anyMatch(el->el.getName().equals("Вакансия - Аналитик")));
            Assert.assertTrue(pages3.stream().anyMatch(el->el.getName().equals("Аналитик (ALM и рыночные риски)")));
    }
}