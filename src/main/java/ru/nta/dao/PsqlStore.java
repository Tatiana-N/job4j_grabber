package ru.nta.dao;

import ru.nta.api.DaoStore;
import ru.nta.model.Post;
import ru.nta.utils.DateTimeParser;
import ru.nta.utils.SqlRuDateTimeParser;

import java.sql.*;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class PsqlStore implements DaoStore<Post>, AutoCloseable {
    private final Connection connection;
    private final String tableName;

    public PsqlStore(Connection connection, String tableName) {
        this.connection = connection;
        this.tableName = tableName;
    }

    public PsqlStore(Properties cfg, String tableName) {
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
            connection = DriverManager.getConnection(cfg.getProperty("jdbc.url"), cfg.getProperty("jdbc.username"), cfg.getProperty("jdbc.password"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        this.tableName = tableName;
    }

    @Override
    public void save(Post post) {
        if (post.getText().toLowerCase(Locale.ROOT).contains("java") || post.getName().toLowerCase(Locale.ROOT).contains("java")) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(String.format("insert into %s (name, text, link, created) values  (?, ?, ?, ?)", tableName), Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, post.getName());
                preparedStatement.setString(2, post.getText());
                preparedStatement.setString(3, post.getLink());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:00");
                Timestamp timestamp = Timestamp.valueOf(post.getCreated().format(formatter));
                preparedStatement.setTimestamp(4, timestamp);
                preparedStatement.executeUpdate();
                try (
                        ResultSet generatedKeys = preparedStatement.getGeneratedKeys()
                ) {
                    if (generatedKeys.next()) {
                        post.setId(generatedKeys.getInt(1));
                    }

                }
            } catch (SQLException e) {
                if (!e.getMessage().contains("????????????: ?????????????????????????? ???????????????? ??????????")) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public List<Post> getAll() {
        List<Post> list = new ArrayList<>();
        try (PreparedStatement ps = connection.prepareStatement(String.format("select * from %s;", tableName))) {
            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    LocalDateTime created = ldt(resultSet.getString("created"));
                    Post post = new Post(resultSet.getInt("id"), resultSet.getString("link"),
                            resultSet.getString("text"),
                            resultSet.getString("name"),
                            created);
                    list.add(post);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Post findById(String id) {
        Post post = null;
        int postId = Integer.parseInt(id);
        try (PreparedStatement preparedStatement = connection.prepareStatement(String.format("select * from %s where id = ? ;", tableName))) {
            preparedStatement.setInt(1, postId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                LocalDateTime created = ldt(resultSet.getString("created"));
                post = new Post(resultSet.getInt("id"), resultSet.getString("link"),
                        resultSet.getString("text"),
                        resultSet.getString("name"),
                        created);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return post;
    }

    private LocalDateTime ldt(String dateSQL) {
        DateTimeParser parser = new SqlRuDateTimeParser();
        String[] s = dateSQL.substring(0, dateSQL.lastIndexOf(":")).trim().replaceAll("-", " ").split(" ");
        String datePost = s[2] + " " + s[1] + " " + s[0].substring(2) + " " + s[3];
        try {
            return parser.parse(datePost);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }
}
