package com.z.module.bpm.service.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.z.framework.common.util.date.DateUtils;
import com.z.framework.common.web.rest.vm.PageResult;
import com.z.module.bpm.domain.task.BpmTaskExtDO;
import com.z.module.bpm.enums.task.BpmProcessInstanceDeleteReasonEnum;
import com.z.module.bpm.enums.task.BpmProcessInstanceResultEnum;
import com.z.module.bpm.repository.task.BpmTaskRepository;
import com.z.module.bpm.service.dto.AdminUserRespDTO;
import com.z.module.bpm.service.dto.DeptRespDTO;
import com.z.module.bpm.service.message.BpmMessageService;
import com.z.module.bpm.util.PageUtils;
import com.z.module.bpm.web.mapper.task.BpmTaskConvert;
import com.z.module.bpm.web.vo.task.*;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import jakarta.annotation.Resource;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.*;

import static com.z.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.z.framework.common.util.collection.CollectionUtils.convertMap;
import static com.z.framework.common.util.collection.CollectionUtils.convertSet;
import static com.z.module.bpm.enums.ErrorCodeConstants.*;

/**
 * 流程任务实例 Service 实现类
 *
 * @author 芋道源码
 * @author jason
 */
@Slf4j
@Service
public class BpmTaskServiceImpl implements BpmTaskService {

    @Resource
    private TaskService taskService;
    @Resource
    private HistoryService historyService;

    @Resource
    private BpmProcessInstanceService processInstanceService;
    //    @Resource
//    private AdminUserApi adminUserApi;
//    @Resource
//    private DeptApi deptApi;
    @Resource
    private BpmTaskRepository taskExtMapper;
    @Resource
    private BpmMessageService messageService;

    @Autowired
    private BpmTaskConvert bpmTaskConvert;

    @Override
    public PageResult<BpmTaskTodoPageItemRespVO> getTodoTaskPage(Long userId, BpmTaskTodoPageReqVO pageVO) {
        // 查询待办任务
        TaskQuery taskQuery = taskService.createTaskQuery().taskAssignee(String.valueOf(userId)) // 分配给自己
                .orderByTaskCreateTime().desc(); // 创建时间倒序
        if (StrUtil.isNotBlank(pageVO.getName())) {
            taskQuery.taskNameLike("%" + pageVO.getName() + "%");
        }
        if (ArrayUtil.get(pageVO.getCreateTime(), 0) != null) {
            taskQuery.taskCreatedAfter(DateUtils.of(pageVO.getCreateTime()[0]));
        }
        if (ArrayUtil.get(pageVO.getCreateTime(), 1) != null) {
            taskQuery.taskCreatedBefore(DateUtils.of(pageVO.getCreateTime()[1]));
        }
        // 执行查询
        List<Task> tasks = taskQuery.listPage(PageUtils.getStart(pageVO), pageVO.getPageSize());
        if (CollUtil.isEmpty(tasks)) {
            return PageResult.empty(taskQuery.count());
        }

        // 获得 ProcessInstance Map
        Map<String, ProcessInstance> processInstanceMap =
                processInstanceService.getProcessInstanceMap(convertSet(tasks, Task::getProcessInstanceId));
        // 获得 User Map
//        Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(
//            convertSet(processInstanceMap.values(), instance -> Long.valueOf(instance.getStartUserId())));
        Map<Long, AdminUserRespDTO> userMap = new HashMap<>();
        // 拼接结果
        return new PageResult<>(BpmTaskConvert.INSTANCE.convertList1(tasks, processInstanceMap, userMap),
                taskQuery.count());
    }

