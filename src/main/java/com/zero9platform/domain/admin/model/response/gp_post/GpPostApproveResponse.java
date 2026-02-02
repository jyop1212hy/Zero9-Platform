package com.zero9platform.domain.admin.model.response.gp_post;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GpPostApproveResponse {

    private final Long gppId;
    private final String status;
}
