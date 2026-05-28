<template>
  <div class="page-card page-panel">
    <div class="toolbar">
      <div class="toolbar-left">
        <div class="section-title">操作日志</div>
        <el-input v-model="query.module" clearable placeholder="模块" style="width: 140px" />
        <el-input v-model="query.action" clearable placeholder="动作" style="width: 140px" />
        <el-input v-model="query.username" clearable placeholder="操作人" style="width: 140px" />
        <el-select v-model="query.status" clearable placeholder="状态" style="width: 120px">
          <el-option label="成功" value="SUCCESS" />
          <el-option label="失败" value="FAILED" />
        </el-select>
        <el-date-picker
          v-model="query.timeRange"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button @click="handleExport">导出日志</el-button>
      </div>
    </div>

    <el-table :data="tableData" border :row-class-name="getRowClassName">
      <el-table-column prop="module" label="模块" min-width="120" />
      <el-table-column prop="action" label="动作" min-width="120" />
      <el-table-column prop="username" label="操作人" min-width="120" />
      <el-table-column prop="method" label="请求方式" width="100" />
      <el-table-column prop="requestUri" label="请求路径" min-width="180" />
      <el-table-column prop="ipAddress" label="IP" min-width="130" />
      <el-table-column label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'SUCCESS' ? 'success' : 'danger'">
            {{ row.status === "SUCCESS" ? "成功" : "失败" }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="时间" min-width="170" />
      <el-table-column label="详情" min-width="280">
        <template #default="{ row }">
          <div class="log-detail">{{ row.requestData || "-" }}</div>
          <div v-if="row.errorMessage" class="log-error">{{ row.errorMessage }}</div>
          <el-button link type="primary" @click="openDetail(row)">查看详情</el-button>
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

    <el-dialog v-model="detailVisible" title="日志详情" width="760px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="模块">{{ currentRow?.module || "-" }}</el-descriptions-item>
        <el-descriptions-item label="动作">{{ currentRow?.action || "-" }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ currentRow?.username || "-" }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ currentRow?.status || "-" }}</el-descriptions-item>
        <el-descriptions-item label="请求路径">{{ currentRow?.requestUri || "-" }}</el-descriptions-item>
        <el-descriptions-item label="请求方式">{{ currentRow?.method || "-" }}</el-descriptions-item>
        <el-descriptions-item label="IP">{{ currentRow?.ipAddress || "-" }}</el-descriptions-item>
        <el-descriptions-item label="请求参数">
          <pre class="detail-pre">{{ currentRow?.requestData || "-" }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="错误信息">
          <pre class="detail-pre error-pre">{{ currentRow?.errorMessage || "-" }}</pre>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { getLogPage } from "@/api/logs";
import { downloadFile } from "@/utils/download";

const query = reactive({
  current: 1,
  size: 10,
  module: "",
  action: "",
  username: "",
  status: "",
  timeRange: []
});
const pagination = reactive({ total: 0 });
const tableData = ref([]);
const detailVisible = ref(false);
const currentRow = ref(null);

async function loadData() {
  const params = {
    current: query.current,
    size: query.size,
    module: query.module || undefined,
    action: query.action || undefined,
    username: query.username || undefined,
    status: query.status || undefined,
    startTime: query.timeRange?.[0] || undefined,
    endTime: query.timeRange?.[1] || undefined
  };
  const { data } = await getLogPage(params);
  tableData.value = data.records;
  pagination.total = data.total;
}

function handlePageChange(page) {
  query.current = page;
  loadData();
}

function getRowClassName({ row }) {
  return row.status === "FAILED" ? "failed-row" : "";
}

function openDetail(row) {
  currentRow.value = row;
  detailVisible.value = true;
}

function handleExport() {
  downloadFile("/api/logs/export", "logs.xlsx");
}

onMounted(loadData);
</script>

<style scoped>
.page-panel {
  padding: 22px;
}

.log-detail {
  word-break: break-all;
  color: #475569;
}

.log-error {
  margin-top: 6px;
  color: #b91c1c;
  word-break: break-all;
}

.detail-pre {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-all;
  font-family: Menlo, Monaco, Consolas, monospace;
}

.error-pre {
  color: #b91c1c;
}

:deep(.failed-row) {
  --el-table-tr-bg-color: #fff1f2;
}
</style>
