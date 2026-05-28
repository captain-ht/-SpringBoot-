import axios from "axios";
import { ElMessage } from "element-plus";
import router from "@/router";
import { clearToken, clearUser, getToken } from "@/utils/storage";

const service = axios.create({
  baseURL: "/api",
  timeout: 10000
});

service.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

service.interceptors.response.use(
  (response) => {
    const { data } = response;
    if (data.code !== 200) {
      ElMessage.error(data.message || "请求失败");
      if (data.code === 401) {
        clearToken();
        clearUser();
        router.replace("/login");
      }
      return Promise.reject(data);
    }
    return data;
  },
  (error) => {
    ElMessage.error(error.response?.data?.message || error.message || "服务异常");
    if (error.response?.status === 401) {
      clearToken();
      clearUser();
      router.replace("/login");
    }
    return Promise.reject(error);
  }
);

export default service;
