import request from "@/utils/request";

export function getPurchasePage(params) {
  return request({
    url: "/purchases",
    method: "get",
    params
  });
}

export function savePurchase(data) {
  return request({
    url: "/purchases",
    method: "post",
    data
  });
}

export function deletePurchase(id) {
  return request({
    url: `/purchases/${id}`,
    method: "delete"
  });
}

export function receivePurchase(data) {
  return request({
    url: "/purchases/receive",
    method: "post",
    data
  });
}
