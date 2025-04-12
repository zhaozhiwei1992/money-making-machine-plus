package com.z.module.system.web.vo;

import lombok.Data;

@Data
public class OnLineUserVO {
    private Long id;
    private String nowTime;
    private String os;
    private String ip;
    private String userName;
    private String browser;
    private String tokenId;
}
