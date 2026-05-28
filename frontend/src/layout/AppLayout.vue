<template>
  <div class="layout-shell">
    <aside class="layout-sidebar">
      <div class="brand-block">
        <div class="brand-mark">L</div>
        <div>
          <div class="brand-title">图书管理系统</div>
          <div class="brand-subtitle">Library Operations Desk</div>
        </div>
      </div>
      <el-menu
        class="side-menu"
        :default-active="route.path"
        router
        background-color="transparent"
        text-color="#dbe7eb"
        active-text-color="#ffffff"
      >
        <el-menu-item
          v-for="item in menuItems"
          :key="item.path"
          :index="item.path"
        >
          <el-icon><component :is="item.meta.icon" /></el-icon>
          <span>{{ item.meta.title }}</span>
        </el-menu-item>
      </el-menu>
    </aside>

    <div class="layout-main">
      <header class="layout-header page-card">
        <div>
          <div class="header-title">{{ currentTitle }}</div>
          <div class="header-subtitle">{{ currentDateText }}</div>
        </div>
        <div class="header-actions">
          <div class="header-user">
            <div class="user-name">{{ userStore.userInfo?.realName }}</div>
            <div class="user-role">{{ userStore.userInfo?.role }}</div>
          </div>
          <el-button round @click="handleLogout">退出登录</el-button>
        </div>
      </header>

      <main class="layout-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import dayjs from "dayjs";
import { ElMessageBox } from "element-plus";
import { useUserStore } from "@/stores/user";

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const menuItems = computed(() => {
  const root = router.options.routes.find((item) => item.path === "/");
  const role = userStore.userInfo?.role;
  return root?.children
    .filter((item) => !item.meta?.roles || item.meta.roles.includes(role))
    .map((item) => ({ ...item, path: `/${item.path}` })) || [];
});

const currentTitle = computed(() => route.meta.title || "工作台");
const currentDateText = computed(() => dayjs().format("YYYY年MM月DD日 dddd"));

async function handleLogout() {
  await ElMessageBox.confirm("确认退出当前账号？", "提示", { type: "warning" });
  await userStore.logoutAction();
  router.replace("/login");
}
</script>

<style scoped>
.layout-shell {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 280px 1fr;
}

.layout-sidebar {
  padding: 26px 20px;
  background:
    radial-gradient(circle at top left, rgba(250, 212, 113, 0.18), transparent 34%),
    linear-gradient(180deg, #183746 0%, #214f60 55%, #1d4250 100%);
  color: #fff;
}

.brand-block {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 30px;
  padding: 14px;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.08);
}

.brand-mark {
  width: 48px;
  height: 48px;
  border-radius: 16px;
  display: grid;
  place-items: center;
  font-size: 24px;
  font-weight: 700;
  color: #183746;
  background: linear-gradient(135deg, #f5d584 0%, #f8efe1 100%);
}

.brand-title {
  font-size: 20px;
  font-weight: 700;
}

.brand-subtitle {
  margin-top: 4px;
  font-size: 12px;
  color: rgba(219, 231, 235, 0.72);
  letter-spacing: 0.08em;
}

.side-menu {
  border-right: none;
}

:deep(.el-menu-item) {
  margin-bottom: 8px;
  height: 48px;
  border-radius: 14px;
}

:deep(.el-menu-item.is-active) {
  background: rgba(255, 255, 255, 0.14);
}

.layout-main {
  padding: 22px;
}

.layout-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  margin-bottom: 20px;
}

.header-title {
  font-size: 28px;
  font-weight: 700;
}

.header-subtitle {
  margin-top: 6px;
  color: var(--text-secondary);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-user {
  text-align: right;
}

.user-name {
  font-weight: 700;
}

.user-role {
  font-size: 13px;
  color: var(--text-secondary);
}

.layout-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

@media (max-width: 980px) {
  .layout-shell {
    grid-template-columns: 1fr;
  }

  .layout-sidebar {
    padding-bottom: 12px;
  }

  .layout-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
}
</style>
