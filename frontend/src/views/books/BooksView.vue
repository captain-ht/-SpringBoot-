<template>
  <div class="page-card page-panel">
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="query.keyword" clearable placeholder="搜索书名 / 作者 / ISBN" style="width: 260px" />
        <el-select v-model="query.categoryId" clearable placeholder="分类" style="width: 160px">
          <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
        <el-select v-model="query.status" clearable placeholder="状态" style="width: 140px">
          <el-option label="可用" :value="1" />
          <el-option label="停用" :value="0" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
      </div>
      <div class="toolbar-right">
        <el-button @click="handleExport">导出 Excel</el-button>
        <el-button type="primary" @click="openDialog()">新增图书</el-button>
      </div>
    </div>

    <el-table :data="tableData" border>
      <el-table-column prop="bookCode" label="图书编码" min-width="120" />
      <el-table-column prop="title" label="图书名称" min-width="180" />
      <el-table-column prop="author" label="作者" min-width="120" />
      <el-table-column prop="isbn" label="ISBN" min-width="140" />
      <el-table-column prop="totalStock" label="总库存" width="90" />
      <el-table-column prop="availableStock" label="可借" width="80" />
      <el-table-column prop="borrowedCount" label="借出" width="80" />
      <el-table-column prop="location" label="位置" width="120" />
      <el-table-column label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? "可用" : "停用" }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑图书' : '新增图书'" width="760px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="92px">
        <div class="form-grid">
          <el-form-item label="图书编码" prop="bookCode"><el-input v-model="form.bookCode" /></el-form-item>
          <el-form-item label="ISBN" prop="isbn"><el-input v-model="form.isbn" /></el-form-item>
          <el-form-item label="图书名称" prop="title"><el-input v-model="form.title" /></el-form-item>
          <el-form-item label="作者" prop="author"><el-input v-model="form.author" /></el-form-item>
          <el-form-item label="分类" prop="categoryId">
            <el-select v-model="form.categoryId" placeholder="选择分类">
              <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="出版社"><el-input v-model="form.publisher" /></el-form-item>
          <el-form-item label="出版日期"><el-date-picker v-model="form.publishDate" type="date" value-format="YYYY-MM-DD" /></el-form-item>
          <el-form-item label="价格"><el-input-number v-model="form.price" :min="0" :precision="2" :step="1" style="width: 100%" /></el-form-item>
          <el-form-item label="总库存" prop="totalStock"><el-input-number v-model="form.totalStock" :min="0" style="width: 100%" /></el-form-item>
          <el-form-item label="馆藏位置"><el-input v-model="form.location" /></el-form-item>
          <el-form-item label="封面地址"><el-input v-model="form.coverUrl" /></el-form-item>
          <el-form-item label="状态">
            <el-select v-model="form.status">
              <el-option label="可用" :value="1" />
              <el-option label="停用" :value="0" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item label="简介">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
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
import { deleteBook, getBookPage, saveBook } from "@/api/books";
import { getCategories } from "@/api/categories";

const query = reactive({ current: 1, size: 10, keyword: "", categoryId: null, status: null });
const pagination = reactive({ total: 0 });
const tableData = ref([]);
const categories = ref([]);
const dialogVisible = ref(false);
const formRef = ref();

const createEmptyForm = () => ({
  id: null,
  bookCode: "",
  isbn: "",
  title: "",
  author: "",
  categoryId: null,
  publisher: "",
  publishDate: "",
  price: 0,
  totalStock: 1,
  location: "",
  coverUrl: "",
  description: "",
  status: 1
});

const form = reactive(createEmptyForm());

const rules = {
  bookCode: [{ required: true, message: "请输入图书编码", trigger: "blur" }],
  isbn: [{ required: true, message: "请输入 ISBN", trigger: "blur" }],
  title: [{ required: true, message: "请输入图书名称", trigger: "blur" }],
  author: [{ required: true, message: "请输入作者", trigger: "blur" }],
  categoryId: [{ required: true, message: "请选择分类", trigger: "change" }],
  totalStock: [{ required: true, message: "请输入库存", trigger: "change" }]
};

async function loadCategories() {
  const { data } = await getCategories();
  categories.value = data;
}

async function loadData() {
  const { data } = await getBookPage(query);
  tableData.value = data.records;
  pagination.total = data.total;
}

function resetForm() {
  Object.assign(form, createEmptyForm());
}

function openDialog(row) {
  resetForm();
  if (row) {
    Object.assign(form, row);
  }
  dialogVisible.value = true;
}

async function submitForm() {
  await formRef.value.validate();
  await saveBook(form);
  ElMessage.success("保存成功");
  dialogVisible.value = false;
  loadData();
}

async function handleDelete(id) {
  await ElMessageBox.confirm("确认删除该图书？", "提示", { type: "warning" });
  await deleteBook(id);
  ElMessage.success("删除成功");
  loadData();
}

function handlePageChange(page) {
  query.current = page;
  loadData();
}

function handleExport() {
  window.open("http://127.0.0.1:8081/api/books/export", "_blank");
}

onMounted(() => {
  loadCategories();
  loadData();
});
</script>

<style scoped>
.page-panel {
  padding: 22px;
}
</style>
