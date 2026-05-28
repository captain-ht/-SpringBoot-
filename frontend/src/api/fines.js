import request from "@/utils/request";

export function getFinePage(params) {
  return request({
    url: "/fines",
    method: "get",
    params
  });
}

export function payFine(data) {
  return request({
    url: "/fines/pay",
    method: "post",
    data
  });
}
