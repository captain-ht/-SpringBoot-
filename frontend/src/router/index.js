import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "@/stores/user";
import Layout from "@/layout/AppLayout.vue";

const routes = [
  {
    path: "/login",
    component: () => import("@/views/login/LoginView.vue"),
    meta: { public: true }
  },
  {
    path: "/",
    component: Layout,
    redirect: "/dashboard",
    children: [
      {
        path: "dashboard",
        name: "dashboard",
        meta: { title: "工作台", icon: "DataBoard" },
        component: () => import("@/views/dashboard/DashboardView.vue")
      },
      {
        path: "books",
        name: "books",
        meta: { title: "图书管理", icon: "Reading" },
        component: () => import("@/views/books/BooksView.vue")
      },
      {
        path: "categories",
        name: "categories",
        meta: { title: "分类管理", icon: "CollectionTag" },
        component: () => import("@/views/categories/CategoriesView.vue")
      },
      {
        path: "readers",
        name: "readers",
        meta: { title: "读者管理", icon: "User" },
        component: () => import("@/views/readers/ReadersView.vue")
      },
      {
        path: "borrows",
        name: "borrows",
        meta: { title: "借阅管理", icon: "Tickets" },
        component: () => import("@/views/borrows/BorrowsView.vue")
      },
      {
        path: "fines",
        name: "fines",
        meta: { title: "罚款管理", icon: "Wallet" },
        component: () => import("@/views/fines/FinesView.vue")
      },
      {
        path: "purchases",
        name: "purchases",
        meta: { title: "采购管理", icon: "ShoppingCart" },
        component: () => import("@/views/purchases/PurchasesView.vue")
      },
      {
        path: "logs",
        name: "logs",
        meta: { title: "操作日志", icon: "Document", roles: ["ADMIN"] },
        component: () => import("@/views/logs/LogsView.vue")
      }
    ]
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

router.beforeEach((to) => {
  const userStore = useUserStore();
  if (to.meta.public) {
    return userStore.isLoggedIn && to.path === "/login" ? "/" : true;
  }
  if (!userStore.isLoggedIn) {
    return "/login";
  }
  return true;
});

export default router;
