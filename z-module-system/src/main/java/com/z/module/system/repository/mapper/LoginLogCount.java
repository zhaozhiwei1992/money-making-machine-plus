package com.z.module.system.repository.mapper;

public class LoginLogCount {
    private String browser;
    private String os;
    private long count;

    //This constructor will be used by Spring Data JPA
    //for creating this class instances as per result set
    public LoginLogCount(String browser, String os, long count){
        this.browser = browser;
        this.os = os;
        this.count = count;
    }

    public String getBrowser() {
        return browser;
    }

    public String getOs() {
        return os;
    }

    public long getCount() {
        return count;
    }
}
