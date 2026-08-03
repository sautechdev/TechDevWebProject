import { apiRequest } from './apiClient.js';

function queryString(params) {
  const query = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') query.set(key, value);
  });
  return query.toString();
}

export const notificationApi = {
  list: (params = {}) => apiRequest(`/api/notifications?${queryString(params)}`),
  markAsRead: (id) => apiRequest(`/api/notifications/${id}/read`, { method: 'PATCH' }),
  markAllAsRead: () => apiRequest('/api/notifications/read-all', { method: 'PATCH' }),
};

export function getNotificationTarget(notification) {
  if (!notification?.relatedEntityId) return '/notifications';
  if (String(notification.type || '').startsWith('EVENT_')) {
    return `/events/${notification.relatedEntityId}`;
  }
  if (String(notification.type || '').startsWith('APPLICATION_')) return '/projects';
  return '/notifications';
}
