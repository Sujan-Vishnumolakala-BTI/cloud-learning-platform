// package com.learningplatform.user_service.dto;

// public class LoginResponse {

//     private String token;
//     private String tokenType;

//     public LoginResponse(String token) {
//         this.token = token;
//         this.tokenType = "Bearer";
//     }

//     public String getToken() {
//         return token;
//     }

//     public String getTokenType() {
//         return tokenType;
//     }
// }

package com.learningplatform.user_service.dto;

public class LoginResponse {

    private String accessToken;
    private String refreshToken;

    public LoginResponse(
            String accessToken,
            String refreshToken) {

        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}