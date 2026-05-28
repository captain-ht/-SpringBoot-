import request from "@/utils/request";

export function getReaderPage(params) {
  return request({
    url: "/readers",
    method: "get",
    params
  });
}

export function saveReader(data) {
  return request({
    url: "/readers",
    method: "post",
    data
  });
}

export function deleteReader(id) {
  return request({
    url: `/readers/${id}`,
    method: "delete"
  });
}

export function importReaders(data) {
  return request({
    url: "/readers/import",
    method: "post",
    data,
    headers: {
      "Content-Type": "multipart/form-data"
    }
  });
}
