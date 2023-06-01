package com.z.module.system.repository.mapper;

public class LoginLogMonthlyGroupCount {
    private Integer month;
    private long count;

    //This constructor will be used by Spring Data JPA
    //for creating this class instances as per result set
    public LoginLogMonthlyGroupCount(Integer month, long count){
        this.month = month;
        this.count = count;
    }

    public Integer getMonth() {
        return month;
    }

    public long getCount() {
        return count;
    }
}
