export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
export const AUTH_STORAGE_KEY = 'techdev.auth';

export class ApiError extends Error {
  constructor(message, details = {}) {
    super(message);
    this.name = 'ApiError';
    this.status = details.status ?? null;
    this.data = details.data ?? null;
    this.url = details.url || '';
    this.method = details.method || 'GET';
    this.hasAuthorizationHeader = Boolean(details.hasAuthorizationHeader);
  }
}

export function normalizeAuthToken(value) {
  if (typeof value !== 'string') return null;
  let token = value.trim();
  if (token.startsWith('"') && token.endsWith('"')) {
    try {
      token = JSON.parse(token).trim();
    } catch {
      return null;
    }
  }
  token = token.replace(/^Bearer\s+/i, '').trim();
  return token || null;
}

export function getStoredAuth() {
  try {
    const value = localStorage.getItem(AUTH_STORAGE_KEY);
    if (!value) return null;
    const parsed = JSON.parse(value);
    if (!parsed || typeof parsed !== 'object') return null;
    return { ...parsed, token: normalizeAuthToken(parsed.token) };
  } catch {
    return null;
  }
}

async function readResponse(response) {
  if (response.status === 204) return null;
  const contentType = response.headers.get('content-type') || '';
  if (contentType.includes('application/json')) return response.json();
  const text = await response.text();
  return text || null;
}

export async function apiRequest(path, options = {}) {
  const auth = getStoredAuth();
  const headers = { ...options.headers };
  const requestUrl = `${API_BASE_URL}${path}`;
  const method = options.method || 'GET';

  if (options.body && !headers['Content-Type']) headers['Content-Type'] = 'application/json';
  if (auth?.token) headers.Authorization = `Bearer ${auth.token}`;

  let response;
  try {
    response = await fetch(requestUrl, { ...options, headers });
  } catch {
    throw new ApiError(
      'Sunucuya bağlanılamadı. Backend servisinin çalıştığını ve CORS ayarlarını kontrol edin.',
      { url: requestUrl, method, hasAuthorizationHeader: Boolean(headers.Authorization) },
    );
  }

  let payload;
  try {
    payload = await readResponse(response);
  } catch {
    payload = null;
  }

  if (!response.ok) {
    const message = payload?.message || payload?.error || (typeof payload === 'string' ? payload : null);
    throw new ApiError(message || `İşlem tamamlanamadı (${response.status}).`, {
      status: response.status,
      data: payload,
      url: requestUrl,
      method,
      hasAuthorizationHeader: Boolean(headers.Authorization),
    });
  }

  return payload;
}

export function getApiErrorMessage(error, fallback = 'İşlem sırasında bir hata oluştu.') {
  const data = error?.data ?? error?.response?.data;
  if (typeof data === 'string' && data.trim()) return data.trim();
  if (typeof data?.message === 'string' && data.message.trim()) return data.message.trim();
  if (typeof data?.error === 'string' && data.error.trim()) return data.error.trim();
  const statusMessages = {
    400: 'Gönderilen bilgiler geçerli değil. Lütfen formu kontrol edin.',
    401: 'Oturumunuz geçersiz veya süresi dolmuş. Lütfen tekrar giriş yapın.',
    403: 'Bu işlem için gerekli yetkiye sahip değilsiniz.',
    404: 'İstenen kayıt bulunamadı.',
    409: 'Bu işlem mevcut bir kayıtla çakışıyor.',
    500: 'Sunucuda bir hata oluştu. Lütfen daha sonra tekrar deneyin.',
  };
  if (statusMessages[error?.status]) return statusMessages[error.status];
  if (typeof error?.message === 'string' && error.message.trim()) return error.message.trim();
  return fallback;
}

export function asList(payload) {
  if (Array.isArray(payload)) return payload;
  if (Array.isArray(payload?.content)) return payload.content;
  return [];
}
