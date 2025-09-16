package com.z.module.system.repository.mapper;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginLogMonthlyGroupCount {
    private Integer month;
    private long count;
}
