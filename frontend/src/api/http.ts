import axios from 'axios';
import { clearToken, getToken } from '../auth/tokenStorage';

export const http = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

http.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers = config.headers ?? {};
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

http.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err?.response?.status === 401) {
      clearToken();
    }
    return Promise.reject(err);
  }
);

export function getErrorMessage(error: unknown): string {
  if (!error || typeof error !== 'object') return 'Unknown error';

  // axios style
  const anyErr = error as any;

  // Friendly message for bad credentials on login
  const status = anyErr?.response?.status;
  const url = String(anyErr?.config?.url ?? anyErr?.request?.responseURL ?? '');
  if ((status === 401 || status === 403) && url.includes('/user/login')) {
    return 'Błędna nazwa użytkownika lub hasło.';
  }

  const data = anyErr?.response?.data;
  if (typeof data === 'string' && data.trim()) return data;
  if (data && typeof data === 'object') {
    if (typeof data.message === 'string' && data.message.trim()) return data.message;
    if (typeof data.error === 'string' && data.error.trim()) return data.error;
  }
  if (typeof anyErr.message === 'string' && anyErr.message.trim()) return anyErr.message;
  return 'Request failed';
}
