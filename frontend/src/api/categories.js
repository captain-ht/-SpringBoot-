import request from "@/utils/request";

export function getCategories() {
  return request({
    url: "/categories",
    method: "get"
  });
}

export function saveCategory(data) {
  return request({
    url: "/categories",
    method: "post",
    data
  });
}

export function deleteCategory(id) {
  return request({
    url: `/categories/${id}`,
    method: "delete"
  });
}
