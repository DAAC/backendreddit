package com.mx.daac.service;

import com.mx.daac.dto.PostRequest;
import com.mx.daac.dto.PostResponse;
import com.mx.daac.exceptions.PostNotFoundException;
import com.mx.daac.exceptions.SpringRedditException;
import com.mx.daac.exceptions.SubredditNotFoundException;
import com.mx.daac.mapper.PostMapper;
import com.mx.daac.model.Post;
import com.mx.daac.model.Subreddit;
import com.mx.daac.model.Users;
import com.mx.daac.repository.PostRepository;
import com.mx.daac.repository.SubredditRepository;
import com.mx.daac.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class PostService {

    private final SubredditRepository subredditRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final PostMapper postMapper;

    public Post save(PostRequest postRequest) {
        Subreddit subreddit = subredditRepository.findByName(postRequest.getSubredditName())
                .orElseThrow(() -> new SubredditNotFoundException(postRequest.getSubredditName()));

        Users currentUser = authService.getCurrentUser();

        return postRepository.save(postMapper.map(postRequest, subreddit, currentUser));
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException(id.toString()));

        return postMapper.mapToDto(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll()
                .stream()
                .map(postMapper::mapToDto)
                .collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsBySubreddit(Long subredditId) {
        Subreddit subreddit = subredditRepository.findById(subredditId)
                .orElseThrow(() -> new SubredditNotFoundException(subredditId.toString()));
        List<Post> posts = postRepository.findAllBySubreddit(subreddit);
        return posts.stream()
                .map(postMapper::mapToDto)
                .collect(Collectors.toList());

    }

    @Transactional(readOnly = true)
    public List<PostResponse> getPostsByUsername(String username) {
        Users users = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
         return postRepository.findByUser(users)
                 .stream()
                 .map(postMapper::mapToDto)
                 .collect(Collectors.toList());
    }


}
