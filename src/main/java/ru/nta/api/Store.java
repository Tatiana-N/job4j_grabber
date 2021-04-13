package ru.nta.api;

import ru.nta.model.Post;

import java.util.List;

public interface Store {
    void save(Post post);

    List<Post> getAll();

    Post findById(String id);
}