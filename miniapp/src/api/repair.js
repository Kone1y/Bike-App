import { post } from '@/utils/request'

export const submitRepair = (bikeCode, description, images) =>
  post('/api/app/repair/submit', { bikeCode, description, images })
