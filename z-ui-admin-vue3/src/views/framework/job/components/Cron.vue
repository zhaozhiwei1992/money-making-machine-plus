<script setup lang="ts">
import { provide, reactive } from 'vue'
import { ElTabs, ElTabPane, ElCard, ElInput, ElRow, ElCol } from 'element-plus'
import CrontabSecond from './Second.vue'
import CrontabMin from './Min.vue'
import CrontabHour from './Hour.vue'
import CrontabDay from './Day.vue'
import CrontabMonth from './Month.vue'
import CrontabWeek from './Week.vue'
import CrontabYear from './Year.vue'
import CrontabResult from './Result.vue'

// 数据校验,整数表示
const checkNumber = (value, minLimit, maxLimit) => {
  value = Math.floor(value)
  if (value < minLimit) {
    value = minLimit
  } else if (value > maxLimit) {
    value = maxLimit
  }
  return value
}

// 将公式传递给子孙组件
provide('checkNumber', checkNumber)
</script>

<template>
  <ElCard class="box-card">
    <ElTabs v-model="activeName" type="card" @tab-click="handleTabClick">
      <ElTabPane label="秒" name="second">
        <CrontabSecond
          @update="updateCrontabValue"
          :check="checkNumber"
          :cron="crontabValueObj"
          ref="cronsecond"
        />
      </ElTabPane>

      <ElTabPane label="分钟" name="min">
        <CrontabMin
          @update="updateCrontabValue"
          :check="checkNumber"
          :cron="crontabValueObj"
          ref="cronmin"
        />
      </ElTabPane>

      <ElTabPane label="小时" name="hour">
        <CrontabHour
          @update="updateCrontabValue"
          :check="checkNumber"
          :cron="crontabValueObj"
          ref="cronhour"
        />
      </ElTabPane>

      <ElTabPane label="日" name="day">
        <CrontabDay
          @update="updateCrontabValue"
          :check="checkNumber"
          :cron="crontabValueObj"
          ref="cronday"
        />
      </ElTabPane>

      <ElTabPane label="月" name="month">
        <CrontabMonth
          @update="updateCrontabValue"
          :check="checkNumber"
          :cron="crontabValueObj"
          ref="cronmonth"
        />
      </ElTabPane>

      <ElTabPane label="周" name="week">
        <CrontabWeek
          @update="updateCrontabValue"
          :check="checkNumber"
          :cron="crontabValueObj"
          ref="cronweek"
        />
      </ElTabPane>

      <ElTabPane label="年" name="year">
        <CrontabYear
          @update="updateCrontabValue"
          :check="checkNumber"
          :cron="crontabValueObj"
          ref="cronyear"
        />
      </ElTabPane>
    </ElTabs>
  </ElCard>
  <ElCard class="box-card">
    <div class="clearfix">
      <span>表达式</span>
    </div>
    <ElRow :gutter="40">
      <ElCol v-for="item of tabTitles" :span="2" :key="item">{{ item }}</ElCol>
      <ElCol :span="10">Cron表达式</ElCol>
    </ElRow>
    <ElRow :gutter="10">
      <ElCol v-for="(value, key) in crontabValueObj" :span="2" :key="key">
        <ElInput v-model="crontabValueObj[key]" :disabled="true" />
      </ElCol>
      <ElCol :span="10">
        <ElInput v-model="crontabValueString" :disabled="true" />
      </ElCol>
    </ElRow>
  </ElCard>

  <ElCard class="box-card">
    <div class="clearfix">
      <CrontabResult :ex="crontabValueString" />
    </div>
  </ElCard>
</template>
