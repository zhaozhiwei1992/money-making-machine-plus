package com.z.module.screen.web.vo;


import lombok.Data;

@Data
public class GoViewFileVO {
	
    private String id;
    private String fileName;
    private Integer fileSize;
    private String fileSuffix;
    private String createTime;
    
    /**
     * 相对路径
     */
    private String relativePath;
    
    /**
     * 虚拟路径key
     */
    private String virtualKey;
    
    /**
     * 请求url
     */
    private String fileurl;
    
}
