<template>
  <div class="login-page">
    <div class="login-panel page-card">
      <div class="login-copy">
        <div class="copy-tag">数字图书馆</div>
        <h1>让图书流转、借阅与统计回到一个系统里。</h1>
        <p>
          覆盖图书、读者、借还、罚款与采购的日常管理，适合中小型图书馆快速落地。
        </p>
        <div class="copy-points">
          <span>JWT 鉴权</span>
          <span>Redis 会话</span>
          <span>借阅闭环</span>
        </div>
      </div>

      <div class="login-form-wrap">
        <div class="form-title">后台登录</div>
        <div class="form-desc">使用管理员或馆员账号进入管理后台</div>
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" size="large" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              size="large"
              show-password
              placeholder="请输入密码"
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          <el-button
            class="submit-btn"
            type="primary"
            size="large"
            :loading="loading"
            @click="handleLogin"
          >
            进入系统
          </el-button>
        </el-form>
        <div class="demo-account">
          默认账号：
          <strong>admin / 123456</strong>
          或
          <strong>librarian / 123456</strong>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/stores/user";

const router = useRouter();
const userStore = useUserStore();
const formRef = ref();
const loading = ref(false);

const form = reactive({
  username: "admin",
  password: "123456"
});

const rules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }]
};

async function handleLogin() {
  await formRef.value.validate();
  loading.value = true;
  try {
    await userStore.loginAction(form);
    ElMessage.success("登录成功");
    router.replace("/");
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: grid;
  place-items: center;
  padding: 24px;
  background:
    radial-gradient(circle at 15% 15%, rgba(245, 213, 132, 0.55), transparent 28%),
    radial-gradient(circle at 85% 18%, rgba(31, 78, 95, 0.18), transparent 30%),
    linear-gradient(135deg, #f6efe3 0%, #eaf1f3 55%, #edf4f1 100%);
}

.login-panel {
  width: min(1100px, 100%);
  display: grid;
  grid-template-columns: 1.1fr 0.9fr;
  overflow: hidden;
}

.login-copy {
  padding: 54px;
  background:
    linear-gradient(160deg, rgba(24, 55, 70, 0.95), rgba(33, 79, 96, 0.9)),
    linear-gradient(180deg, #183746, #214f60);
  color: #f7fbfc;
}

.copy-tag {
  display: inline-flex;
  padding: 8px 14px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.12);
  font-size: 13px;
  letter-spacing: 0.08em;
}

.login-copy h1 {
  margin: 24px 0 14px;
  font-size: 44px;
  line-height: 1.15;
}

.login-copy p {
  margin: 0;
  max-width: 460px;
  color: rgba(247, 251, 252, 0.78);
  line-height: 1.8;
}

.copy-points {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
  margin-top: 28px;
}

.copy-points span {
  padding: 10px 14px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.1);
}

.login-form-wrap {
  padding: 54px 46px;
}

.form-title {
  font-size: 30px;
  font-weight: 700;
}

.form-desc {
  margin: 10px 0 28px;
  color: var(--text-secondary);
}

.submit-btn {
  width: 100%;
  margin-top: 8px;
  height: 48px;
  border-radius: 14px;
}

.demo-account {
  margin-top: 18px;
  color: var(--text-secondary);
  line-height: 1.8;
}

@media (max-width: 980px) {
  .login-panel {
    grid-template-columns: 1fr;
  }

  .login-copy,
  .login-form-wrap {
    padding: 32px 24px;
  }

  .login-copy h1 {
    font-size: 34px;
  }
}
</style>
