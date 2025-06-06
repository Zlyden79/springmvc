package ru.netology.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.netology.model.Post;
import ru.netology.model.PostEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class PostRepositoryImpl implements PostRepository {
    // храним в Map для быстрого поиска, key = PostEntity.getPost().getId();
    private final Map<Long, PostEntity> repo;
    //сюда запоминаем последний id для постов
    private final AtomicLong lastId;

    @Autowired
    public PostRepositoryImpl() {
        this.repo = new ConcurrentHashMap<>();
        this.lastId = new AtomicLong();
        lastId.set(0L);
    }

    @Override
    public List<Post> all() {
        if (repo.isEmpty()) return Collections.emptyList();
        return repo.values().stream() // мутим стрим из PostEntity
                .filter(a -> !a.isRemoved()) // фильтруем неудалённые
                .map(PostEntity::getPost) // вынимаем Post из PostEntity
                .collect(Collectors.toList()); // терминальная операция - пакуем в List
    }

    @Override
    public Optional<Post> getById(long id) {
        //если в хранилище нет поста с id - возвращаем пустой Optional
        if (!repo.containsKey(id)) {return Optional.empty();}
        //если дошло сюда - значит пост с id есть, проверяем поле removed
        //если пост помечен удалённым - возвращаем пустой Optional
        if (repo.get(id).isRemoved()) {return Optional.empty();}
        //если дошло сюда - значит пост с id есть и он не помечен удалённым, возвращаем пост
        return Optional.ofNullable(repo.get(id).getPost());
    }

    @Override
    public Post save(Post post) {
        long postId = post.getId();
        //если в посте нет id - значит пост новый, смело сохраняем
        if (postId == 0) {
            //устанавливаем в post очередной id
            post.setId(lastId.incrementAndGet());
            //сохраняем в хранилище
            repo.put(post.getId(), new PostEntity(post));
            //возвращаем post
            return post;
        }
        //если дошло сюда => пост имеет id, проверим его наличие в хранилище
        //если поста с id нет в хранилище - возвращаем null
        if (!repo.containsKey(postId)) {
            return null;
        }
        //если дошло сюда => пост имеется в хранилище
        //если пост помечен удалённым - возвращаем null
        if (repo.get(postId).isRemoved()) {
            return null;
        }
        //если дошло сюда => пост имеется в хранилище и не помечен удалённым
        //пакуем Post в PostEntity и переписываем в хранилище, возвращаем Post
        repo.put(postId, new PostEntity(post));
        return post;
    }

    @Override
    public void removeById(long id) {
        if (getById(id).isPresent()) {
            repo.get(id).setRemoved(true);
        }
    }
}