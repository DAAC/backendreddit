package com.mx.daac.controller;

import com.mx.daac.dto.CommentsDto;
import com.mx.daac.service.CommentsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/comments")
public class CommentsController {

    private final CommentsService commentsService;

    @PostMapping
    public ResponseEntity<Void> createComment(@RequestBody CommentsDto commentsDto) {
        commentsService.save(commentsDto);
        return new ResponseEntity<>(HttpStatus.CREATED);

    }

    @GetMapping("/by-postId/{postId}")
    public ResponseEntity<List<CommentsDto>> getAllCommentsForPost(@PathVariable Long postId) {
       return ResponseEntity.status(HttpStatus.OK)
               .body(commentsService.getAllCommentsForPost(postId));
    }

    @GetMapping("/by-user/{userName}")
    public ResponseEntity<List<CommentsDto>> getAllCommentsByUsername(@PathVariable String userName) {
       return ResponseEntity.status(HttpStatus.OK)
                .body(commentsService.getAllCommentsByUsername(userName));


    }


}
