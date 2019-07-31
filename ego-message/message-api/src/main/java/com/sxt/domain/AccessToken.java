package com.sxt.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author WWF
 * @title: AccessToken
 * @projectName ego
 * @description: com.sxt.domain
 * @date 2019/6/2 12:31
 */
public class AccessToken {
    /**
     * 获得access_token
     */
    @JsonProperty(value = "access_token")
    private String accessToken;
    /**
     * token的过期时间
     */
    @JsonProperty(value = "expires_in")
    private String expiresIn;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(String expiresIn) {
        this.expiresIn = expiresIn;
    }
    public AccessToken(){}
    public AccessToken(String accessToken, String expiresIn) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }
}
