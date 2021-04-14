package ru.nta.model;


import org.junit.Assert;
import org.junit.Test;
import ru.nta.api.Store;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;

public class PsqlStoreTest {

    @Test
    public void save() throws IOException {
        Properties prop = new Properties();
        prop.load(new FileReader("src/main/resources/rabbit.properties"));
        Store store = new PsqlStore(prop);
        Post post = new Post("er513", "re", "tr", LocalDateTime.now());
        store.save(post);
    }

    @Test
    public void getAll() throws IOException {
        Properties prop = new Properties();
        prop.load(new FileReader("src/main/resources/rabbit.properties"));
        Store store = new PsqlStore(prop);
        List<Post> all = store.getAll();
        all.forEach(System.out::println);
    }

    @Test
    public void findById() throws IOException {
        Post post = new Post("newLink413", "newText", "newName", LocalDateTime.now());
        Properties prop = new Properties();
        prop.load(new FileReader("src/main/resources/rabbit.properties"));
        Store store = new PsqlStore(prop);
        store.save(post);
        int id = post.getId();
        Post byId = store.findById(id + "");
        Assert.assertEquals(byId, post);

    }

    @Test
    public void close() {
    }
}