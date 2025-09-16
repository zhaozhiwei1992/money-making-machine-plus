export type AnalysisTotalTypes = {
  loginCount: number
  userCount: number
  requestLogCount: number
  todayRequestLogCount: number
}

export type UserAccessSource = {
  value: number
  name: string
}

export type WeeklyUserActivity = {
  value: number
  name: string
}

export type MonthlyUserActivity = {
  name: string
  estimate: number
  actual: number
}
