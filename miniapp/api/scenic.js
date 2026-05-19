import { get } from '@/utils/request'

export const getScenicList = () => get('/api/app/scenic/list')

export const getAreaList = () => get('/api/app/area/list')
