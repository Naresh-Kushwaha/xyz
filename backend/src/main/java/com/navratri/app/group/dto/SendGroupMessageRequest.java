package com.navratri.app.group.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SendGroupMessageRequest(
        @NotBlank @Size(max = 2000) String content
) {
}
