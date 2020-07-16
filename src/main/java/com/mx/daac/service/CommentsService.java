package com.mx.daac.service;

import com.mx.daac.dto.CommentsDto;
import com.mx.daac.exceptions.PostNotFoundException;
import com.mx.daac.mapper.CommentMapper;
import com.mx.daac.model.Comment;
import com.mx.daac.model.NotificationEmail;
import com.mx.daac.model.Post;
import com.mx.daac.model.Users;
import com.mx.daac.repository.CommentRepository;
import com.mx.daac.repository.PostRepository;
import com.mx.daac.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class CommentsService {


    private  final String POST_URL = "";
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final AuthService authService;
    private final CommentMapper commentMapper;
    private final MailContentBuilder mailContentBuilder;
    private final MailService mailService;


    public void save(CommentsDto commentsDto) {
        Post post = postRepository.findById(commentsDto.getPostId())
                .orElseThrow(() -> new PostNotFoundException(commentsDto.getPostId().toString()));

        Comment comment = commentMapper.map(commentsDto, post, authService.getCurrentUser());
        commentRepository.save(comment);

        String message = mailContentBuilder.build(post.getUser().getUsername() + " posted a comment on your post. " + POST_URL);
        sendCommentNotification(message, post.getUser());
    }

    private void sendCommentNotification(String message, Users user) {
        mailService.sendMail(new NotificationEmail(user.getUsername() + " commented on your post ", user.getEmail(), message));
    }

    public List<CommentsDto> getAllCommentsForPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId.toString()));
        return commentRepository.findByPost(post)
         .stream()
         .map(commentMapper::mapToDto)
         .collect(Collectors.toList());


    }

    public List<CommentsDto> getAllCommentsByUsername(String userName) {
        Users user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new UsernameNotFoundException(userName));
        return commentRepository.findByUser(user)
                .stream()
                .map(commentMapper::mapToDto)
                .collect(Collectors.toList());


    }
}
