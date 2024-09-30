package com.z.framework.job.web.rest;

import com.z.framework.common.web.rest.errors.BadRequestAlertException;
import com.z.framework.job.annotation.JobDescription;
import com.z.framework.job.domain.TaskParam;
import com.z.framework.job.repository.TaskParamRepository;
import com.z.framework.job.service.CustomJobInterface;
import com.z.framework.job.service.QuartzJobExecuteService;
import com.z.framework.job.service.QuartzJobManagerService;
import io.swagger.v3.oas.annotations.Operation;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.*;

/**
 * REST controller for managing {@link TaskParam}.
 */
@RestController
@Transactional
public class TaskParamResource {

    private final Logger log = LoggerFactory.getLogger(TaskParamResource.class);

    private static final String ENTITY_NAME = "taskParam";

    private final TaskParamRepository taskParamRepository;

    public TaskParamResource(TaskParamRepository taskParamRepository, QuartzJobManagerService quartzJobManagerService) {
        this.taskParamRepository = taskParamRepository;
        this.quartzJobManagerService = quartzJobManagerService;
    }

    /**
     * {@code POST  /task-params} : Create a new taskParam.
     *
     * @param taskParam the taskParam to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new taskParam, or with
     * status {@code 400 (Bad Request)} if the taskParam has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/task-params")
    public TaskParam createTaskParam(@RequestBody TaskParam taskParam) throws URISyntaxException {
        log.debug("REST request to save TaskParam : {}", taskParam);
        if (taskParam.getId() != null) {
            throw new BadRequestAlertException("A new taskParam cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return taskParamRepository.save(taskParam);
    }

    /**
     * {@code GET  /task-params} : get all the taskParams.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of taskParams in body.
     */
    @GetMapping("/task-params")
    public HashMap<String, Object> getAllTaskParams(Pageable pageable) {
        log.debug("REST request to get a page of TaskParams");
        // 根据id, 升序
        Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
        // 分页
        pageable = PageRequest.of(pageable.getPageNumber() - 1, pageable.getPageSize(), sort);
        Page<TaskParam> page = taskParamRepository.findAll(pageable);
        return new HashMap<>() {{
            put("list", page.getContent());
            put("total", Long.valueOf(page.getTotalElements()).intValue());
        }};
    }

    /**
     * {@code GET  /task-params/:id} : get the "id" taskParam.
     *
     * @param id the id of the taskParam to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the taskParam, or with status
     * {@code 404 (Not Found)}.
     */
    @GetMapping("/task-params/{id}")
    public TaskParam getTaskParam(@PathVariable Long id) {
        log.debug("REST request to get TaskParam : {}", id);
        Optional<TaskParam> taskParam = taskParamRepository.findById(id);
        return taskParam.orElse(new TaskParam());
    }

    /**
     * @param idList :
     * @data: 2022/6/29-下午10:09
     * @User: zhaozhiwei
     * @method: deleteSelectTaskParam
     * @return: org.springframework.http.ResponseEntity<java.lang.Void>
     * @Description: 删除选中的任务
     */
    @DeleteMapping("/task-params")
    public String deleteSelectTaskParam(@RequestBody List<Long> idList) {
        log.debug("REST request to delete select TaskParam.id : {}", idList);
        final List<TaskParam> delList = taskParamRepository.findAllById(idList);
        for (TaskParam taskParam : delList) {
            if (taskParam.getEnable()) {
                throw new RuntimeException("请先停用定时任务!");
            }
        }
        taskParamRepository.deleteAllById(idList);
        return "success";
    }

    private final QuartzJobManagerService quartzJobManagerService;

    @PostMapping("/task-params/start")
    public String startSelectTaskParam(@RequestBody List<Long> idList) {
        log.debug("REST request to start select TaskParam.id : {}", idList);

        final List<TaskParam> executeList = taskParamRepository.findAllById(idList);
        executeList.forEach(taskParam -> {
            taskParam.setEnable(true);
            try {
                quartzJobManagerService.startJob(
                    taskParam.getCronExpression(),
                    taskParam.getStartClass(),
                    "defaultGroup",
                    QuartzJobExecuteService.class
                );
            } catch (SchedulerException e) {
                log.error("定时任务启用异常: " + taskParam.getName(), e);
            }
        });

//        利用hibernate session自动提交, 这里不需要保存, 待测试
//        taskParamRepository.saveAll(executeList);

        return "success";
    }

    @PostMapping("/task-params/stop")
    public String stopSelectTaskParam(@RequestBody List<Long> idList) {
        log.debug("REST request to stop select TaskParam.id : {}", idList);

        final List<TaskParam> executeList = taskParamRepository.findAllById(idList);
        executeList.forEach(taskParam -> {
            taskParam.setEnable(false);
            try {
                quartzJobManagerService.deleteJob(taskParam.getStartClass(), "defaultGroup");
            } catch (SchedulerException e) {
                log.error("定时任务停用异常: {}", taskParam.getName(), e);
            }
        });

//        利用Hibernate 自动更新
//        taskParamRepository.saveAll(executeList);
        return "success";
    }

    // 所有的job实现
    @Autowired
    private Map<String, CustomJobInterface> jobs;

    @Operation(description = "获取定时任务树")
    @GetMapping("/task-params/select")
    public List<Map<String, Object>> getJobSelect() {
        log.debug("REST request to get Position Select");
        List<Map<String, Object>> result = new ArrayList<>();
        // 获取所有实现bean名, 同时根据注解获取描述信息
        for (Map.Entry<String, CustomJobInterface> entry : jobs.entrySet()) {
            String key = entry.getKey();
            CustomJobInterface value = entry.getValue();
            Map<String, Object> map = new HashMap<>();
            map.put("value", key + "#execute");
            map.put("id", key);
            map.put("label", value.getClass().getAnnotation(JobDescription.class).value());
            result.add(map);
        }
        return result;
    }
}
