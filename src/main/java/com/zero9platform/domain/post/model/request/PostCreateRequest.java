package com.zero9platform.domain.post.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zero9platform.common.enums.PostType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequest {

    @NotNull(message = "게시물 타입은 필수입니다.")
    private PostType postType;

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 255, message = "제목은 255자 이하여야 합니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @JsonProperty("isSecret")
    private boolean isSecret;

    private String password;
}
