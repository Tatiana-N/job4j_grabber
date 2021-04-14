package ru.nta.model;

import ru.nta.api.Store;
import ru.nta.utils.DateTimeParser;
import ru.nta.utils.SqlRuDateTimeParser;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {
    private final Connection connection;

    public PsqlStore(Properties cfg) {
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
            connection = DriverManager.getConnection(cfg.getProperty("jdbc.url"), cfg.getProperty("jdbc.username"), cfg.getProperty("jdbc.password"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void save(Post post) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("insert into post (name, text, link, created) values  (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, post.getName());
            preparedStatement.setString(2, post.getText());
            preparedStatement.setString(3, post.getLink());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yy, HH:mm");
            preparedStatement.setString(4, post.getCreated().format(formatter));
            preparedStatement.executeUpdate();
            try (
                    ResultSet generatedKeys = preparedStatement.getGeneratedKeys()
            ) {
                if (generatedKeys.next()) {
                    post.setId(generatedKeys.getInt(1));
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement("select * from post;")) {
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    DateTimeParser parser = new SqlRuDateTimeParser();
                    LocalDateTime created = parser.parse(resultSet.getString("created"));
                    Post post = new Post(resultSet.getString("link"),
                            resultSet.getString("text"),
                            resultSet.getString("name"),
                            created);
                    post.setId(resultSet.getInt("id"));
                    list.add(post);
                }
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Post findById(String id) {
        Post post = null;
        int postId = Integer.parseInt(id);
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from post where id = ? ;")) {
            preparedStatement.setInt(1, postId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                DateTimeParser parser = new SqlRuDateTimeParser();
                LocalDateTime created = parser.parse(resultSet.getString("created"));
                post = new Post(resultSet.getString("link"),
                        resultSet.getString("text"),
                        resultSet.getString("name"),
                        created);
                post.setId(postId);
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    public static void main(String[] args) {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("src/main/resources/rabbit.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Store psqlStore = new PsqlStore(properties);
        Post post = new Post("link1", "test1", "name1", LocalDateTime.now());
        psqlStore.save(post);
        List<Post> list = psqlStore.getAll();
        list.forEach(System.out::println);
        Post byId = psqlStore.findById(post.getId() + "");
        System.out.println(byId.equals(post));
    }

    @Override
    public boolean deeleteById(String id) {
        int postId = Integer.parseInt(id);
        try (PreparedStatement preparedStatement = connection.prepareStatement("delete from post where id = ? ;")) {
            preparedStatement.setInt(1, postId);
            preparedStatement.executeQuery();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return true;
    }
}