    @Override
    public PageResult<BpmTaskDonePageItemRespVO> getDoneTaskPage(Long userId, BpmTaskDonePageReqVO pageVO) {
        // 查询已办任务
        HistoricTaskInstanceQuery taskQuery = historyService.createHistoricTaskInstanceQuery().finished() // 已完成
                .taskAssignee(String.valueOf(userId)) // 分配给自己
                .orderByHistoricTaskInstanceEndTime().desc(); // 审批时间倒序
        if (StrUtil.isNotBlank(pageVO.getName())) {
            taskQuery.taskNameLike("%" + pageVO.getName() + "%");
        }
        if (pageVO.getBeginCreateTime() != null) {
            taskQuery.taskCreatedAfter(DateUtils.of(pageVO.getBeginCreateTime()));
        }
        if (pageVO.getEndCreateTime() != null) {
            taskQuery.taskCreatedBefore(DateUtils.of(pageVO.getEndCreateTime()));
        }
        // 执行查询
        List<HistoricTaskInstance> tasks = taskQuery.listPage(PageUtils.getStart(pageVO), pageVO.getPageSize());
        if (CollUtil.isEmpty(tasks)) {
            return PageResult.empty(taskQuery.count());
        }

        // 获得 TaskExtDO Map
        List<BpmTaskExtDO> bpmTaskExtDOs =
                taskExtMapper.findAllByTaskIdIn(convertSet(tasks, HistoricTaskInstance::getId));
        Map<String, BpmTaskExtDO> bpmTaskExtDOMap = convertMap(bpmTaskExtDOs, BpmTaskExtDO::getTaskId);
        // 获得 ProcessInstance Map
        Map<String, HistoricProcessInstance> historicProcessInstanceMap =
                processInstanceService.getHistoricProcessInstanceMap(
                        convertSet(tasks, HistoricTaskInstance::getProcessInstanceId));
        // 获得 User Map
//        Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(
//            convertSet(historicProcessInstanceMap.values(), instance -> Long.valueOf(instance.getStartUserId())));
        Map<Long, AdminUserRespDTO> userMap = new HashMap<>();
        // 拼接结果
        return new PageResult<>(
                BpmTaskConvert.INSTANCE.convertList2(tasks, bpmTaskExtDOMap, historicProcessInstanceMap, userMap),
                taskQuery.count());
    }

    @Override
    public List<Task> getTasksByProcessInstanceIds(List<String> processInstanceIds) {
        if (CollUtil.isEmpty(processInstanceIds)) {
            return Collections.emptyList();
        }
        return taskService.createTaskQuery().processInstanceIdIn(processInstanceIds).list();
    }

