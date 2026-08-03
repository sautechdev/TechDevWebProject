import { createContext, useContext, useState } from 'react';
import { AUTH_STORAGE_KEY, getStoredAuth, normalizeAuthToken } from '../services/apiClient.js';
import { authApi } from '../services/authApi.js';
import { clearPendingVerificationEmail } from '../services/pendingVerification.js';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [auth, setAuth] = useState(getStoredAuth);
  const [isLoading, setIsLoading] = useState(false);
  const [sessionMessage, setSessionMessage] = useState('');

  function completeAuthentication(response) {
    const token = normalizeAuthToken(response.token);
    if (!token) throw new Error('Giriş yanıtında geçerli bir token bulunamadı.');
    const nextAuth = {
      token,
      currentUser: {
        userId: response.userId,
        email: response.email,
        fullName: response.fullName,
        role: response.role,
      },
    };
    localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(nextAuth));
    clearPendingVerificationEmail();
    setSessionMessage('');
    setAuth(nextAuth);
    return nextAuth.currentUser;
  }

  async function login(credentials) {
    setIsLoading(true);
    try {
      const response = await authApi.login(credentials);
      return completeAuthentication(response);
    } finally {
      setIsLoading(false);
    }
  }

  async function register(details) {
    setIsLoading(true);
    try {
      return await authApi.register(details);
    } finally {
      setIsLoading(false);
    }
  }

  async function verifyEmail(email, code) {
    setIsLoading(true);
    try {
      const response = await authApi.verifyEmail(email, code);
      completeAuthentication(response);
      return response;
    } finally {
      setIsLoading(false);
    }
  }

  function resendVerification(email) {
    return authApi.resendVerification(email);
  }

  function updateCurrentUser(changes) {
    setAuth((current) => {
      if (!current) return current;
      const next = { ...current, currentUser: { ...current.currentUser, ...changes } };
      localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(next));
      return next;
    });
  }

  function logout() {
    localStorage.removeItem(AUTH_STORAGE_KEY);
    clearPendingVerificationEmail();
    setAuth(null);
  }

  function showSessionMessage(message) {
    setSessionMessage(typeof message === 'string' ? message : '');
  }

  const value = {
    token: auth?.token || null,
    currentUser: auth?.currentUser || null,
    isAuthenticated: Boolean(auth?.token),
    isLoading,
    sessionMessage,
    login,
    register,
    verifyEmail,
    resendVerification,
    logout,
    showSessionMessage,
    updateCurrentUser,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

// Hook ve provider aynı modülde tutularak auth API'si tek giriş noktasında kalır.
// eslint-disable-next-line react-refresh/only-export-components
export function useAuth() {
  const value = useContext(AuthContext);
  if (!value) throw new Error('useAuth, AuthProvider içinde kullanılmalıdır.');
  return value;
}
