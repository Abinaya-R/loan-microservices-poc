import { get, post } from "./http.js";

const PAYMENT_BASE_URL = import.meta.env.VITE_PAYMENT_SERVICE_URL || "http://localhost:8093";

export async function payLoan(payload, token) {
  return post(`${PAYMENT_BASE_URL}/payments/loan`, payload, token);
}

export async function getPaymentById(paymentId, token) {
  return get(`${PAYMENT_BASE_URL}/payments/${paymentId}`, token);
}
