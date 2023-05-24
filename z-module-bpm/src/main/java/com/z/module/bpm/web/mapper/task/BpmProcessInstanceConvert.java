package com.z.module.bpm.web.mapper.task;

import com.z.framework.common.web.rest.vm.PageResult;
import com.z.module.bpm.domain.definition.BpmProcessDefinitionExtDO;
import com.z.module.bpm.domain.task.BpmProcessInstanceExtDO;
import com.z.module.bpm.framework.bpm.core.event.BpmProcessInstanceResultEvent;
import com.z.module.bpm.service.dto.AdminUserRespDTO;
import com.z.module.bpm.service.dto.DeptRespDTO;
import com.z.module.bpm.service.message.dto.BpmMessageSendWhenProcessInstanceApproveReqDTO;
import com.z.module.bpm.service.message.dto.BpmMessageSendWhenProcessInstanceRejectReqDTO;
import com.z.module.bpm.web.vo.instance.BpmProcessInstancePageItemRespVO;
import com.z.module.bpm.web.vo.instance.BpmProcessInstanceRespVO;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

/**
 * 流程实例 Convert
 *
 * @author 芋道源码
 */
@Mapper(componentModel = "spring")
public interface BpmProcessInstanceConvert {

    BpmProcessInstanceConvert INSTANCE = Mappers.getMapper(BpmProcessInstanceConvert.class);

    default PageResult<BpmProcessInstancePageItemRespVO> convertPage(PageResult<BpmProcessInstanceExtDO> page,
                                                                     Map<String, List<Task>> taskMap) {
        List<BpmProcessInstancePageItemRespVO> list = convertList(page.getList());
        list.forEach(respVO -> respVO.setTasks(convertList2(taskMap.get(respVO.getId()))));
        return new PageResult<>(list, page.getTotal());
    }

    List<BpmProcessInstancePageItemRespVO> convertList(List<BpmProcessInstanceExtDO> list);

    @Mapping(source = "processInstanceId", target = "id")
    BpmProcessInstancePageItemRespVO convert(BpmProcessInstanceExtDO bean);

    List<BpmProcessInstancePageItemRespVO.Task> convertList2(List<Task> tasks);

    default BpmProcessInstanceRespVO convert2(HistoricProcessInstance processInstance,
                                              BpmProcessInstanceExtDO processInstanceExt,
                                              ProcessDefinition processDefinition,
                                              BpmProcessDefinitionExtDO processDefinitionExt,
                                              String bpmnXml
            , AdminUserRespDTO startUser, DeptRespDTO dept
    ) {
        BpmProcessInstanceRespVO respVO = convert2(processInstance);
        copyTo(processInstanceExt, respVO);
        // definition
        respVO.setProcessDefinition(convert2(processDefinition));
        copyTo(processDefinitionExt, respVO.getProcessDefinition());
        respVO.getProcessDefinition().setBpmnXml(bpmnXml);
        // user
//        if (startUser != null) {
//            respVO.setStartUser(convert2(startUser));
//            if (dept != null) {
//                respVO.getStartUser().setDeptName(dept.getName());
//            }
//        }
        return respVO;
    }

    BpmProcessInstanceRespVO convert2(HistoricProcessInstance bean);

    @Mapping(source = "from.id", target = "to.id", ignore = true)
    void copyTo(BpmProcessInstanceExtDO from, @MappingTarget BpmProcessInstanceRespVO to);

    BpmProcessInstanceRespVO.ProcessDefinition convert2(ProcessDefinition bean);

    @Mapping(source = "from.id", target = "to.id", ignore = true)
    void copyTo(BpmProcessDefinitionExtDO from, @MappingTarget BpmProcessInstanceRespVO.ProcessDefinition to);

//    BpmProcessInstanceRespVO.User convert2(AdminUserRespDTO bean);

    default BpmProcessInstanceResultEvent convert(Object source, HistoricProcessInstance instance, Integer result) {
        BpmProcessInstanceResultEvent event = new BpmProcessInstanceResultEvent(source);
        event.setId(instance.getId());
        event.setProcessDefinitionKey(instance.getProcessDefinitionKey());
        event.setBusinessKey(instance.getBusinessKey());
        event.setResult(result);
        return event;
    }

    default BpmProcessInstanceResultEvent convert(Object source, ProcessInstance instance, Integer result) {
        BpmProcessInstanceResultEvent event = new BpmProcessInstanceResultEvent(source);
        event.setId(instance.getId());
        event.setProcessDefinitionKey(instance.getProcessDefinitionKey());
        event.setBusinessKey(instance.getBusinessKey());
        event.setResult(result);
        return event;
    }

    default BpmMessageSendWhenProcessInstanceApproveReqDTO convert2ApprovedReq(ProcessInstance instance) {
        final BpmMessageSendWhenProcessInstanceApproveReqDTO bpmMessageSendWhenProcessInstanceApproveReqDTO =
                new BpmMessageSendWhenProcessInstanceApproveReqDTO();
        bpmMessageSendWhenProcessInstanceApproveReqDTO.setStartUserId(Long.parseLong(instance.getStartUserId()));
        bpmMessageSendWhenProcessInstanceApproveReqDTO.setProcessInstanceId(instance.getId());
        bpmMessageSendWhenProcessInstanceApproveReqDTO.setProcessInstanceName(instance.getName());
        return bpmMessageSendWhenProcessInstanceApproveReqDTO;
    }

    default BpmMessageSendWhenProcessInstanceRejectReqDTO convert2RejectReq(ProcessInstance instance, String reason) {
        final BpmMessageSendWhenProcessInstanceRejectReqDTO bpmMessageSendWhenProcessInstanceRejectReqDTO =
                new BpmMessageSendWhenProcessInstanceRejectReqDTO();
        bpmMessageSendWhenProcessInstanceRejectReqDTO.setProcessInstanceName(instance.getName());
        bpmMessageSendWhenProcessInstanceRejectReqDTO.setProcessInstanceId(instance.getId());
        bpmMessageSendWhenProcessInstanceRejectReqDTO.setReason(reason);
        bpmMessageSendWhenProcessInstanceRejectReqDTO.setStartUserId(Long.parseLong(instance.getStartUserId()));
        return bpmMessageSendWhenProcessInstanceRejectReqDTO;
    }

}
