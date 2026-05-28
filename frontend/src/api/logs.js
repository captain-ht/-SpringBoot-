import request from "@/utils/request";

export function getLogPage(params) {
  return request({
    url: "/logs",
    method: "get",
    params
  });
}
