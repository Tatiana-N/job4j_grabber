package ru.nta.api;

import org.jsoup.nodes.Element;

import java.util.List;

public interface Parse<T> {
    List<T> list(String link);

    T detail(Element ref);
}
