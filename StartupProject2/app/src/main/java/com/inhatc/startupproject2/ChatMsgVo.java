package com.inhatc.startupproject2;

public class ChatMsgVo {
    private String userName;
    private String crt_dt;
    private String content;
    private String language;

    public ChatMsgVo(){

    }

    public ChatMsgVo(String userName, String crt_dt, String content, String language) {
        this.userName = userName;
        this.crt_dt = crt_dt;
        this.content = content;
        this.language = language;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCrt_dt() {
        return crt_dt;
    }

    public void setCrt_dt(String crt_dt) {
        this.crt_dt = crt_dt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return "ChatMsgVo{" +
                "userName='" + userName + '\'' +
                ", crt_dt='" + crt_dt + '\'' +
                ", content='" + content + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
