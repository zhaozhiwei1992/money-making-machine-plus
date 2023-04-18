package com.z.framework.job.web.rest;

import com.z.framework.job.domain.TaskParam;
import com.z.framework.job.repository.TaskParamRepository;
import com.z.framework.job.service.QuartzJobExecuteService;
import com.z.framework.job.service.QuartzJobManagerService;
import com.z.framework.common.web.rest.errors.BadRequestAlertException;
import com.z.framework.common.web.util.HeaderUtil;
import com.z.framework.common.web.util.PaginationUtil;
import com.z.framework.common.web.util.ResponseUtil;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for managing {@link TaskParam}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class TaskParamResource {

    private final Logger log = LoggerFactory.getLogger(TaskParamResource.class);

    private static final String ENTITY_NAME = "taskParam";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

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
    public ResponseEntity<TaskParam> createTaskParam(@RequestBody TaskParam taskParam) throws URISyntaxException {
        log.debug("REST request to save TaskParam : {}", taskParam);
        if (taskParam.getId() != null) {
            throw new BadRequestAlertException("A new taskParam cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TaskParam result = taskParamRepository.save(taskParam);
        return ResponseEntity
            .created(new URI("/api/task-params/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /task-params/:id} : Updates an existing taskParam.
     *
     * @param id        the id of the taskParam to save.
     * @param taskParam the taskParam to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taskParam,
     * or with status {@code 400 (Bad Request)} if the taskParam is not valid,
     * or with status {@code 500 (Internal Server Error)} if the taskParam couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/task-params/{id}")
    public ResponseEntity<TaskParam> updateTaskParam(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TaskParam taskParam
    ) throws URISyntaxException {
        log.debug("REST request to update TaskParam : {}, {}", id, taskParam);
        if (taskParam.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taskParam.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskParamRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TaskParam result = taskParamRepository.save(taskParam);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, taskParam.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /task-params/:id} : Partial updates given fields of an existing taskParam, field will ignore if
     * it is null
     *
     * @param id        the id of the taskParam to save.
     * @param taskParam the taskParam to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated taskParam,
     * or with status {@code 400 (Bad Request)} if the taskParam is not valid,
     * or with status {@code 404 (Not Found)} if the taskParam is not found,
     * or with status {@code 500 (Internal Server Error)} if the taskParam couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/task-params/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TaskParam> partialUpdateTaskParam(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TaskParam taskParam
    ) throws URISyntaxException {
        log.debug("REST request to partial update TaskParam partially : {}, {}", id, taskParam);
        if (taskParam.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, taskParam.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!taskParamRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TaskParam> result = taskParamRepository
            .findById(taskParam.getId())
            .map(existingTaskParam -> {
                if (taskParam.getName() != null) {
                    existingTaskParam.setName(taskParam.getName());
                }
                if (taskParam.getCronExpression() != null) {
                    existingTaskParam.setCronExpression(taskParam.getCronExpression());
                }
                if (taskParam.getStartClass() != null) {
                    existingTaskParam.setStartClass(taskParam.getStartClass());
                }
                if (taskParam.getEnable() != null) {
                    existingTaskParam.setEnable(taskParam.getEnable());
                }

                return existingTaskParam;
            })
            .map(taskParamRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, taskParam.getId().toString())
        );
    }

    /**
     * {@code GET  /task-params} : get all the taskParams.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of taskParams in body.
     */
    @GetMapping("/task-params")
    public ResponseEntity<List<TaskParam>> getAllTaskParams(Pageable pageable) {
        log.debug("REST request to get a page of TaskParams");
        Page<TaskParam> page = taskParamRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /task-params/:id} : get the "id" taskParam.
     *
     * @param id the id of the taskParam to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the taskParam, or with status
     * {@code 404 (Not Found)}.
     */
    @GetMapping("/task-params/{id}")
    public ResponseEntity<TaskParam> getTaskParam(@PathVariable Long id) {
        log.debug("REST request to get TaskParam : {}", id);
        Optional<TaskParam> taskParam = taskParamRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(taskParam);
    }

    /**
     * {@code DELETE  /task-params/:id} : delete the "id" taskParam.
     *
     * @param id the id of the taskParam to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/task-params/{id}")
    public ResponseEntity<Void> deleteTaskParam(@PathVariable Long id) {
        log.debug("REST request to delete TaskParam : {}", id);
        taskParamRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * @param delList :
     * @data: 2022/6/29-下午10:09
     * @User: zhaozhiwei
     * @method: deleteSelectTaskParam
     * @return: org.springframework.http.ResponseEntity<java.lang.Void>
     * @Description: 删除选中的任务
     */
    @PostMapping("/task-params/del")
    public ResponseEntity<Void> deleteSelectTaskParam(@RequestBody List<TaskParam> delList) {
        log.debug("REST request to delete select TaskParam : {}", delList);
        for (TaskParam taskParam : delList) {
            if (taskParam.getEnable()) {
                throw new RuntimeException("请先停用定时任务!");
            }
        }
        final List<Long> delIds = delList.stream().map(taskParam -> taskParam.getId()).collect(Collectors.toList());
        taskParamRepository.deleteAllById(delIds);
        return ResponseEntity.noContent().build();
    }

    private final QuartzJobManagerService quartzJobManagerService;

    @PostMapping("/task-params/start")
    public ResponseEntity<Void> startSelectTaskParam(@RequestBody List<TaskParam> executeList) {
        log.debug("REST request to start select TaskParam : {}", executeList);

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

        taskParamRepository.saveAll(executeList);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/task-params/stop")
    public ResponseEntity<Void> stopSelectTaskParam(@RequestBody List<TaskParam> executeList) {
        log.debug("REST request to stop select TaskParam : {}", executeList);

        executeList.forEach(taskParam -> {
            taskParam.setEnable(false);
            try {
                quartzJobManagerService.deleteJob(taskParam.getStartClass(), "defaultGroup");
            } catch (SchedulerException e) {
                log.error("定时任务停用异常: " + taskParam.getName(), e);
            }
        });

        taskParamRepository.saveAll(executeList);

        return ResponseEntity.noContent().build();
    }
}
