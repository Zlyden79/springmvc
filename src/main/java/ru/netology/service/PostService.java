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
        //если пост без id => свежий, сохраняем
        if (post.getId() == 0) {return repository.save(post);}
        //Если поста с id нет в репозитории или есть, но помечен удалённым,
        //то репозиторий вернёт пустой Optional. Тогда выбрасываем исключение.
        if (repository.getById(post.getId()).isEmpty()) {
            throw new NotFoundException();
        }
        //а если Optional пришёл не пустым - значит переписываем пост в хранилище
        return repository.save(post);
    }

    public void removeById(long id) {
        //Если поста с id не существует - выкинуть исключение NotFoundException.
        if (repository.getById(id).isEmpty()) {
            throw new NotFoundException();
        }
        repository.removeById(id);
    }
}
