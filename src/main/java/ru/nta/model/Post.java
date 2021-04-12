package ru.nta.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(" dd.MM.yyyyг. HH:mm ");
        return "Post{" +
                "id=" + id +
                ", created=" + formatter.format(created) +
                ", name='" + name + '\'' +
                ", link='" + link + '\'' +
                ", text='" + text.substring(0, 25) + "..." + '\'' +
                '}';
    }

    public Post(String link, String text, String name, LocalDateTime created) {
        this.link = link;
        this.text = text;
        this.name = name;
        this.created = created;
    }
}
