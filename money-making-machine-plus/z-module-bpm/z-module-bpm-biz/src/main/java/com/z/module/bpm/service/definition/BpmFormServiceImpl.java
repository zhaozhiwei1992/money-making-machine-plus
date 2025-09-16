package com.z.module.bpm.service.definition;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import com.z.framework.common.web.rest.vm.PageResult;
import com.z.module.bpm.domain.definition.BpmFormDO;
import com.z.module.bpm.enums.ErrorCodeConstants;
import com.z.module.bpm.enums.definition.BpmModelFormTypeEnum;
import com.z.module.bpm.repository.definition.BpmFormRepository;
import com.z.module.bpm.service.dto.definition.BpmFormFieldRespDTO;
import com.z.module.bpm.service.dto.definition.BpmModelMetaInfoRespDTO;
import com.z.module.bpm.web.mapper.definition.BpmFormConvert;
import com.z.module.bpm.web.vo.definition.form.BpmFormCreateReqVO;
import com.z.module.bpm.web.vo.definition.form.BpmFormPageReqVO;
import com.z.module.bpm.web.vo.definition.form.BpmFormUpdateReqVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.*;

import static com.z.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * 动态表单 Service 实现类
 *
 * @author 风里雾里
 */
@Service
@Validated
public class BpmFormServiceImpl implements BpmFormService {

    @Autowired
    private BpmFormRepository bpmFormRepository;

    @Override
    public Long createForm(BpmFormCreateReqVO createReqVO) {
        this.checkFields(createReqVO.getFields());
        BpmFormDO form = BpmFormConvert.INSTANCE.convert(createReqVO);
        return bpmFormRepository.save(form).getId();
    }

    @Override
    public void updateForm(BpmFormUpdateReqVO updateReqVO) {
        this.checkFields(updateReqVO.getFields());
        // 校验存在
        this.validateFormExists(updateReqVO.getId());
        // 更新
        BpmFormDO updateObj = BpmFormConvert.INSTANCE.convert(updateReqVO);
        bpmFormRepository.save(updateObj);
    }

    @Override
    public void deleteForm(Long id) {
        // 校验存在
        this.validateFormExists(id);
        // 删除
        bpmFormRepository.deleteById(id);
    }

    private void validateFormExists(Long id) {
        if (!bpmFormRepository.findById(id).isPresent()) {
            throw exception(ErrorCodeConstants.FORM_NOT_EXISTS);
        }
    }

    @Override
    public BpmFormDO getForm(Long id) {
        return bpmFormRepository.findById(id).get();
    }

    @Override
    public List<BpmFormDO> getFormList() {
        return bpmFormRepository.findAll();
    }

    @Override
    public List<BpmFormDO> getFormList(Collection<Long> ids) {
        return bpmFormRepository.findAllById(ids);
    }

    @Override
    public PageResult<BpmFormDO> getFormPage(BpmFormPageReqVO pageReqVO) {
        final PageRequest page = PageRequest.of(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        Page<BpmFormDO> all;
        if (StringUtils.hasText(pageReqVO.getName())){
            all = bpmFormRepository.findAllByName(page, pageReqVO.getName());
        }else{
            all = bpmFormRepository.findAll(page);
        }
        return new PageResult<>(all.getContent(), all.getTotalElements());
    }


    @Override
    public BpmFormDO checkFormConfig(String configStr) {
        BpmModelMetaInfoRespDTO metaInfo = JSON.parseObject(configStr, BpmModelMetaInfoRespDTO.class);
        if (metaInfo == null || metaInfo.getFormType() == null) {
            throw exception(ErrorCodeConstants.MODEL_DEPLOY_FAIL_FORM_NOT_CONFIG);
        }
        // 校验表单存在
        if (Objects.equals(metaInfo.getFormType(), BpmModelFormTypeEnum.NORMAL.getType())) {
            BpmFormDO form = getForm(metaInfo.getFormId());
            if (form == null) {
                throw exception(ErrorCodeConstants.FORM_NOT_EXISTS);
            }
            return form;
        }
        return null;
    }

    /**
     * 校验 Field，避免 field 重复
     *
     * @param fields field 数组
     */
    private void checkFields(List<String> fields) {
        if (true) { // TODO 芋艿：兼容 Vue3 工作流：因为采用了新的表单设计器，所以暂时不校验
            return;
        }
        // key 是 vModel，value 是 label
        Map<String, String> fieldMap = new HashMap<>();
        for (String field : fields) {
            BpmFormFieldRespDTO fieldDTO = JSON.parseObject(field, BpmFormFieldRespDTO.class);
            Assert.notNull(fieldDTO);
            String oldLabel = fieldMap.put(fieldDTO.getVModel(), fieldDTO.getLabel());
            // 如果不存在，则直接返回
            if (oldLabel == null) {
                continue;
            }
            // 如果存在，则报错
            throw exception(ErrorCodeConstants.FORM_FIELD_REPEAT, oldLabel, fieldDTO.getLabel(), fieldDTO.getVModel());
        }
    }

}
