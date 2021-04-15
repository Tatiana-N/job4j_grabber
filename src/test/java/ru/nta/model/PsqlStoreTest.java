package ru.nta.model;


import org.junit.Assert;
import org.junit.Test;
import ru.nta.api.Store;
import ru.nta.utils.ConnectionRollback;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

public class PsqlStoreTest {
    public Connection init() {
        Properties config = new Properties();
        try {
            config.load(new FileReader("src/main/resources/rabbit.properties"));
            Class.forName(config.getProperty("jdbc.driver"));
            return DriverManager.getConnection(config.getProperty("jdbc.url"), config.getProperty("jdbc.username"), config.getProperty("jdbc.password"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Test
    public void save() {
        try (Store psqlStore = new PsqlStore((ConnectionRollback.create(this.init())), "post")) {
            Post post1 = new Post("link1.ru", "something about java", "vacancy", LocalDateTime.now());
            Post post2 = new Post("link2.ru", "something about", "vacancy of Java", LocalDateTime.now());
            Post post3 = new Post("link3.ru", "this vacancy shouldn't be in DB", "vacancy", LocalDateTime.now());
            psqlStore.save(post1);
            psqlStore.save(post2);
            psqlStore.save(post3);
            Assert.assertTrue(post1.getId() > 0);
            List<Post> all = psqlStore.getAll();
            Assert.assertTrue(all.contains(post1));
            Assert.assertTrue(all.contains(post2));
            Assert.assertFalse(all.contains(post3));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void getAll() {
        try (Store psqlStore = new PsqlStore((ConnectionRollback.create(this.init())), "post")) {
            Post post1 = new Post("link1.ru", "something about java", "vacancy", LocalDateTime.now());
            Post post2 = new Post("link2.ru", "something about", "vacancy of Java", LocalDateTime.now());
            Post post3 = new Post("link3.ru", "this vacancy shouldn't be in DB", "vacancy", LocalDateTime.now());
            psqlStore.save(post1);
            psqlStore.save(post2);
            psqlStore.save(post3);
            List<Post> all = psqlStore.getAll();
            Assert.assertTrue(all.contains(post1));
            Assert.assertTrue(all.contains(post2));
            Assert.assertFalse(all.contains(post3));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void findById() {
        try (Store psqlStore = new PsqlStore((ConnectionRollback.create(this.init())), "post")) {
            Post post1 = new Post("link1.ru", "something about java", "vacancy", LocalDateTime.now());
            Post post2 = new Post("link2.ru", "something about", "vacancy of Java", LocalDateTime.now());
            Post post3 = new Post("link3.ru", "this vacancy shouldn't be in DB", "vacancy", LocalDateTime.now());
            psqlStore.save(post1);
            psqlStore.save(post2);
            psqlStore.save(post3);
            Post post1sql = psqlStore.findById(post1.getId() + "");
            Post post2sql = psqlStore.findById(post2.getId() + "");
            Post post3sql = psqlStore.findById(post3.getId() + "");
            Assert.assertEquals(post1, post1sql);
            Assert.assertEquals(post2, post2sql);
            Assert.assertNull(post3sql);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}