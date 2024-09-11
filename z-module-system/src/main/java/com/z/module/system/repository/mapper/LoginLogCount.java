package com.z.module.system.repository.mapper;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginLogCount {
    private String browser;
    private String os;
    private long count;
}
