<template>
  <div class="page-card page-panel">
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input-number v-model="query.readerId" :min="1" placeholder="读者ID" />
        <el-select v-model="query.status" clearable placeholder="支付状态" style="width: 160px">
          <el-option label="未支付" :value="0" />
          <el-option label="已支付" :value="1" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
      </div>
      <el-button @click="handleExport">导出罚款</el-button>
    </div>

    <el-table :data="tableData" border>
      <el-table-column prop="id" label="罚款ID" width="90" />
      <el-table-column label="读者信息" min-width="170">
        <template #default="{ row }">
          <div>{{ row.readerName || "-" }}</div>
          <div class="sub-text">{{ row.readerNo || `ID: ${row.readerId}` }}</div>
        </template>
      </el-table-column>
      <el-table-column label="图书信息" min-width="220">
        <template #default="{ row }">
          <div>{{ row.bookTitle || "-" }}</div>
          <div class="sub-text">{{ row.bookCode || `记录ID: ${row.borrowRecordId}` }} / {{ row.author || "-" }}</div>
        </template>
      </el-table-column>
      <el-table-column prop="borrowDate" label="借阅日期" min-width="120" />
      <el-table-column prop="dueDate" label="应还日期" min-width="120" />
      <el-table-column prop="amount" label="金额" width="100" />
      <el-table-column prop="reason" label="原因" min-width="160" />
      <el-table-column prop="payType" label="支付方式" width="120" />
      <el-table-column prop="payTime" label="支付时间" min-width="160" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? "已支付" : "未支付" }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button v-if="row.status === 0" link type="primary" @click="openPayDialog(row.id)">缴费</el-button>
          <span v-else>已完成</span>
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

    <el-dialog v-model="dialogVisible" title="缴纳罚款" width="420px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="罚款ID"><el-input v-model="form.fineId" disabled /></el-form-item>
        <el-form-item label="支付方式" prop="payType">
          <el-select v-model="form.payType" placeholder="选择支付方式">
            <el-option label="现金" value="现金" />
            <el-option label="微信" value="微信" />
            <el-option label="支付宝" value="支付宝" />
            <el-option label="刷卡" value="刷卡" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPay">确认缴费</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { getFinePage, payFine } from "@/api/fines";
import { downloadFile } from "@/utils/download";

const query = reactive({ current: 1, size: 10, readerId: null, status: null });
const pagination = reactive({ total: 0 });
const tableData = ref([]);
const dialogVisible = ref(false);
const formRef = ref();
const form = reactive({ fineId: null, payType: "" });

const rules = {
  payType: [{ required: true, message: "请选择支付方式", trigger: "change" }]
};

async function loadData() {
  const { data } = await getFinePage(query);
  tableData.value = data.records;
  pagination.total = data.total;
}

function openPayDialog(id) {
  form.fineId = id;
  form.payType = "";
  dialogVisible.value = true;
}

async function submitPay() {
  await formRef.value.validate();
  await payFine(form);
  ElMessage.success("缴费成功");
  dialogVisible.value = false;
  loadData();
}

function handlePageChange(page) {
  query.current = page;
  loadData();
}

function handleExport() {
  downloadFile("/api/fines/export", "fines.xlsx");
}

onMounted(loadData);
</script>

<style scoped>
.page-panel {
  padding: 22px;
}

.sub-text {
  margin-top: 4px;
  font-size: 12px;
  color: #6b7280;
}
</style>
