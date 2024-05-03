package com.lion.codecatcherbe.infra.social.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class GoogleInfoRes {
    private String iss;
    private String azp;
    private String aud;
    private String sub;
    private String email;
    private boolean email_verified;
    private String at_hash;
    private int iat;
    private int exp;
}
