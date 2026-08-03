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
