import { post } from '@/utils/request'

export const login = (code) => post('/api/app/auth/login', { code })
