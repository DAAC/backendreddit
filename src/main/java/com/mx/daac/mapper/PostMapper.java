package com.mx.daac.mapper;

import com.mx.daac.dto.PostRequest;
import com.mx.daac.dto.PostResponse;
import com.mx.daac.model.Post;
import com.mx.daac.model.Subreddit;
import com.mx.daac.model.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PostMapper {

    @Mapping(target = "createDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "description", source = "postRequest.description")
    @Mapping(target = "subreddit", source = "subreddit")
    @Mapping(target = "user", source = "users")
    Post map(PostRequest postRequest, Subreddit subreddit, Users users);

    @Mapping(target = "id", source = "postId")
    @Mapping(target = "subredditName", source = "subreddit.name")
    @Mapping(target = "userName", source = "user.username")
    PostResponse mapToDto(Post post);

}
