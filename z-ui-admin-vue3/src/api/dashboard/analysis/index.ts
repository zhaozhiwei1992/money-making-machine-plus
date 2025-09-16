import request from '@/config/axios'
import type {
  AnalysisTotalTypes,
  MonthlyUserActivity,
  UserAccessSource,
  WeeklyUserActivity
} from './types'

export const getCountApi = (): Promise<IResponse<AnalysisTotalTypes[]>> => {
  return request.get({ url: '/system/analysis/total' })
}

export const getUserAccessSourceApi = (): Promise<IResponse<UserAccessSource[]>> => {
  return request.get({ url: '/system/analysis/userAccessSource' })
}

export const getWeeklyUserActivityApi = (): Promise<IResponse<WeeklyUserActivity[]>> => {
  return request.get({ url: '/system/analysis/weeklyUserActivity' })
}

export const getMonthlyUserActivityApi = (): Promise<IResponse<MonthlyUserActivity[]>> => {
  return request.get({ url: '/system/analysis/monthlyUserActivity' })
}
