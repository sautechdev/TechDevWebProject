import { apiRequest } from './apiClient.js';

export const authApi = {
  login: (credentials) => apiRequest('/api/auth/login', {
    method: 'POST', body: JSON.stringify(credentials),
  }),
  register: (details) => apiRequest('/api/auth/register', {
    method: 'POST', body: JSON.stringify(details),
  }),
  verifyEmail: (email, code) => apiRequest('/api/auth/verify-email', {
    method: 'POST', body: JSON.stringify({ email, code }),
  }),
  resendVerification: (email) => apiRequest('/api/auth/resend-verification', {
    method: 'POST', body: JSON.stringify({ email }),
  }),
};
