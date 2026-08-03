import { apiRequest, asList } from './apiClient.js';

function normalizeSkills(payload) {
  return asList(payload).filter((skill) => skill?.id != null && typeof skill?.name === 'string');
}

export async function getSkillCatalog() {
  return normalizeSkills(await apiRequest('/api/skills'));
}

export async function getUserSkills() {
  return normalizeSkills(await apiRequest('/api/users/me/skills'));
}

export async function addUserSkill(skillId) {
  await apiRequest(`/api/users/me/skills/${skillId}`, { method: 'POST' });
}

export async function removeUserSkill(skillId) {
  await apiRequest(`/api/users/me/skills/${skillId}`, { method: 'DELETE' });
}
