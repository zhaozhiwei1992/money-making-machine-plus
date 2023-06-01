import request from '@/config/axios'
import type {
  AnalysisTotalTypes,
  UserAccessSource,
  WeeklyUserActivity,
  MonthlyUserActivity
} from './types'

export const getCountApi = (): Promise<IResponse<AnalysisTotalTypes[]>> => {
  return request.get({ url: '/analysis/total' })
}

export const getUserAccessSourceApi = (): Promise<IResponse<UserAccessSource[]>> => {
  return request.get({ url: '/analysis/userAccessSource' })
}

export const getWeeklyUserActivityApi = (): Promise<IResponse<WeeklyUserActivity[]>> => {
  return request.get({ url: '/analysis/weeklyUserActivity' })
}

export const getMonthlyUserActivityApi = (): Promise<IResponse<MonthlyUserActivity[]>> => {
  return request.get({ url: '/analysis/monthlyUserActivity' })
}
