package com.jiucom.api.domain.post.repository;

import com.jiucom.api.domain.post.entity.Post;
import com.jiucom.api.domain.post.entity.enums.BoardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepositoryCustom {

    Page<Post> searchPosts(String keyword, BoardType boardType, Pageable pageable);

    List<String> suggestTitles(String keyword, int limit);
}
