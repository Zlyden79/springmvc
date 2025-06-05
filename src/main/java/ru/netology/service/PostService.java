package ru.netology.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.netology.exception.NotFoundException;
import ru.netology.model.Post;
import ru.netology.repository.PostRepository;


import java.util.List;

@Service
public class PostService {
    private final PostRepository repository;

    @Autowired
    public PostService(PostRepository repository) {
        this.repository = repository;
    }

    public List<Post> all() {
        return repository.all();
    }

    public Post getById(long id) {
        return repository.getById(id).orElseThrow(NotFoundException::new);
    }

    public Post save(Post post) {
        //если поста с postId нет в репозитории - выбрасываем исключение
        if (post.getId() != 0 && repository.getById(post.getId()).isEmpty()) {
            String errorMessage = "Поста с id = " + post.getId() + " не существует, сохранение/модификация невозможна.";
            throw new NotFoundException(errorMessage);
        }
        return repository.save(post);
    }

    public void removeById(long id) {
        //Если поста с id не существует - выкинуть исклюение NotFoundException.
        if (repository.getById(id).isEmpty()) {
            throw new NotFoundException();
        }
        repository.removeById(id);
    }
}
