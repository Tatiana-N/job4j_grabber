package ru.nta.api;


import org.jsoup.nodes.Element;
import ru.nta.model.Post;

import java.util.List;

public interface Parse {
    List<Post> list(String link);

    Post detail(Element ref);
}
