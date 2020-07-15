package com.mx.daac.repository;

import com.mx.daac.model.Post;
import com.mx.daac.model.Subreddit;
import com.mx.daac.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllBySubreddit(Subreddit subreddit);

    List<Post> findByUser(Users user);
}
