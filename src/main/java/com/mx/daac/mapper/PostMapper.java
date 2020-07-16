package com.mx.daac.mapper;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.mx.daac.dto.PostRequest;
import com.mx.daac.dto.PostResponse;
import com.mx.daac.model.Post;
import com.mx.daac.model.Subreddit;
import com.mx.daac.model.Users;
import com.mx.daac.repository.CommentRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;


@Mapper(componentModel = "spring")
public abstract class PostMapper {

    @Autowired
    private CommentRepository commentRepository;

    @Mapping(target = "createDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "description", source = "postRequest.description")
    @Mapping(target = "subreddit", source = "subreddit")
    @Mapping(target = "user", source = "users")
    @Mapping(target = "voteCount", constant = "0")
    public abstract Post map(PostRequest postRequest, Subreddit subreddit, Users users);

    @Mapping(target = "id", source = "postId")
    @Mapping(target = "subredditName", source = "subreddit.name")
    @Mapping(target = "userName", source = "user.username")
    @Mapping(target = "commentCount", expression = "java(commentCount(post))")
    @Mapping(target = "duration", expression = "java(getDuration(post))")
    public abstract PostResponse mapToDto(Post post);

    Integer commentCount(Post post){return  commentRepository.findByPost(post).size();}

    String getDuration(Post post){return TimeAgo.using(post.getCreateDate().toEpochMilli());
    }


}
