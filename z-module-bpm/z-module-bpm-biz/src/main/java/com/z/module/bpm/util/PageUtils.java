package com.z.module.bpm.util;

import com.z.module.bpm.web.vo.PageParam;

/**
 * {@link cn.iocoder.yudao.framework.common.pojo.PageParam} 工具类
 *
 * @author 芋道源码
 */
public class PageUtils {

    /**
     * @data: 2023/5/24-下午5:21
     * @User: zhaozhiwei
     * @method: getStart
      * @param pageParam :
     * @return: int
     * @Description: flowable工作流查询组件分页特殊, 需要返回startIndex
     */
    public static int getStart(PageParam pageParam) {
        return (pageParam.getPageNo() - 1) * pageParam.getPageSize();
    }

}
