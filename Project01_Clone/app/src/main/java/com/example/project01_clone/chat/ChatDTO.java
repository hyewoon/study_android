package com.example.project01_clone.chat;

import java.io.Serializable;

public class ChatDTO implements Serializable {
    private int imgRes;
    private String name, msg, time;

    public ChatDTO(int imgRes, String name, String msg, String time) {
        this.imgRes= imgRes;
        this.name = name;
        this.msg = msg;
        this.time = time;
    }

    public int getImgRes() {
        return imgRes;
    }

    public void setImgRes(int imgRes) {
        this.imgRes = imgRes;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
