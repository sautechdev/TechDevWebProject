import { apiRequest } from './apiClient.js';

export const techFieldApi = {
  list: () => apiRequest('/api/tech-fields'),
  getById: (id) => apiRequest(`/api/tech-fields/${id}`),
  listStacks: (fieldId) => apiRequest(`/api/tech-stacks/field/${fieldId}`),
  searchStacks: (keyword) => apiRequest(`/api/tech-stacks/search?keyword=${encodeURIComponent(keyword)}`),
  getStack: (id) => apiRequest(`/api/tech-stacks/${id}`),
  getStackContent: (stackId) => apiRequest(`/api/tech-contents/stack/${stackId}`),
};
