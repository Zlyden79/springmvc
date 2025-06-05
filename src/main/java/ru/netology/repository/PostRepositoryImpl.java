package ru.netology.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.netology.model.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PostRepositoryImpl implements PostRepository{
    // храним в мапе для быстрого поиска, key = Post.getId();
    private final Map<Long, Post> repo;
    //сюда запоминаем последний id для постов
    private volatile AtomicLong lastId;

    @Autowired
    public PostRepositoryImpl() {
        this.repo = new ConcurrentHashMap<>();
        this.lastId = new AtomicLong();
        lastId.set(0l);
    }

    @Override
    public List<Post> all() {
        if (repo.values().size() == 0) return Collections.emptyList();
        return new ArrayList<>(repo.values());
    }

    @Override
    public Optional<Post> getById(long id) {
        Optional<Post> result = Optional.ofNullable(repo.get(id));
        return result;
    }
    @Override
    public Post save(Post post) {
        long id = (post.getId() == 0) ? lastId.incrementAndGet() : post.getId();
        post.setId(id);
        repo.put(id, post);
        return post;
    }
    @Override
    public void removeById(long id) {
        repo.remove(id);
    }

    public Map<Long, Post> getRepo() {
        return repo;
    }

    public AtomicLong getLastId() {
        return lastId;
    }
}
