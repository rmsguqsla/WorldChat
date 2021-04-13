package com.inhatc.startupproject;

import java.util.HashMap;
import java.util.Map;

public class User {
    public String id;
    public String pw;
    public String name;
    public String language;

    public User (String id, String pw, String fname, String lname, String language) {
        this.id = id;
        this.pw = pw;
        this.name = fname + " " + lname;
        this.language = language;
    }

    public void setId(String id) {this.id = id;}
    public String getId() {return id;}

    public void setPw(String pw) {this.pw = pw;}
    public String getPw() {return pw;}

    public void setName(String fname, String lname) {
        this.name = fname + " " + lname;}
    public String getName() {return name;}

    public void setLanguage(String language) {
        this.language = language;}
    public String getLanguage() {return language;}

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("pw",pw);
        result.put("name",name);
        result.put("language",language);
        return result;
    }
}
