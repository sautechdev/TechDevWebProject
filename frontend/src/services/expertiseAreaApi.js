import { apiRequest } from './apiClient.js';

export const expertiseAreaApi = {
  create: (projectId, { techFieldId, requiredCount }) =>
    apiRequest(`/api/projects/${projectId}/expertise-areas`, {
      method: 'POST',
      body: JSON.stringify({
        techField: { id: Number(techFieldId) },
        requiredCount: Number(requiredCount),
      }),
    }),
};
