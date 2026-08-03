import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useRef,
  useState,
} from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from './AuthContext.jsx';
import { API_BASE_URL } from '../services/apiClient.js';
import { notificationApi } from '../services/notificationApi.js';
import { profileApi } from '../services/profileApi.js';

const NotificationContext = createContext(null);
const MAX_RECONNECT_ATTEMPTS = 3;
const RECONNECT_DELAY_MS = 4000;
const AUTH_FAILURE_MESSAGE = 'Oturumunuzun süresi doldu. Lütfen yeniden giriş yapın.';
const AUTH_REJECTION_PATTERN = /\b(?:401|403)\b|unauthori[sz]ed|forbidden|authentication|jwt[^\n]*(?:expired|invalid)|(?:expired|invalid)[^\n]*token|kimlik doğrulama/iu;

function getJwtExpiration(token) {
  try {
    const parts = token.split('.');
    if (parts.length !== 3) return null;

    const base64 = parts[1].replace(/-/g, '+').replace(/_/g, '/');
    const padded = base64.padEnd(Math.ceil(base64.length / 4) * 4, '=');
    const payload = JSON.parse(atob(padded));
    return Number.isFinite(payload?.exp) ? payload.exp * 1000 : null;
  } catch {
    return null;
  }
}

function hasAuthenticationRejection(details) {
  if (details?.code === 4401 || details?.code === 4403) return true;

  const message = [
    details?.reason,
    details?.body,
    details?.headers?.message,
  ].filter((value) => typeof value === 'string').join(' ');

  return AUTH_REJECTION_PATTERN.test(message);
}

