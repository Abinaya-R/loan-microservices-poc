import { get, post } from "./http.js";

const USER_BASE_URL = import.meta.env.VITE_USER_SERVICE_URL || "http://localhost:8091";

export async function registerUser(payload) {
  return post(`${USER_BASE_URL}/api/auth/register`, payload);
}

export async function loginUser(payload) {
  const res = await post(`${USER_BASE_URL}/api/auth/login`, payload);
  return res?.token || "";
}

export async function getUserById(userId) {
  return get(`${USER_BASE_URL}/api/users/${userId}`);
}
