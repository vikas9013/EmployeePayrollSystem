package com.vikas.dto;

import lombok.Data;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Schema(description = "Request body containing the refresh token to generate a new JWT")
public class RefreshTokenRequestDTO {
    @Schema(description = "UUID-formatted refresh token string", example = "a25dfd6f-ea90-410a-9d93-3d0b30cb99bb")
    private String refreshToken;
}
