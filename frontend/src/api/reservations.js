import request from "@/utils/request";

export function getReservationPage(params) {
  return request({
    url: "/reservations",
    method: "get",
    params
  });
}

export function createReservation(data) {
  return request({
    url: "/reservations",
    method: "post",
    data
  });
}

export function cancelReservation(data) {
  return request({
    url: "/reservations/cancel",
    method: "post",
    data
  });
}

export function getBookReservationCount(bookId) {
  return request({
    url: `/reservations/book/${bookId}/count`,
    method: "get"
  });
}
