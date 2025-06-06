package ru.netology.model;

public class PostEntity {
    private final Post post;
    private boolean removed;

    public PostEntity(Post post) {
        this.post = post;
        this.removed = false;
    }

    public Post getPost() {
        return post;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }
}
