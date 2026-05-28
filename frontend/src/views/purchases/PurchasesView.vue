<template>
  <div class="page-card page-panel">
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="query.keyword" clearable placeholder="搜索采购单号 / 书名 / 供应商" style="width: 300px" />
        <el-select v-model="query.status" clearable placeholder="状态" style="width: 160px">
          <el-option label="待采购" :value="0" />
          <el-option label="已下单" :value="1" />
          <el-option label="已到货" :value="2" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
      </div>
      <el-button type="primary" @click="openDialog()">新增采购单</el-button>
    </div>

    <el-table :data="tableData" border>
      <el-table-column prop="purchaseNo" label="采购单号" min-width="140" />
      <el-table-column prop="bookTitle" label="图书名称" min-width="180" />
      <el-table-column prop="author" label="作者" min-width="120" />
      <el-table-column prop="supplier" label="供应商" min-width="120" />
      <el-table-column prop="quantity" label="数量" width="90" />
      <el-table-column prop="unitPrice" label="单价" width="100" />
      <el-table-column prop="expectedDate" label="预计到货" min-width="120" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="statusTypeMap[row.status]">{{ statusMap[row.status] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="{ row }">
          <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
          <el-button v-if="row.status !== 2" link type="success" @click="openReceiveDialog(row)">到货入库</el-button>
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑采购单' : '新增采购单'" width="760px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="96px">
        <div class="form-grid">
          <el-form-item label="采购单号" prop="purchaseNo"><el-input v-model="form.purchaseNo" /></el-form-item>
          <el-form-item label="图书名称" prop="bookTitle"><el-input v-model="form.bookTitle" /></el-form-item>
          <el-form-item label="作者" prop="author"><el-input v-model="form.author" /></el-form-item>
          <el-form-item label="ISBN"><el-input v-model="form.isbn" /></el-form-item>
          <el-form-item label="出版社"><el-input v-model="form.publisher" /></el-form-item>
          <el-form-item label="供应商"><el-input v-model="form.supplier" /></el-form-item>
          <el-form-item label="采购数量" prop="quantity"><el-input-number v-model="form.quantity" :min="1" style="width: 100%" /></el-form-item>
          <el-form-item label="单价"><el-input-number v-model="form.unitPrice" :min="0" :precision="2" style="width: 100%" /></el-form-item>
          <el-form-item label="预计到货"><el-date-picker v-model="form.expectedDate" type="date" value-format="YYYY-MM-DD" /></el-form-item>
          <el-form-item label="状态">
            <el-select v-model="form.status">
              <el-option label="待采购" :value="0" />
              <el-option label="已下单" :value="1" />
              <el-option label="已到货" :value="2" />
            </el-select>
          </el-form-item>
        </div>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="receiveDialogVisible" title="采购到货入库" width="560px">
      <el-form ref="receiveFormRef" :model="receiveForm" :rules="receiveRules" label-width="96px">
        <el-form-item label="采购单ID">
          <el-input v-model="receiveForm.purchaseId" disabled />
        </el-form-item>
        <el-form-item label="图书分类" prop="categoryId">
          <el-input-number v-model="receiveForm.categoryId" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="馆藏位置">
          <el-input v-model="receiveForm.location" />
        </el-form-item>
        <el-form-item label="图书简介">
          <el-input v-model="receiveForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="封面地址">
          <el-input v-model="receiveForm.coverUrl" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="receiveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReceive">确认入库</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { deletePurchase, getPurchasePage, receivePurchase, savePurchase } from "@/api/purchases";

const query = reactive({ current: 1, size: 10, keyword: "", status: null });
const pagination = reactive({ total: 0 });
const tableData = ref([]);
const dialogVisible = ref(false);
const receiveDialogVisible = ref(false);
const formRef = ref();
const receiveFormRef = ref();

const statusMap = {
  0: "待采购",
  1: "已下单",
  2: "已到货"
};

const statusTypeMap = {
  0: "warning",
  1: "",
  2: "success"
};

const createEmptyForm = () => ({
  id: null,
  purchaseNo: "",
  bookTitle: "",
  author: "",
  isbn: "",
  publisher: "",
  quantity: 1,
  unitPrice: 0,
  expectedDate: "",
  supplier: "",
  status: 0,
  remark: ""
});

const form = reactive(createEmptyForm());
const receiveForm = reactive({
  purchaseId: null,
  categoryId: null,
  location: "",
  description: "",
  coverUrl: ""
});

const rules = {
  purchaseNo: [{ required: true, message: "请输入采购单号", trigger: "blur" }],
  bookTitle: [{ required: true, message: "请输入图书名称", trigger: "blur" }],
  author: [{ required: true, message: "请输入作者", trigger: "blur" }],
  quantity: [{ required: true, message: "请输入采购数量", trigger: "change" }]
};

const receiveRules = {
  categoryId: [{ required: true, message: "请输入分类 ID", trigger: "change" }]
};

async function loadData() {
  const { data } = await getPurchasePage(query);
  tableData.value = data.records;
  pagination.total = data.total;
}

function openDialog(row) {
  Object.assign(form, createEmptyForm(), row || {});
  dialogVisible.value = true;
}

async function submitForm() {
  await formRef.value.validate();
  await savePurchase(form);
  ElMessage.success("保存成功");
  dialogVisible.value = false;
  loadData();
}

async function handleDelete(id) {
  await ElMessageBox.confirm("确认删除该采购单？", "提示", { type: "warning" });
  await deletePurchase(id);
  ElMessage.success("删除成功");
  loadData();
}

function openReceiveDialog(row) {
  receiveForm.purchaseId = row.id;
  receiveForm.categoryId = null;
  receiveForm.location = "";
  receiveForm.description = "";
  receiveForm.coverUrl = "";
  receiveDialogVisible.value = true;
}

async function submitReceive() {
  await receiveFormRef.value.validate();
  await receivePurchase(receiveForm);
  ElMessage.success("入库成功");
  receiveDialogVisible.value = false;
  loadData();
}

function handlePageChange(page) {
  query.current = page;
  loadData();
}

onMounted(loadData);
</script>

<style scoped>
.page-panel {
  padding: 22px;
}
</style>
