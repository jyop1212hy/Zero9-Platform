package com.zero9platform.domain.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GroupPurchasePostStatusChangedEvent implements Serializable {
    private Long groupPurchasePostId;
    private String groupPurchasePostTitle;
    private String oldStatus;
    private String newStatus;
}
