package com.jiucom.api.domain.search.service;

import com.jiucom.api.domain.build.entity.Build;
import com.jiucom.api.domain.build.repository.BuildRepository;
import com.jiucom.api.domain.part.entity.Part;
import com.jiucom.api.domain.part.repository.PartRepository;
import com.jiucom.api.domain.post.entity.Post;
import com.jiucom.api.domain.post.repository.PostRepository;
import com.jiucom.api.domain.search.dto.response.SearchResultResponse;
import com.jiucom.api.domain.search.dto.response.SuggestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final PartRepository partRepository;
    private final PostRepository postRepository;
    private final BuildRepository buildRepository;

    public SearchResultResponse search(String keyword, int size) {
        Pageable pageable = PageRequest.of(0, size);

        List<Part> parts = partRepository.searchParts(keyword, null, null, null, pageable).getContent();
        List<Post> posts = postRepository.searchPosts(keyword, null, pageable).getContent();
        List<Build> builds = buildRepository.findByIsPublicTrueAndIsDeletedFalse(pageable).getContent()
                .stream()
                .filter(b -> keyword == null || keyword.isBlank()
                        || b.getName().toLowerCase().contains(keyword.toLowerCase())
                        || (b.getDescription() != null && b.getDescription().toLowerCase().contains(keyword.toLowerCase())))
                .toList();

        return SearchResultResponse.builder()
                .parts(parts.stream().map(this::toPartResult).toList())
                .posts(posts.stream().map(this::toPostResult).toList())
                .builds(builds.stream().map(this::toBuildResult).toList())
                .build();
    }

    public SuggestResponse suggest(String keyword) {
        List<String> partNames = partRepository.suggestNames(keyword, 5);
        List<String> postTitles = postRepository.suggestTitles(keyword, 5);

        return SuggestResponse.builder()
                .partNames(partNames)
                .postTitles(postTitles)
                .build();
    }

    private SearchResultResponse.PartResult toPartResult(Part part) {
        return SearchResultResponse.PartResult.builder()
                .id(part.getId())
                .name(part.getName())
                .category(part.getCategory().name())
                .manufacturer(part.getManufacturer())
                .lowestPrice(part.getLowestPrice())
                .imageUrl(part.getImageUrl())
                .build();
    }

    private SearchResultResponse.PostResult toPostResult(Post post) {
        return SearchResultResponse.PostResult.builder()
                .id(post.getId())
                .title(post.getTitle())
                .boardType(post.getBoardType().name())
                .authorNickname(post.getAuthor().getNickname())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .build();
    }

    private SearchResultResponse.BuildResult toBuildResult(Build build) {
        return SearchResultResponse.BuildResult.builder()
                .id(build.getId())
                .name(build.getName())
                .description(build.getDescription())
                .totalPrice(build.getTotalPrice())
                .likeCount(build.getLikeCount())
                .userNickname(build.getUser().getNickname())
                .build();
    }
}
