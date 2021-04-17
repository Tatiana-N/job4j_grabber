package ru.nta.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Post {
    private int id;
    private String link;
    private String text;
    private String name;
    private LocalDateTime created;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Post)) {
            return false;
        }
        Post post = (Post) o;
        return getId() == post.getId() && getLink().equals(post.getLink()) && getText().equals(post.getText()) && getName().equals(post.getName()) && getCreated().equals(post.getCreated());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getLink(), getText(), getName(), getCreated());
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" dd.MM.yyyyг. HH:mm ");
        text = text.length() > 25 ? text.substring(0, 25) + "..." : text;
        return "Post{"
                + "id=" + id
                + ", created=" + formatter.format(created)
                + ", name='" + name + '\''
                + ", link='" + link + '\''
                + ", text='" + text + '\''
                + '}';
    }

    public Post(int id, String link, String text, String name, LocalDateTime created) {
        this.id = id;
        this.link = link;
        this.text = text;
        this.name = name;
        created = created.withNano(0);
        created = created.withSecond(0);
        this.created = created;
    }
}
