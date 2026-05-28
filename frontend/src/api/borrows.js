import request from "@/utils/request";

export function getBorrowPage(params) {
  return request({
    url: "/borrows",
    method: "get",
    params
  });
}

export function borrowBook(data) {
  return request({
    url: "/borrows/borrow",
    method: "post",
    data
  });
}

export function returnBook(data) {
  return request({
    url: "/borrows/return",
    method: "post",
    data
  });
}
