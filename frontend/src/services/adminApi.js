import { apiRequest } from './apiClient.js';

export const adminApi = {
  getUsers: () => apiRequest('/api/admin/users'),
  getUser: (id) => apiRequest(`/api/admin/users/${id}`),
  createUser: (details) => apiRequest('/api/admin/users', { method: 'POST', body: JSON.stringify(details) }),
  updateUser: (id, details) => apiRequest(`/api/admin/users/${id}`, { method: 'PUT', body: JSON.stringify(details) }),
  deleteUser: (id) => apiRequest(`/api/admin/users/${id}`, { method: 'DELETE' }),
  getProjects: (status = '') => apiRequest(`/api/admin/projects${status ? `?status=${status}` : ''}`),
  getPendingProjects: () => apiRequest('/api/admin/projects/pending'),
  approveProject: (id) => apiRequest(`/api/admin/projects/${id}/approve`, { method: 'PUT' }),
  rejectProject: (id) => apiRequest(`/api/admin/projects/${id}/reject`, { method: 'PUT' }),
  updateProject: (id, details) => apiRequest(`/api/admin/projects/${id}`, { method: 'PUT', body: JSON.stringify(details) }),
  deleteProject: (id) => apiRequest(`/api/admin/projects/${id}`, { method: 'DELETE' }),
  getSkills: () => apiRequest('/api/skills'),
  createSkill: (name) => apiRequest('/api/admin/skills', { method: 'POST', body: JSON.stringify({ name }) }),
  deleteSkill: (id) => apiRequest(`/api/admin/skills/${id}`, { method: 'DELETE' }),
};
