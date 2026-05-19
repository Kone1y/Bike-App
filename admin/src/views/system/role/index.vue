<template>
  <div>
    <el-card>
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="角色名称">
          <el-input v-model="searchForm.roleName" placeholder="请输入角色名称" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="正常" :value="0" />
            <el-option label="禁用" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch"><el-icon><Search /></el-icon>搜索</el-button>
          <el-button @click="handleReset"><el-icon><Refresh /></el-icon>重置</el-button>
        </el-form-item>
      </el-form>

      <div style="margin-bottom: 16px">
        <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon>新增角色</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="roleName" label="角色名称" width="130" />
        <el-table-column prop="roleCode" label="角色编码" width="150" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'" size="small">
              {{ row.status === 0 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link size="small" @click="handleAssignMenu(row)">分配权限</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" destroy-on-close>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="form.roleCode" placeholder="请输入角色编码（如 OPERATOR）" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入角色描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" style="width: 100%">
            <el-option label="正常" :value="0" />
            <el-option label="禁用" :value="1" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配权限弹窗 -->
    <el-dialog v-model="menuDialogVisible" title="分配菜单权限" width="500px" destroy-on-close>
      <div style="margin-bottom: 12px; color: #909399; font-size: 14px;">
        当前角色：{{ currentRole.roleName }}（{{ currentRole.roleCode }}）
      </div>
      <el-tree
        ref="menuTreeRef"
        :data="menuTree"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedMenuIds"
        :props="{ label: 'menuName', children: 'children' }"
        style="max-height: 400px; overflow-y: auto;"
      />
      <template #footer>
        <el-button @click="menuDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleMenuSubmit" :loading="menuSubmitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { getRoleList, addRole, editRole, deleteRole, getRoleMenuIds, assignRoleMenus } from '@/api/role'
import { getMenuTree } from '@/api/menu'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref(null)
const dialogTitle = ref('')

const menuDialogVisible = ref(false)
const menuTreeRef = ref(null)
const menuTree = ref([])
const checkedMenuIds = ref([])
const currentRole = reactive({ id: null, roleName: '', roleCode: '' })
const menuSubmitLoading = ref(false)

const searchForm = reactive({ roleName: '', status: null })

const form = reactive({
  id: null, roleName: '', roleCode: '', description: '', status: 0
})

const rules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }]
}

async function loadData() {
  loading.value = true
  try {
    const res = await getRoleList({
      roleName: searchForm.roleName || undefined,
      status: searchForm.status
    })
    tableData.value = res.data || []
  } catch (e) {} finally {
    loading.value = false
  }
}

function handleSearch() { loadData() }

function handleReset() {
  searchForm.roleName = ''
  searchForm.status = null
  loadData()
}

function handleAdd() {
  isEdit.value = false
  dialogTitle.value = '新增角色'
  Object.assign(form, { id: null, roleName: '', roleCode: '', description: '', status: 0 })
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  dialogTitle.value = '编辑角色'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

async function handleSubmit() {
  try { await formRef.value.validate() } catch { return }
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await editRole(form)
      ElMessage.success('编辑成功')
    } else {
      await addRole(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {} finally {
    submitLoading.value = false
  }
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定删除角色"${row.roleName}"吗？`, '提示', { type: 'warning' })
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {}
}

async function handleAssignMenu(row) {
  currentRole.id = row.id
  currentRole.roleName = row.roleName
  currentRole.roleCode = row.roleCode
  try {
    const [treeRes, idsRes] = await Promise.all([getMenuTree(), getRoleMenuIds(row.id)])
    menuTree.value = treeRes.data || []
    checkedMenuIds.value = idsRes.data || []
    menuDialogVisible.value = true
    await nextTick()
    menuTreeRef.value?.setCheckedKeys(checkedMenuIds.value)
  } catch (e) {}
}

async function handleMenuSubmit() {
  menuSubmitLoading.value = true
  try {
    const checkedKeys = menuTreeRef.value.getCheckedKeys()
    const halfCheckedKeys = menuTreeRef.value.getHalfCheckedKeys()
    const menuIds = [...checkedKeys, ...halfCheckedKeys]
    await assignRoleMenus(currentRole.id, menuIds)
    ElMessage.success('权限分配成功')
    menuDialogVisible.value = false
  } catch (e) {} finally {
    menuSubmitLoading.value = false
  }
}

onMounted(() => { loadData() })
</script>
