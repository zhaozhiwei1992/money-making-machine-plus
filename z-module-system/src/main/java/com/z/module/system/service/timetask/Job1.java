package com.z.module.system.service.timetask;

import com.z.framework.job.annotation.JobDescription;
import com.z.framework.job.service.CustomJobInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * @Title: Job1
 * @Package com/longtu/service/timetask/Job1.java
 * @Description:
 * 定时任务测试
 * @author zhaozhiwei
 * @date 2022/7/27 下午4:39
 * @version V1.0
 */
@Service
@JobDescription("测试定时任务1")
public class Job1 implements CustomJobInterface {

    private static final Logger log = LoggerFactory.getLogger(Job1.class);

    public void execute() {
        log.info("{} 执行了...", Thread.currentThread().getStackTrace()[1].getMethodName());
        // 测试定时任务耗时
//        Thread.sleep(1000);

        // 测试数据库连接
//        final List<Entity> user = Db.use().findAll("user");
        final Random random = new Random();
        if(random.nextInt(10) > 5){
            throw new RuntimeException("测试定时任务异常");
        }
    }
}
