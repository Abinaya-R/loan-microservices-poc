import { get, post } from "./http.js";

const ACCOUNT_BASE_URL = import.meta.env.VITE_ACCOUNT_SERVICE_URL || "http://localhost:8092";

export async function createAccount(payload, token) {
  return post(`${ACCOUNT_BASE_URL}/accounts/create`, payload, token);
}

export async function getAccountById(accountId, token) {
  return get(`${ACCOUNT_BASE_URL}/accounts/${accountId}`, token);
}

export async function getAccountsByUserId(userId, token) {
  return get(`${ACCOUNT_BASE_URL}/accounts/user/${userId}`, token);
}

export async function debitAccount(payload, token) {
  return post(`${ACCOUNT_BASE_URL}/accounts/debit`, payload, token);
}

export async function creditAccount(payload, token) {
  return post(`${ACCOUNT_BASE_URL}/accounts/credit`, payload, token);
}
