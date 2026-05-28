import request from "@/utils/request";

export function getBookPage(params) {
  return request({
    url: "/books",
    method: "get",
    params
  });
}

export function saveBook(data) {
  return request({
    url: "/books",
    method: "post",
    data
  });
}

export function deleteBook(id) {
  return request({
    url: `/books/${id}`,
    method: "delete"
  });
}
