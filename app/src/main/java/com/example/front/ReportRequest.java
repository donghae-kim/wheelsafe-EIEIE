package com.example.front;

import java.io.File;

public class ReportRequest {

    public String title;
    public String content;
    public String address;
    public File filename;

    public String getTitle() {
        return title;
    }
    public String getContent() {
        return content;
    }
    public String getAddresss() {
        return address;
    }
    public File getFilename() {
        return filename;
    }


    public void setTitle(String title) {
        this.title = title;

    }
    public void setContent(String content) {
        this.content = content;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setFilename(File filename) {
        this.filename = filename;
    }


    public ReportRequest(String title, String content,String address,File filename) {
        this.title = title;
        this.content = content;
        this.address = address;
        this.filename = filename;
    }
}
