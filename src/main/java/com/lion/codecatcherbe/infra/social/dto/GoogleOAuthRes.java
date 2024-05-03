package com.lion.codecatcherbe.infra.social.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class GoogleOAuthRes {
    private String access_token;
    private String expires_in;
    private String scope;
    private String token_type;
    private String id_token;
}
