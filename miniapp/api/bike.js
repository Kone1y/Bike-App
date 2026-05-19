import { get, post } from '@/utils/request'

export const getNearbyBikes = (lng, lat, radius = 3000) =>
  get('/api/app/bike/nearby', { lng, lat, radius })

export const rentBike = (bikeCode, startLng, startLat) =>
  post('/api/app/bike/rent', { bikeCode, startLng, startLat })
