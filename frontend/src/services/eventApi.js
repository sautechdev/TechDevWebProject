import { apiRequest } from './apiClient.js';

function queryString(params) {
  const query = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') query.set(key, value);
  });
  return query.toString();
}

export const eventApi = {
  list: (params = {}) => apiRequest(`/api/events?${queryString(params)}`),
  getById: (id) => apiRequest(`/api/events/${id}`),
  createEvent: (payload) => apiRequest('/api/events', {
    method: 'POST',
    body: JSON.stringify(payload),
  }),
  updateEvent: (eventId, payload) => apiRequest(`/api/events/${eventId}`, {
    method: 'PUT',
    body: JSON.stringify(payload),
  }),
  deleteEvent: (eventId) => apiRequest(`/api/events/${eventId}`, { method: 'DELETE' }),
  register: (eventId) => apiRequest(`/api/events/${eventId}/registrations`, { method: 'POST' }),
  unregister: (eventId) => apiRequest(`/api/events/${eventId}/registrations`, { method: 'DELETE' }),
};
