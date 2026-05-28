<template>
  <div class="dashboard-wrap">
    <div class="hero-card page-card">
      <div>
        <div class="hero-eyebrow">馆藏数字工作台</div>
        <div class="hero-title">把借阅波动、逾期压力和馆藏分布放在一个视图里。</div>
      </div>
      <div class="hero-note">
        当前账号：{{ userStore.userInfo?.realName }} / {{ userStore.userInfo?.role }}
      </div>
    </div>

    <div class="stat-grid">
      <div v-for="item in stats" :key="item.label" class="stat-card page-card">
        <div class="stat-label">{{ item.label }}</div>
        <div class="stat-value">{{ item.value }}</div>
        <div class="stat-desc">{{ item.desc }}</div>
      </div>
    </div>

    <div class="chart-grid">
      <div class="page-card chart-card">
        <div class="section-title">分类馆藏分布</div>
        <div ref="categoryChartRef" class="chart-box"></div>
      </div>
      <div class="page-card chart-card">
        <div class="section-title">近 6 月借阅趋势</div>
        <div ref="borrowChartRef" class="chart-box"></div>
      </div>
    </div>

    <div class="page-card chart-card">
      <div class="section-title">热门图书排行</div>
      <div class="hot-list">
        <div v-for="(item, index) in dataSource.hotBookStats" :key="item.name" class="hot-item">
          <div class="hot-rank">{{ index + 1 }}</div>
          <div class="hot-name">{{ item.name }}</div>
          <div class="hot-value">{{ item.value }} 次</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from "vue";
import * as echarts from "echarts";
import { getDashboard } from "@/api/dashboard";
import { useUserStore } from "@/stores/user";

const userStore = useUserStore();
const dataSource = ref({
  totalBooks: 0,
  availableBooks: 0,
  totalReaders: 0,
  borrowingCount: 0,
  overdueCount: 0,
  unpaidFineAmount: 0,
  categoryStats: [],
  monthlyBorrowStats: [],
  hotBookStats: []
});

const categoryChartRef = ref();
const borrowChartRef = ref();
let categoryChart;
let borrowChart;

const stats = computed(() => [
  { label: "馆藏总量", value: dataSource.value.totalBooks, desc: "当前系统登记图书总数" },
  { label: "可借库存", value: dataSource.value.availableBooks, desc: "当前仍可外借的图书数量" },
  { label: "读者数量", value: dataSource.value.totalReaders, desc: "已登记并可管理的读者数" },
  { label: "借阅中", value: dataSource.value.borrowingCount, desc: "仍未归还的借阅记录" },
  { label: "逾期记录", value: dataSource.value.overdueCount, desc: "已超过应还日期的记录" },
  { label: "未缴罚款", value: `￥${dataSource.value.unpaidFineAmount || 0}`, desc: "尚未完成缴纳的罚款金额" }
]);

async function loadData() {
  const { data } = await getDashboard();
  dataSource.value = data;
  await nextTick();
  renderCharts();
}

function renderCharts() {
  if (!categoryChart) {
    categoryChart = echarts.init(categoryChartRef.value);
  }
  if (!borrowChart) {
    borrowChart = echarts.init(borrowChartRef.value);
  }

  categoryChart.setOption({
    tooltip: { trigger: "item" },
    color: ["#1f4e5f", "#d4a24c", "#7aa6a1", "#8d5d4b", "#c98474"],
    series: [
      {
        type: "pie",
        radius: ["42%", "72%"],
        itemStyle: { borderRadius: 10, borderColor: "#fff", borderWidth: 4 },
        data: dataSource.value.categoryStats.map((item) => ({
          name: item.name,
          value: item.value
        }))
      }
    ]
  });

  borrowChart.setOption({
    tooltip: { trigger: "axis" },
    color: ["#1f4e5f"],
    xAxis: {
      type: "category",
      data: dataSource.value.monthlyBorrowStats.map((item) => item.name)
    },
    yAxis: { type: "value" },
    series: [
      {
        type: "bar",
        data: dataSource.value.monthlyBorrowStats.map((item) => item.value),
        barWidth: 28,
        itemStyle: {
          borderRadius: [12, 12, 0, 0]
        }
      }
    ]
  });
}

function handleResize() {
  categoryChart?.resize();
  borrowChart?.resize();
}

onMounted(() => {
  loadData();
  window.addEventListener("resize", handleResize);
});

onBeforeUnmount(() => {
  window.removeEventListener("resize", handleResize);
  categoryChart?.dispose();
  borrowChart?.dispose();
});
</script>

<style scoped>
.dashboard-wrap {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.hero-card {
  display: flex;
  align-items: end;
  justify-content: space-between;
  gap: 20px;
  padding: 28px;
}

.hero-eyebrow {
  color: #b07a1e;
  font-weight: 700;
  letter-spacing: 0.1em;
}

.hero-title {
  max-width: 760px;
  margin-top: 10px;
  font-size: 30px;
  font-weight: 700;
  line-height: 1.3;
}

.hero-note {
  color: var(--text-secondary);
}

.stat-grid {
  grid-template-columns: repeat(3, minmax(0, 1fr));
}

.stat-card {
  padding: 22px;
}

.stat-label {
  color: var(--text-secondary);
}

.stat-value {
  margin: 14px 0 10px;
  font-size: 34px;
  font-weight: 700;
}

.stat-desc {
  color: var(--text-secondary);
}

.chart-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
}

.chart-card {
  padding: 22px;
}

.chart-box {
  height: 340px;
  margin-top: 16px;
}

.hot-list {
  margin-top: 18px;
  display: grid;
  gap: 12px;
}

.hot-item {
  display: grid;
  grid-template-columns: 52px 1fr auto;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  border-radius: 16px;
  background: rgba(31, 78, 95, 0.06);
}

.hot-rank {
  width: 36px;
  height: 36px;
  display: grid;
  place-items: center;
  border-radius: 12px;
  background: #1f4e5f;
  color: #fff;
  font-weight: 700;
}

.hot-name {
  font-weight: 600;
}

.hot-value {
  color: #b07a1e;
  font-weight: 700;
}

@media (max-width: 1080px) {
  .chart-grid,
  .stat-grid {
    grid-template-columns: 1fr;
  }

  .hero-card {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
