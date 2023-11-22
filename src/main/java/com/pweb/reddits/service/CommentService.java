package com.pweb.reddits.service;

import com.pweb.reddits.entity.Comment;
import com.pweb.reddits.entity.Post;
import com.pweb.reddits.repo.CommentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CommentService {
    @Autowired
    private CommentRepo repo;

    public void add(Comment comment) {
        repo.save(comment);
    }

    public List<Comment> findByPostId(Long postId) {
        List<Comment> result = new ArrayList<>();

        for (Comment c : repo.findAll()) {
            if (c.getPostId().equals(postId)) {
                result.add(c);
            }
        }

        return result;
    }

    public List<Comment> findNewestByPostId(Long postId) {
        List<Comment> reversed = new ArrayList<>(findByPostId(postId));
        Collections.reverse(reversed);

        return reversed;
    }

    public Integer getCountByPostId(Long postId) {
        return findByPostId(postId).size();
    }

    public void removeById(Long id) {
        repo.deleteById(id);
    }

    public void update(Comment comment) {
        repo.save(comment);
    }
}
