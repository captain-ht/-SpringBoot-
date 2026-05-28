<template>
  <div class="page-card page-panel">
    <div class="toolbar">
      <div class="section-title">分类管理</div>
      <el-button type="primary" @click="openDialog()">新增分类</el-button>
    </div>
    <el-table :data="tableData" border>
      <el-table-column prop="name" label="分类名称" />
      <el-table-column prop="code" label="分类编码" />
      <el-table-column prop="sortOrder" label="排序" width="100" />
      <el-table-column prop="description" label="说明" min-width="220" />
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑分类' : '新增分类'" width="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="88px">
        <el-form-item label="分类名称" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="分类编码"><el-input v-model="form.code" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sortOrder" :min="0" style="width: 100%" /></el-form-item>
        <el-form-item label="说明"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { deleteCategory, getCategories, saveCategory } from "@/api/categories";

const tableData = ref([]);
const dialogVisible = ref(false);
const formRef = ref();

const createEmptyForm = () => ({
  id: null,
  name: "",
  code: "",
  description: "",
  sortOrder: 0
});

const form = reactive(createEmptyForm());
const rules = {
  name: [{ required: true, message: "请输入分类名称", trigger: "blur" }]
};

async function loadData() {
  const { data } = await getCategories();
  tableData.value = data;
}

function openDialog(row) {
  Object.assign(form, createEmptyForm(), row || {});
  dialogVisible.value = true;
}

async function submitForm() {
  await formRef.value.validate();
  await saveCategory(form);
  ElMessage.success("保存成功");
  dialogVisible.value = false;
  loadData();
}

async function handleDelete(id) {
  await ElMessageBox.confirm("确认删除该分类？", "提示", { type: "warning" });
  await deleteCategory(id);
  ElMessage.success("删除成功");
  loadData();
}

onMounted(loadData);
</script>

<style scoped>
.page-panel {
  padding: 22px;
}
</style>
