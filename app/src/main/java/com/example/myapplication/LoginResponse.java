package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("token")
    private String token;

    // Getter for token
    public String getToken() {
        return token;
    }
}
