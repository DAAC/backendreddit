package com.mx.daac.repository;

import com.mx.daac.model.Comment;
import com.mx.daac.model.Post;
import com.mx.daac.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);

    List<Comment> findByUser(Users user);
}