    @Override
    public List<BpmTaskRespVO> getTaskListByProcessInstanceId(String processInstanceId) {
        // 获得任务列表
        List<HistoricTaskInstance> tasks = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByHistoricTaskInstanceStartTime().desc() // 创建时间倒序
                .list();
        if (CollUtil.isEmpty(tasks)) {
            return Collections.emptyList();
        }

        // 获得 TaskExtDO Map
        List<BpmTaskExtDO> bpmTaskExtDOs = taskExtMapper.findAllByTaskIdIn(convertSet(tasks,
                HistoricTaskInstance::getId));
        Map<String, BpmTaskExtDO> bpmTaskExtDOMap = convertMap(bpmTaskExtDOs, BpmTaskExtDO::getTaskId);
        // 获得 ProcessInstance Map
        HistoricProcessInstance processInstance = processInstanceService.getHistoricProcessInstance(processInstanceId);
        // 获得 User Map
        Set<Long> userIds = convertSet(tasks, task -> Long.parseLong(task.getAssignee()));
        userIds.add(Long.parseLong(processInstance.getStartUserId()));
//        Map<Long, AdminUserRespDTO> userMap = adminUserApi.getUserMap(userIds);
        Map<Long, AdminUserRespDTO> userMap = new HashMap<>();
        // 获得 Dept Map
//        Map<Long, DeptRespDTO> deptMap = deptApi.getDeptMap(convertSet(userMap.values(),
//        AdminUserRespDTO::getDeptId));
        Map<Long, DeptRespDTO> deptMap = new HashMap<>();

        // 拼接数据
        return bpmTaskConvert.convertList3(tasks, bpmTaskExtDOMap, processInstance, userMap, deptMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveTask(Long userId, @Valid BpmTaskApproveReqVO reqVO) {
        // 校验任务存在
        Task task = checkTask(userId, reqVO.getId());
        // 校验流程实例存在
        ProcessInstance instance = processInstanceService.getProcessInstance(task.getProcessInstanceId());
        if (instance == null) {
            throw exception(PROCESS_INSTANCE_NOT_EXISTS);
        }

        // 完成任务，审批通过
        taskService.complete(task.getId(), instance.getProcessVariables());

        // 更新任务拓展表为通过
        Optional<BpmTaskExtDO> taskExtOptional = taskExtMapper.findOneByTaskId(task.getId());
        if (taskExtOptional.isPresent()) {
            final BpmTaskExtDO bpmTaskExtDO = taskExtOptional.get();
            bpmTaskExtDO.setResult(BpmProcessInstanceResultEnum.APPROVE.getResult())
                    .setReason(reqVO.getReason());
        }
//        taskExtMapper.updateByTaskId(
//            new BpmTaskExtDO().setTaskId(task.getId()).setResult(BpmProcessInstanceResultEnum.APPROVE.getResult())
//                .setReason(reqVO.getReason()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectTask(Long userId, @Valid BpmTaskRejectReqVO reqVO) {
        Task task = checkTask(userId, reqVO.getId());
        // 校验流程实例存在
        ProcessInstance instance = processInstanceService.getProcessInstance(task.getProcessInstanceId());
        if (instance == null) {
            throw exception(PROCESS_INSTANCE_NOT_EXISTS);
        }

        // 更新流程实例为不通过
        processInstanceService.updateProcessInstanceExtReject(instance.getProcessInstanceId(), reqVO.getReason());

        // 更新任务拓展表为不通过
        Optional<BpmTaskExtDO> taskExtOptional = taskExtMapper.findOneByTaskId(task.getId());
        if (taskExtOptional.isPresent()) {
            final BpmTaskExtDO bpmTaskExtDO = taskExtOptional.get();
            bpmTaskExtDO.setResult(BpmProcessInstanceResultEnum.REJECT.getResult())
                    .setEndTime(LocalDateTime.now()).setReason(reqVO.getReason());
        }
//        taskExtMapper.updateByTaskId(
//            new BpmTaskExtDO().setTaskId(task.getId()).setResult(BpmProcessInstanceResultEnum.REJECT.getResult())
//                    .setEndTime(LocalDateTime.now()).setReason(reqVO.getReason()));
    }

    @Override
    public void updateTaskAssignee(Long userId, BpmTaskUpdateAssigneeReqVO reqVO) {
        // 校验任务存在
        Task task = checkTask(userId, reqVO.getId());
        // 更新负责人
        updateTaskAssignee(task.getId(), reqVO.getAssigneeUserId());
    }

    @Override
    public void updateTaskAssignee(String id, Long userId) {
        taskService.setAssignee(id, String.valueOf(userId));
    }

    /**
     * 校验任务是否存在， 并且是否是分配给自己的任务
     *
     * @param userId 用户 id
     * @param taskId task id
     */
    private Task checkTask(Long userId, String taskId) {
        Task task = getTask(taskId);
        if (task == null) {
            throw exception(TASK_COMPLETE_FAIL_NOT_EXISTS);
        }
        if (!Objects.equals(userId, Long.parseLong(task.getAssignee()))) {
            throw exception(TASK_COMPLETE_FAIL_ASSIGN_NOT_SELF);
        }
        return task;
    }

    @Override
    public void createTaskExt(Task task) {
        BpmTaskExtDO taskExtDO =
                bpmTaskConvert.convert2TaskExt(task).setResult(BpmProcessInstanceResultEnum.PROCESS.getResult());
        taskExtMapper.save(taskExtDO);
    }

    @Override
    public void updateTaskExtComplete(Task task) {
        Optional<BpmTaskExtDO> taskExtOptional = taskExtMapper.findOneByTaskId(task.getId());
        if (taskExtOptional.isPresent()) {
            final BpmTaskExtDO bpmTaskExtDO = taskExtOptional.get();
            bpmTaskExtDO.setResult(BpmProcessInstanceResultEnum.APPROVE.getResult())
                    // 不设置也问题不大，因为 Complete
                    // 一般是审核通过，已经设置
                    .setEndTime(LocalDateTime.now());
        }
//        BpmTaskExtDO taskExtDO = BpmTaskConvert.INSTANCE.convert2TaskExt(task)
//                .setResult(BpmProcessInstanceResultEnum.APPROVE.getResult()) // 不设置也问题不大，因为 Complete 一般是审核通过，已经设置
//                .setEndTime(LocalDateTime.now());
//        taskExtMapper.updateByTaskId(taskExtDO);
    }

    @Override
    public void updateTaskExtCancel(String taskId) {
        // 需要在事务提交后，才进行查询。不然查询不到历史的原因
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {

            @Override
            public void afterCommit() {
                // 可能只是活动，不是任务，所以查询不到
                HistoricTaskInstance task = getHistoricTask(taskId);
                if (task == null) {
                    return;
                }

                // 如果任务拓展表已经是完成的状态，则跳过
                Optional<BpmTaskExtDO> taskExtOptional = taskExtMapper.findOneByTaskId(taskId);
                if (!taskExtOptional.isPresent()) {
                    log.error("[updateTaskExtCancel][taskId({}) 查找不到对应的记录，可能存在问题]", taskId);
                    return;
                }
                BpmTaskExtDO taskExt = taskExtOptional.get();
                // 如果已经是最终的结果，则跳过
                if (BpmProcessInstanceResultEnum.isEndResult(taskExt.getResult())) {
                    log.error("[updateTaskExtCancel][taskId({}) 处于结果({})，无需进行更新]", taskId, taskExt.getResult());
                    return;
                }

                // 更新任务
                final Optional<BpmTaskExtDO> byId = taskExtMapper.findById(taskExt.getId());
                if (byId.isPresent()) {
                    final BpmTaskExtDO bpmTaskExtDO = byId.get();
                    bpmTaskExtDO.setResult(BpmProcessInstanceResultEnum.CANCEL.getResult())
                            .setEndTime(LocalDateTime.now()).setReason(BpmProcessInstanceDeleteReasonEnum.translateReason(task.getDeleteReason()));
                }
//                taskExtMapper.updateById(new BpmTaskExtDO().setId(taskExt.getId()).setResult
//                (BpmProcessInstanceResultEnum.CANCEL.getResult())
//                        .setEndTime(LocalDateTime.now()).setReason(BpmProcessInstanceDeleteReasonEnum
//                        .translateReason(task.getDeleteReason())));
            }

        });
    }

    @Override
    public void updateTaskExtAssign(Task task) {
        Optional<BpmTaskExtDO> taskExtOptional = taskExtMapper.findOneByTaskId(task.getId());
        if (taskExtOptional.isPresent()) {
            final BpmTaskExtDO bpmTaskExtDO = taskExtOptional.get();
            bpmTaskExtDO.setAssigneeUserId(Long.parseLong(task.getAssignee()));
        }
//        BpmTaskExtDO taskExtDO =
//                new BpmTaskExtDO().setAssigneeUserId(Long.parseLong(task.getAssignee())).setTaskId(task.getId());
//        taskExtMapper.updateByTaskId(taskExtDO);
        // 发送通知。在事务提交时，批量执行操作，所以直接查询会无法查询到 ProcessInstance，所以这里是通过监听事务的提交来实现。
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                ProcessInstance processInstance =
                        processInstanceService.getProcessInstance(task.getProcessInstanceId());
//                AdminUserRespDTO startUser = adminUserApi.getUser(Long.valueOf(processInstance.getStartUserId()));
                AdminUserRespDTO startUser = new AdminUserRespDTO();
                messageService.sendMessageWhenTaskAssigned(
                        BpmTaskConvert.INSTANCE.convert(processInstance, startUser, task));
            }
        });
    }

    private Task getTask(String id) {
        return taskService.createTaskQuery().taskId(id).singleResult();
    }

    private HistoricTaskInstance getHistoricTask(String id) {
        return historyService.createHistoricTaskInstanceQuery().taskId(id).singleResult();
    }

}