export function NotificationProvider({ children }) {
  const { isAuthenticated, token, logout, showSessionMessage } = useAuth();
  const navigate = useNavigate();
  const [recentNotifications, setRecentNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [isLive, setIsLive] = useState(false);
  const [connectionStatus, setConnectionStatus] = useState('disconnected');
  const stompClientRef = useRef(null);
  const notificationSubscriptionRef = useRef(null);
  const reconnectAttemptsRef = useRef(0);
  const authFailureHandledRef = useRef(false);
  const intentionalDisconnectsRef = useRef(new WeakSet());
  const connectionGenerationRef = useRef(0);
  const tokenRef = useRef(token);
  const logoutRef = useRef(logout);
  const showSessionMessageRef = useRef(showSessionMessage);
  const navigateRef = useRef(navigate);

  tokenRef.current = token;
  logoutRef.current = logout;
  showSessionMessageRef.current = showSessionMessage;
  navigateRef.current = navigate;

  const refresh = useCallback(async () => {
    if (!isAuthenticated) return;
    setIsLoading(true);
    try {
      const [recent, unread] = await Promise.all([
        notificationApi.list({ page: 0, size: 6, sort: 'createdAt,desc' }),
        notificationApi.list({ onlyUnread: true, page: 0, size: 1, sort: 'createdAt,desc' }),
      ]);
      setRecentNotifications(recent?.content || []);
      setUnreadCount(unread?.totalElements || 0);
      setError('');
    } catch (requestError) {
      setError(requestError.message);
    } finally {
      setIsLoading(false);
    }
  }, [isAuthenticated]);

  const deactivateClient = useCallback(async (client, { resetAttempts = true } = {}) => {
    if (!client) {
      if (!stompClientRef.current) notificationSubscriptionRef.current = null;
      if (resetAttempts) reconnectAttemptsRef.current = 0;
      return;
    }

    intentionalDisconnectsRef.current.add(client);
    client.reconnectDelay = 0;

    try {
      if (client.active) await client.deactivate();
    } finally {
      if (stompClientRef.current === client) {
        stompClientRef.current = null;
        notificationSubscriptionRef.current = null;
      }
      if (resetAttempts) reconnectAttemptsRef.current = 0;
    }
  }, []);

  const disconnectNotifications = useCallback(async () => {
    const client = stompClientRef.current;
    await deactivateClient(client);
    setIsLive(false);
    setConnectionStatus('disconnected');
  }, [deactivateClient]);

  const handleAuthenticationFailure = useCallback(async (failedToken) => {
    if (authFailureHandledRef.current || tokenRef.current !== failedToken) return;
    authFailureHandledRef.current = true;
    setIsLive(false);
    setConnectionStatus('auth-error');
    showSessionMessageRef.current(AUTH_FAILURE_MESSAGE);
    await disconnectNotifications();

    if (tokenRef.current === failedToken) {
      logoutRef.current();
      navigateRef.current('/login', { replace: true });
    }
  }, [disconnectNotifications]);

  const verifySessionAfterFailures = useCallback(async (failedToken, client) => {
    await deactivateClient(client, { resetAttempts: false });
    if (tokenRef.current !== failedToken || authFailureHandledRef.current) return;

    try {
      await profileApi.getMe();
    } catch (requestError) {
      if (requestError?.status === 401 || requestError?.status === 403) {
        await handleAuthenticationFailure(failedToken);
      }
    }
  }, [deactivateClient, handleAuthenticationFailure]);

  useEffect(() => {
    if (!isAuthenticated) {
      setRecentNotifications([]);
      setUnreadCount(0);
      setError('');
      return undefined;
    }
    refresh();
    const timer = window.setInterval(refresh, 60000);
    return () => window.clearInterval(timer);
  }, [isAuthenticated, refresh]);

  useEffect(() => {
    const generation = connectionGenerationRef.current + 1;
    connectionGenerationRef.current = generation;
    let effectClient = null;

    if (!isAuthenticated || !token) {
      void disconnectNotifications();
      return undefined;
    }

    authFailureHandledRef.current = false;
    reconnectAttemptsRef.current = 0;
    setConnectionStatus('connecting');

    const connectNotifications = async () => {
      await disconnectNotifications();

      if (connectionGenerationRef.current !== generation || tokenRef.current !== token) return;
      setConnectionStatus('connecting');

      const expiration = getJwtExpiration(token);
      if (expiration !== null && expiration <= Date.now()) {
        await handleAuthenticationFailure(token);
        return;
      }

      const client = new Client({
        webSocketFactory: () => new SockJS(`${API_BASE_URL}/ws?token=${encodeURIComponent(token)}`),
        reconnectDelay: RECONNECT_DELAY_MS,
        heartbeatIncoming: 10000,
        heartbeatOutgoing: 10000,
        debug: () => {},
      });

      effectClient = client;
      stompClientRef.current = client;

      client.onConnect = () => {
        if (stompClientRef.current !== client || tokenRef.current !== token) return;
        reconnectAttemptsRef.current = 0;
        setIsLive(true);
        setConnectionStatus('connected');
        notificationSubscriptionRef.current = client.subscribe('/user/queue/notifications', (frame) => {
          try {
            const notification = JSON.parse(frame.body);
            setRecentNotifications((current) => [
              notification,
              ...current.filter((item) => item.id !== notification.id),
            ].slice(0, 6));
            if (!notification.read) setUnreadCount((current) => current + 1);
          } catch {
            refresh();
          }
        });
      };

      client.onStompError = (frame) => {
        if (stompClientRef.current !== client) return;
        setIsLive(false);

        if (hasAuthenticationRejection(frame)) {
          void handleAuthenticationFailure(token);
        }
      };

      client.onWebSocketError = (event) => {
        if (stompClientRef.current !== client) return;
        setIsLive(false);

        if (import.meta.env.DEV) {
          console.warn('Notification WebSocket connection error', {
            eventType: event?.type,
          });
        }
      };

      client.onWebSocketClose = (event) => {
        if (stompClientRef.current !== client || intentionalDisconnectsRef.current.has(client)) return;

        setIsLive(false);

        if (hasAuthenticationRejection(event)) {
          void handleAuthenticationFailure(token);
          return;
        }

        reconnectAttemptsRef.current += 1;
        const attempt = reconnectAttemptsRef.current;

        if (import.meta.env.DEV) {
          console.warn('Notification WebSocket connection failed', {
            attempt,
            maxAttempts: MAX_RECONNECT_ATTEMPTS,
            eventType: event?.type,
            closeCode: event?.code,
          });
        }

        if (attempt >= MAX_RECONNECT_ATTEMPTS) {
          client.reconnectDelay = 0;
          setConnectionStatus('disconnected');
          void verifySessionAfterFailures(token, client);
          return;
        }

        setConnectionStatus('reconnecting');
      };

      client.activate();
    };

    void connectNotifications();

    return () => {
      if (connectionGenerationRef.current === generation) {
        connectionGenerationRef.current += 1;
      }
      void deactivateClient(effectClient);
    };
  }, [
    deactivateClient,
    disconnectNotifications,
    handleAuthenticationFailure,
    isAuthenticated,
    refresh,
    token,
    verifySessionAfterFailures,
  ]);

  const markAsRead = useCallback(async (id) => {
    const current = recentNotifications.find((item) => item.id === id);
    if (current?.read) return;
    await notificationApi.markAsRead(id);
    setRecentNotifications((items) => items.map((item) => (
      item.id === id ? { ...item, read: true } : item
    )));
    setUnreadCount((count) => Math.max(0, count - 1));
  }, [recentNotifications]);

  const markAllAsRead = useCallback(async () => {
    await notificationApi.markAllAsRead();
    setRecentNotifications((items) => items.map((item) => ({ ...item, read: true })));
    setUnreadCount(0);
  }, []);

  const value = useMemo(() => ({
    recentNotifications,
    unreadCount,
    isLoading,
    error,
    isLive,
    connectionStatus,
    refresh,
    markAsRead,
    markAllAsRead,
  }), [
    connectionStatus,
    error,
    isLive,
    isLoading,
    markAllAsRead,
    markAsRead,
    recentNotifications,
    refresh,
    unreadCount,
  ]);

  return <NotificationContext.Provider value={value}>{children}</NotificationContext.Provider>;
}

// eslint-disable-next-line react-refresh/only-export-components
export function useNotifications() {
  const value = useContext(NotificationContext);
  if (!value) throw new Error('useNotifications, NotificationProvider içinde kullanılmalıdır.');
  return value;
}
