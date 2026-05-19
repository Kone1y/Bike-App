import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/dashboard/index.vue'), meta: { title: '首页' } },
      { path: 'system/user', name: 'User', component: () => import('@/views/system/user/index.vue'), meta: { title: '用户管理' } },
      { path: 'system/role', name: 'Role', component: () => import('@/views/system/role/index.vue'), meta: { title: '角色管理' } },
      { path: 'system/menu', name: 'Menu', component: () => import('@/views/system/menu/index.vue'), meta: { title: '菜单管理' } },
      { path: 'system/log', name: 'Log', component: () => import('@/views/system/log/index.vue'), meta: { title: '操作日志' } },
      { path: 'bike/list', name: 'Bike', component: () => import('@/views/bike/index.vue'), meta: { title: '车辆管理' } },
      { path: 'scenic/spot', name: 'ScenicSpot', component: () => import('@/views/scenic/spot/index.vue'), meta: { title: '景点管理' } },
      { path: 'scenic/area', name: 'ParkingArea', component: () => import('@/views/scenic/area/index.vue'), meta: { title: '停车区域' } },
      { path: 'repair/list', name: 'Repair', component: () => import('@/views/repair/index.vue'), meta: { title: '报修管理' } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 共享单车管理` : '共享单车管理'
  const userStore = useUserStore()
  if (to.path !== '/login' && !userStore.token) {
    next('/login')
  } else {
    next()
  }
})

export default router
