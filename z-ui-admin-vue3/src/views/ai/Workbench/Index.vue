<template>
    <div class="indexnavHome">
        <div class="h-out-box">
            <el-scrollbar style="height: calc(100% - 5px); margin: 20px 0">
                <!-- 使用 el-row 和 el-col 实现整体居中布局 -->
                <el-row type="flex" justify="center">
                    <el-col :xs="22" :sm="18" :md="16" :lg="12">
                        <div class="content-wrapper">
                            <!-- 主标题部分 -->
                            <h1 class="main-title">
                                <span class="brand-name">华青小财</span>
                                <span class="divider">·</span>
                                <span class="feature-name">应用广场</span>
                            </h1>

                            <!-- 副标题/说明文字 -->
                            <p class="subtitle">
                                为财政工作者打造的AI办公伙伴，让专业服务更简单，让专业工作更高效省力
                            </p>
                        </div>
                    </el-col>
                </el-row>
                <el-row :gutter="20">
                    <el-col :span="8" v-for="(item, index) in datalist" :key="index">
                        <el-card class="box-card">
                            <img :src="item.picture" alt="" />
                            <h2 class="feature-title">{{ item.remark }}</h2>
                            <p class="feature-subtitle">语义直达，一键办理</p>
                            <p class="feature-description">
                                精准解析用户业务需求，自动匹配系统功能模块，实现"描述即跳转"的服务直达。
                                通过AI语义理解与知识图谱关联，消除传统菜单层级，提升操作效率，打造智能化服务体验。
                            </p>
                            <div class="action-buttons">
                                <el-button type="text">了解详情</el-button>
                                <el-button type="primary" @click="protalAi(item)">我要体验</el-button>
                            </div>
                        </el-card>
                    </el-col>
                </el-row>
            </el-scrollbar>
        </div>
    </div>
</template>

<script setup>
import { getCookie } from "@/utils/utils";
import { ElMessage } from 'element-plus';
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter()
const datalist = ref([])

onMounted(() => {
    getQueryAll()
})

const getQueryAll = async () => {
    let params = { wheresql: "1=1 and bus_TYPE = '01'" }
    try {
        const ret = await $PostingAi(
            "/ifmisLLM/plantformconfig/1.0.0.0/querybywheresql?aitoken=" +
            getCookie("token"),
            params
        )
        if (ret.code === "200") {
            datalist.value = ret.data
        }
    } catch (error) {
        ElMessage.error('获取数据失败: ' + error.message)
    }
}

const protalAi = (item) => {
    router.push({
        path: "/home/protalAi",
        query: {
            ishome: 1,
            api_code: item.code,
            token: getCookie("token"),
        }
    })
}
</script>

<style lang="less" scoped>
/* 样式部分保持不变 */
.indexnavHome {
    margin: 15px;
    height: calc(100% - 30px);
    background-color: #fff;
    box-shadow: 0px 0px 10px 0px rgba(153, 153, 153, 0.15);
    border-radius: 15px;

    .h-out-box {
        width: calc(100% - 40px);
        height: calc(100% - 40px);
        border-radius: 8px;
        padding: 20px;
        overflow: hidden;

        .ct-widget {
            padding: 20px;
            border: 1px solid #eaeaea;
            border-radius: 8px;
            cursor: pointer;

            img {
                width: calc(100% - 10px);
                height: calc(100% - 20px);
                display: block;
            }

            .title {
                height: 20px;
                margin-top: 15px;
                font-size: 16px;
                font-weight: bold;
                color: #333;
                line-height: 20px;
            }

            .subtitle {
                height: 20px;
                font-size: 12px;
                line-height: 20px;
                color: #666;
            }
        }
    }
}

img {
    width: calc(100% - 10px);
    height: calc(100% - 20px);
    display: block;
}

.feature-content {
    display: flex;
    align-items: flex-start;
}

.feature-icon {
    font-size: 48px;
    color: #409eff;
    margin-right: 20px;
}

.feature-text {
    flex: 1;
}

.feature-title {
    font-size: 24px;
    font-weight: bold;
    margin-top: 18px;
    margin-bottom: 8px;
    color: #1f2f3d;
}

.feature-subtitle {
    font-size: 16px;
    color: #5e6d82;
    margin-bottom: 15px;
}

.feature-description {
    color: #606266;
    line-height: 1.6;
}

.box-card {
    margin-bottom: 30px;
}

.content-wrapper {
    margin: 0 auto;
}

.main-title {
    margin-bottom: 24px;
    font-weight: normal;
    line-height: 1.2;
}

.brand-name {
    display: inline-block;
    padding: 4px 8px;
    background-color: #e6f7ff;
    color: #1890ff;
    border-radius: 4px;
    font-size: 32px;
    font-weight: 500;
}

.divider {
    margin: 0 8px;
    color: #000;
    font-size: 32px;
    font-weight: normal;
}

.feature-name {
    color: #000;
    font-size: 32px;
    font-weight: 500;
}

.subtitle {
    color: #666;
    font-size: 16px;
    line-height: 1.5;
    margin: 0;
    max-width: 400px;
    margin-left: auto;
    margin-right: auto;
    margin-bottom: 20px;
}

:deep(.el-scrollbar__wrap) {
    overflow-x: hidden !important;
}
</style>