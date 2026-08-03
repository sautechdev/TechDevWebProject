import { apiRequest } from './apiClient.js';

export const projectApi = {
  list: ({ status = 'ACTIVE', page = 0, size = 24 } = {}) => {
    const params = new URLSearchParams({ page, size });
    if (status) params.set('status', status);
    return apiRequest(`/api/projects?${params}`);
  },
  get: (id) => apiRequest(`/api/projects/${id}`),
  getExpertiseAreas: (id) => apiRequest(`/api/projects/${id}/expertise-areas`),
  getMembers: (id) => apiRequest(`/api/projects/${id}/members`),
  getMyRole: (id) => apiRequest(`/api/projects/${id}/my-role`),
  create: ({ title, description }) => apiRequest('/api/projects', {
    method: 'POST',
    body: JSON.stringify({ title: title.trim(), description: description.trim() }),
  }),
  update: (id, details) => apiRequest(`/api/projects/${id}`, { method: 'PUT', body: JSON.stringify(details) }),
  remove: (id) => apiRequest(`/api/projects/${id}`, { method: 'DELETE' }),
  apply: (areaId, message) => apiRequest(`/api/expertise-areas/${areaId}/applications`, {
    method: 'POST', body: JSON.stringify({ message }),
  }),
};
