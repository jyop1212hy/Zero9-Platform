package com.zero9platform.domain.post.model.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateRequest {

    @Size(max = 255, message = "제목은 255자 이하여야 합니다.")
    private String title;

    private String content;

    private String image;
}