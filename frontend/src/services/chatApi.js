import { apiRequest, asList } from './apiClient.js';

function normalizeMessage(message) {
  if (!message || message.id == null) return null;

  return {
    id: message.id,
    content: typeof message.content === 'string' ? message.content : '',
    sentAt: message.sentAt || null,
    sender: message.sender ? {
      id: message.sender.id,
      email: message.sender.email || '',
      fullName: message.sender.fullName || '',
      role: message.sender.role || '',
    } : null,
  };
}

function normalizeMessages(payload) {
  return asList(payload).map(normalizeMessage).filter(Boolean);
}

export const chatApi = {
  getMessages: async (projectId, afterId) => {
    const params = new URLSearchParams();
    if (afterId != null) params.set('after', String(afterId));
    const query = params.size ? `?${params}` : '';
    return normalizeMessages(await apiRequest(`/api/projects/${projectId}/chat/messages${query}`));
  },
  sendMessage: async (projectId, content) => normalizeMessage(await apiRequest(
    `/api/projects/${projectId}/chat/messages`,
    { method: 'POST', body: JSON.stringify({ content: content.trim() }) },
  )),
};
