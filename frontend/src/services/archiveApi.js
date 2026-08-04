import { API_BASE_URL, ApiError, getStoredAuth, apiRequest } from './apiClient.js';

function queryString(params) {
  const query = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') query.set(key, value);
  });
  return query.toString();
}

export const archiveApi = {
  list: (params = {}) => apiRequest(`/api/archive-events?${queryString(params)}`),
  getById: (id) => apiRequest(`/api/archive-events/${id}`),
  getItems: (eventId) => apiRequest(`/api/archive-items/event/${eventId}`),
  createEvent: (details) => apiRequest('/api/archive-events', { method: 'POST', body: JSON.stringify(details) }),
  updateEvent: (id, details) => apiRequest(`/api/archive-events/${id}`, { method: 'PUT', body: JSON.stringify(details) }),
  deleteEvent: (id) => apiRequest(`/api/archive-events/${id}`, { method: 'DELETE' }),
  async uploadItem(eventId, file, type, caption) {
    const auth = getStoredAuth();
    const formData = new FormData();
    formData.append('eventId', eventId);
    formData.append('file', file);
    formData.append('type', type);
    if (caption) formData.append('caption', caption);
    const response = await fetch(`${API_BASE_URL}/api/archive-items/upload`, {
      method: 'POST',
      headers: auth?.token ? { Authorization: `Bearer ${auth.token}` } : {},
      body: formData,
    });
    if (!response.ok) {
      throw new ApiError(`Dosya yüklenemedi (${response.status}).`, { status: response.status });
    }
    return response.json();
  },
  deleteItem: (itemId) => apiRequest(`/api/archive-items/${itemId}`, { method: 'DELETE' }),
  async openFile(fileUrl) {
    const auth = getStoredAuth();
    const url = fileUrl.startsWith('http') ? fileUrl : `${API_BASE_URL}${fileUrl}`;
    const response = await fetch(url, {
      headers: auth?.token ? { Authorization: `Bearer ${auth.token}` } : {},
    });
    if (!response.ok) {
      throw new ApiError(`Dosya açılamadı (${response.status}).`, {
        status: response.status,
        url,
        method: 'GET',
        hasAuthorizationHeader: Boolean(auth?.token),
      });
    }
    return response.blob();
  },
};
