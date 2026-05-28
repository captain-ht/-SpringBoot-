import { defineStore } from "pinia";
import { login, logout } from "@/api/auth";
import { clearToken, clearUser, getToken, getUser, setToken, setUser } from "@/utils/storage";

export const useUserStore = defineStore("user", {
  state: () => ({
    token: getToken(),
    userInfo: getUser()
  }),
  getters: {
    isLoggedIn: (state) => Boolean(state.token)
  },
  actions: {
    async loginAction(payload) {
      const { data } = await login(payload);
      this.token = data.token;
      this.userInfo = data;
      setToken(data.token);
      setUser(data);
    },
    async logoutAction() {
      try {
        if (this.token) {
          await logout();
        }
      } finally {
        this.reset();
      }
    },
    reset() {
      this.token = "";
      this.userInfo = null;
      clearToken();
      clearUser();
    }
  }
});
