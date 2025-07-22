package com.nhnacademy.illuwa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nhnacademy.illuwa.dto.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemberLoginResponse {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("expires_in")
    private Long expiresIn;
    private Status status;

    // ★ 재발급(AccessToken만) 전용
/*    public MemberLoginResponse(String accessToken, long expiresIn) {
        this(accessToken, null, expiresIn);
    }*/
}
