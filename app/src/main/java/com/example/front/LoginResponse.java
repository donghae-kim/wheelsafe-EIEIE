package com.example.front;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
//    @SerializedName("islogined")
    public boolean islogined;

//    @SerializedName("email")
    public String email;

    public Boolean getislogined() {
        return islogined;
    }

    public void setislogined(boolean islogined) {
        this.islogined = islogined;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}