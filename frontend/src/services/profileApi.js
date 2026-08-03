import { apiRequest } from './apiClient.js';

export const profileApi = {
  getMe: () => apiRequest('/api/users/me'),
  updateMe: (details) => apiRequest('/api/users/me', {
    method: 'PUT', body: JSON.stringify(details),
  }),
  getProjects: () => apiRequest('/api/users/me/projects'),
  getApplications: () => apiRequest('/api/users/me/applications'),
};
