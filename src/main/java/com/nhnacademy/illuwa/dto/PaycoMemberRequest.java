package com.nhnacademy.illuwa.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaycoMemberRequest {
    private String idNo;  //paycoId
    private String name;
    private String email;
    private String mobile;
    private String birthdayMMdd;

    public static PaycoMemberRequest of(SocialLoginRequest req) {
        return new PaycoMemberRequest(
                req.getProviderId(),
                (String) req.getAttributes().get("name"),
                (String) req.getAttributes().get("email"),
                (String) req.getAttributes().get("mobile"),
                (String) req.getAttributes().get("birthdayMMdd")
        );
    }
}
