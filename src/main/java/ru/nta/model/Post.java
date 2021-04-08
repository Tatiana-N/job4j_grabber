package ru.nta.model;

import java.time.LocalDateTime;

public class Post {
    private String link;
    private String name;
    private LocalDateTime dateTime;

    public Post(String link, String name, LocalDateTime dateTime) {
        this.link = link;
        this.name = name;
        this.dateTime = dateTime;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
