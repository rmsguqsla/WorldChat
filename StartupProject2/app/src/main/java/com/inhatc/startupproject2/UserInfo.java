package com.inhatc.startupproject2;

public class UserInfo {
    private String name;
    private String language;

    public UserInfo(String name, String language) {
        this.name = name;
        this.language = language;
    }

    public UserInfo(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
