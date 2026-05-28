<template>
  <div class="page-card page-panel">
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="query.keyword" clearable placeholder="搜索姓名 / 编号 / 手机号" style="width: 280px" />
        <el-select v-model="query.status" clearable placeholder="状态" style="width: 140px">
          <el-option label="正常" :value="1" />
          <el-option label="停用" :value="0" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
      </div>
      <div class="toolbar-right">
        <el-upload :show-file-list="false" :auto-upload="false" :on-change="handleImport">
          <el-button>导入读者</el-button>
        </el-upload>
        <el-button @click="handleExport">导出读者</el-button>
        <el-button type="primary" @click="openDialog()">新增读者</el-button>
      </div>
    </div>

    <el-table :data="tableData" border>
      <el-table-column prop="readerNo" label="读者编号" min-width="120" />
      <el-table-column prop="name" label="姓名" min-width="100" />
      <el-table-column prop="readerType" label="类型" width="100">
        <template #default="{ row }">{{ row.readerType === 1 ? "教师" : "学生" }}</template>
      </el-table-column>
      <el-table-column prop="phone" label="手机号" min-width="140" />
      <el-table-column prop="department" label="部门" min-width="120" />
      <el-table-column prop="maxBorrowCount" label="最大借阅" width="100" />
      <el-table-column prop="borrowDays" label="借阅天数" width="100" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? "正常" : "停用" }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="table-pagination">
      <el-pagination
        background
        layout="total, prev, pager, next"
        :total="pagination.total"
        :current-page="query.current"
        :page-size="query.size"
        @current-change="handlePageChange"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑读者' : '新增读者'" width="720px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="92px">
        <div class="form-grid">
          <el-form-item label="读者编号" prop="readerNo"><el-input v-model="form.readerNo" /></el-form-item>
          <el-form-item label="姓名" prop="name"><el-input v-model="form.name" /></el-form-item>
          <el-form-item label="读者类型" prop="readerType">
            <el-select v-model="form.readerType">
              <el-option label="教师" :value="1" />
              <el-option label="学生" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="性别"><el-input v-model="form.gender" /></el-form-item>
          <el-form-item label="手机号" prop="phone"><el-input v-model="form.phone" /></el-form-item>
          <el-form-item label="邮箱"><el-input v-model="form.email" /></el-form-item>
          <el-form-item label="部门"><el-input v-model="form.department" /></el-form-item>
          <el-form-item label="注册日期"><el-date-picker v-model="form.registerDate" type="date" value-format="YYYY-MM-DD" /></el-form-item>
          <el-form-item label="最大借阅"><el-input-number v-model="form.maxBorrowCount" :min="1" style="width: 100%" /></el-form-item>
          <el-form-item label="借阅天数"><el-input-number v-model="form.borrowDays" :min="1" style="width: 100%" /></el-form-item>
          <el-form-item label="状态">
            <el-select v-model="form.status">
              <el-option label="正常" :value="1" />
              <el-option label="停用" :value="0" />
            </el-select>
          </el-form-item>
        </div>
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
import { deleteReader, getReaderPage, importReaders, saveReader } from "@/api/readers";
import { downloadFile } from "@/utils/download";

const query = reactive({ current: 1, size: 10, keyword: "", status: null });
const pagination = reactive({ total: 0 });
const tableData = ref([]);
const dialogVisible = ref(false);
const formRef = ref();

const createEmptyForm = () => ({
  id: null,
  readerNo: "",
  name: "",
  readerType: 1,
  gender: "",
  phone: "",
  email: "",
  department: "",
  registerDate: "",
  maxBorrowCount: 5,
  borrowDays: 30,
  status: 1
});

const form = reactive(createEmptyForm());

const rules = {
  readerNo: [{ required: true, message: "请输入读者编号", trigger: "blur" }],
  name: [{ required: true, message: "请输入姓名", trigger: "blur" }],
  readerType: [{ required: true, message: "请选择读者类型", trigger: "change" }],
  phone: [{ required: true, message: "请输入手机号", trigger: "blur" }]
};

async function loadData() {
  const { data } = await getReaderPage(query);
  tableData.value = data.records;
  pagination.total = data.total;
}

function openDialog(row) {
  Object.assign(form, createEmptyForm(), row || {});
  dialogVisible.value = true;
}

async function submitForm() {
  await formRef.value.validate();
  await saveReader(form);
  ElMessage.success("保存成功");
  dialogVisible.value = false;
  loadData();
}

async function handleDelete(id) {
  await ElMessageBox.confirm("确认删除该读者？", "提示", { type: "warning" });
  await deleteReader(id);
  ElMessage.success("删除成功");
  loadData();
}

function handlePageChange(page) {
  query.current = page;
  loadData();
}

async function handleImport(file) {
  const formData = new FormData();
  formData.append("file", file.raw);
  await importReaders(formData);
  ElMessage.success("导入成功");
  loadData();
}

function handleExport() {
  downloadFile("/api/readers/export", "readers.xlsx");
}

onMounted(loadData);
</script>

<style scoped>
.page-panel {
  padding: 22px;
}
</style>
