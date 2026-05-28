<template>
  <div class="page-card page-panel">
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input-number v-model="query.readerId" :min="1" placeholder="读者ID" />
        <el-input-number v-model="query.bookId" :min="1" placeholder="图书ID" />
        <el-select v-model="query.status" clearable placeholder="状态" style="width: 140px">
          <el-option label="借阅中" :value="0" />
          <el-option label="已归还" :value="1" />
        </el-select>
        <el-switch v-model="query.overdueOnly" active-text="仅看逾期" />
        <el-button type="primary" @click="loadData">查询</el-button>
      </div>
      <div class="toolbar-right">
        <el-button @click="handleExport">导出借阅</el-button>
        <el-button type="primary" @click="borrowDialogVisible = true">办理借书</el-button>
        <el-button @click="reservationDialogVisible = true">办理预约</el-button>
        <el-button @click="returnDialogVisible = true">办理还书</el-button>
      </div>
    </div>

    <el-table :data="tableData" border>
      <el-table-column prop="id" label="记录ID" width="90" />
      <el-table-column label="读者信息" min-width="170">
        <template #default="{ row }">
          <div>{{ row.readerName || "-" }}</div>
          <div class="sub-text">{{ row.readerNo || `ID: ${row.readerId}` }}</div>
        </template>
      </el-table-column>
      <el-table-column label="图书信息" min-width="220">
        <template #default="{ row }">
          <div>{{ row.bookTitle || "-" }}</div>
          <div class="sub-text">{{ row.bookCode || `ID: ${row.bookId}` }} / {{ row.author || "-" }}</div>
        </template>
      </el-table-column>
      <el-table-column prop="borrowDate" label="借阅日期" min-width="120" />
      <el-table-column prop="dueDate" label="应还日期" min-width="120" />
      <el-table-column prop="returnDate" label="归还日期" min-width="120" />
      <el-table-column prop="renewCount" label="续借次数" width="100" />
      <el-table-column prop="overdueDays" label="逾期天数" width="100" />
      <el-table-column label="逾期状态" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.overdue" type="danger">逾期中</el-tag>
          <el-tag v-else type="success">正常</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 0 ? 'warning' : 'success'">{{ row.status === 0 ? "借阅中" : "已归还" }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="记录操作" width="120">
        <template #default="{ row }">
          <el-button v-if="row.status === 0" link type="primary" @click="openRenewDialog(row.id)">续借</el-button>
          <span v-else>-</span>
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

      <div class="reservation-section">
      <div class="toolbar">
        <div class="section-title">预约记录</div>
        <el-button @click="loadReservations">刷新预约列表</el-button>
      </div>
      <el-table :data="reservationTableData" border>
        <el-table-column prop="id" label="预约ID" width="90" />
        <el-table-column label="读者信息" min-width="170">
          <template #default="{ row }">
            <div>{{ row.readerName || "-" }}</div>
            <div class="sub-text">{{ row.readerNo || `ID: ${row.readerId}` }}</div>
          </template>
        </el-table-column>
        <el-table-column label="图书信息" min-width="220">
          <template #default="{ row }">
            <div>{{ row.bookTitle || "-" }}</div>
            <div class="sub-text">{{ row.bookCode || `ID: ${row.bookId}` }} / {{ row.author || "-" }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="reservationDate" label="预约日期" min-width="120" />
        <el-table-column prop="expireDate" label="失效日期" min-width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="reservationStatusTypeMap[row.status]">
              {{ reservationStatusMap[row.status] || "未知" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button v-if="row.status === 0" link type="danger" @click="submitCancelReservation(row.id)">取消</el-button>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="borrowDialogVisible" title="办理借书" width="420px">
      <el-form ref="borrowFormRef" :model="borrowForm" :rules="borrowRules" label-width="90px">
        <el-form-item label="读者ID" prop="readerId"><el-input-number v-model="borrowForm.readerId" :min="1" style="width: 100%" /></el-form-item>
        <el-form-item label="图书ID" prop="bookId">
          <el-input-number v-model="borrowForm.bookId" :min="1" style="width: 100%" @change="checkReservationHint" />
        </el-form-item>
        <el-alert
          v-if="borrowReservationHint"
          :title="borrowReservationHint"
          type="warning"
          :closable="false"
          show-icon
        />
      </el-form>
      <template #footer>
        <el-button @click="borrowDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitBorrow">确认借阅</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="returnDialogVisible" title="办理还书" width="420px">
      <el-form ref="returnFormRef" :model="returnForm" :rules="returnRules" label-width="110px">
        <el-form-item label="借阅记录ID" prop="borrowRecordId">
          <el-input-number v-model="returnForm.borrowRecordId" :min="1" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="returnDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReturn">确认归还</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="renewDialogVisible" title="办理续借" width="420px">
      <el-form ref="renewFormRef" :model="renewForm" :rules="renewRules" label-width="110px">
        <el-form-item label="借阅记录ID" prop="borrowRecordId">
          <el-input-number v-model="renewForm.borrowRecordId" :min="1" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="renewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRenew">确认续借</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="reservationDialogVisible" title="办理预约" width="420px">
      <el-form ref="reservationFormRef" :model="reservationForm" :rules="reservationRules" label-width="90px">
        <el-form-item label="读者ID" prop="readerId"><el-input-number v-model="reservationForm.readerId" :min="1" style="width: 100%" /></el-form-item>
        <el-form-item label="图书ID" prop="bookId"><el-input-number v-model="reservationForm.bookId" :min="1" style="width: 100%" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="reservationDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReservation">确认预约</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { borrowBook, getBorrowPage, returnBook } from "@/api/borrows";
import { cancelReservation, createReservation, getBookReservationCount, getReservationPage } from "@/api/reservations";
import { downloadFile } from "@/utils/download";
import request from "@/utils/request";

const query = reactive({ current: 1, size: 10, readerId: null, bookId: null, status: null, overdueOnly: false });
const pagination = reactive({ total: 0 });
const tableData = ref([]);
const reservationTableData = ref([]);
const borrowReservationHint = ref("");

const borrowDialogVisible = ref(false);
const returnDialogVisible = ref(false);
const renewDialogVisible = ref(false);
const reservationDialogVisible = ref(false);
const borrowFormRef = ref();
const returnFormRef = ref();
const renewFormRef = ref();
const reservationFormRef = ref();

const borrowForm = reactive({ readerId: null, bookId: null });
const returnForm = reactive({ borrowRecordId: null });
const renewForm = reactive({ borrowRecordId: null });
const reservationForm = reactive({ readerId: null, bookId: null });
const reservationQuery = reactive({ current: 1, size: 8, status: 0 });

const reservationStatusMap = {
  0: "预约中",
  1: "已完成",
  2: "已取消",
  3: "已失效"
};

const reservationStatusTypeMap = {
  0: "warning",
  1: "success",
  2: "info",
  3: "danger"
};

const borrowRules = {
  readerId: [{ required: true, message: "请输入读者 ID", trigger: "change" }],
  bookId: [{ required: true, message: "请输入图书 ID", trigger: "change" }]
};

const returnRules = {
  borrowRecordId: [{ required: true, message: "请输入借阅记录 ID", trigger: "change" }]
};

const renewRules = {
  borrowRecordId: [{ required: true, message: "请输入借阅记录 ID", trigger: "change" }]
};

const reservationRules = {
  readerId: [{ required: true, message: "请输入读者 ID", trigger: "change" }],
  bookId: [{ required: true, message: "请输入图书 ID", trigger: "change" }]
};

async function loadData() {
  const params = { ...query, overdueOnly: query.overdueOnly || undefined };
  const { data } = await getBorrowPage(params);
  tableData.value = data.records;
  pagination.total = data.total;
}

async function loadReservations() {
  const { data } = await getReservationPage(reservationQuery);
  reservationTableData.value = data.records;
}

async function submitBorrow() {
  await borrowFormRef.value.validate();
  await borrowBook(borrowForm);
  ElMessage.success("借阅成功");
  borrowDialogVisible.value = false;
  borrowForm.readerId = null;
  borrowForm.bookId = null;
  borrowReservationHint.value = "";
  loadData();
  loadReservations();
}

async function submitReturn() {
  await returnFormRef.value.validate();
  await returnBook(returnForm);
  ElMessage.success("归还成功");
  returnDialogVisible.value = false;
  returnForm.borrowRecordId = null;
  loadData();
}

function openRenewDialog(id) {
  renewForm.borrowRecordId = id;
  renewDialogVisible.value = true;
}

async function submitRenew() {
  await renewFormRef.value.validate();
  await request({
    url: "/borrows/renew",
    method: "post",
    data: renewForm
  });
  ElMessage.success("续借成功");
  renewDialogVisible.value = false;
  renewForm.borrowRecordId = null;
  loadData();
}

async function submitReservation() {
  await reservationFormRef.value.validate();
  await createReservation(reservationForm);
  ElMessage.success("预约成功");
  reservationDialogVisible.value = false;
  reservationForm.readerId = null;
  reservationForm.bookId = null;
  loadReservations();
}

async function submitCancelReservation(id) {
  await ElMessageBox.confirm("确认取消该预约记录？", "提示", { type: "warning" });
  await cancelReservation({ reservationId: id });
  ElMessage.success("取消成功");
  loadReservations();
}

async function checkReservationHint() {
  borrowReservationHint.value = "";
  if (!borrowForm.bookId) {
    return;
  }
  const { data } = await getBookReservationCount(borrowForm.bookId);
  if (data > 0) {
    borrowReservationHint.value = `该图书当前有 ${data} 条有效预约记录，直接借出可能被系统拦截。`;
  }
}

function handlePageChange(page) {
  query.current = page;
  loadData();
}

function handleExport() {
  downloadFile("/api/borrows/export", "borrows.xlsx");
}

onMounted(() => {
  loadData();
  loadReservations();
});
</script>

<style scoped>
.page-panel {
  padding: 22px;
}

.reservation-section {
  margin-top: 28px;
}

.sub-text {
  margin-top: 4px;
  font-size: 12px;
  color: #6b7280;
}
</style>
