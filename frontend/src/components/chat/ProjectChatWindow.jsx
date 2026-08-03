import { useCallback, useEffect, useRef, useState } from 'react';
import { getApiErrorMessage } from '../../services/apiClient.js';
import { chatApi } from '../../services/chatApi.js';
import ChatComposer from './ChatComposer.jsx';
import ChatHeader from './ChatHeader.jsx';
import ChatMessageList from './ChatMessageList.jsx';
import NewMessagesIndicator from './NewMessagesIndicator.jsx';
import './ProjectChat.css';

const POLLING_INTERVAL = 4000;

function sortMessages(messages) {
  return [...messages].sort((first, second) => {
    const firstTime = new Date(first.sentAt || 0).getTime();
    const secondTime = new Date(second.sentAt || 0).getTime();
    if (firstTime !== secondTime) return firstTime - secondTime;
    return String(first.id).localeCompare(String(second.id), undefined, { numeric: true });
  });
}

function mergeMessages(current, incoming) {
  const messagesById = new Map(current.map((message) => [String(message.id), message]));
  incoming.forEach((message) => messagesById.set(String(message.id), message));
  return sortMessages([...messagesById.values()]);
}

function getLoadError(error) {
  if (error?.status === 401) return 'Oturumunuz geçersiz. Lütfen yeniden giriş yapın.';
  if (error?.status === 403) return 'Bu proje sohbetine erişim yetkiniz bulunmuyor.';
  if (error?.status === 404) return 'Proje veya sohbet bulunamadı.';
  if (error?.status == null) return 'Sohbet sunucusuna bağlanılamadı. Lütfen bağlantınızı kontrol edin.';
  return getApiErrorMessage(error, 'Mesajlar yüklenemedi. Lütfen tekrar deneyin.');
}

function getSendError(error) {
  if (error?.status === 401) return 'Oturumunuz geçersiz. Lütfen yeniden giriş yapın.';
  if (error?.status === 403) return 'Bu proje sohbetine erişim yetkiniz bulunmuyor.';
  if (error?.status === 404) return 'Proje veya sohbet bulunamadı.';
  if (error?.status == null) return 'Sohbet sunucusuna bağlanılamadı. Lütfen bağlantınızı kontrol edin.';
  return getApiErrorMessage(error, 'Mesaj gönderilemedi. Lütfen tekrar deneyin.');
}

function ProjectChatWindow({ currentUserId, projectId, projectTitle, role }) {
  const [messages, setMessages] = useState([]);
  const [initialLoading, setInitialLoading] = useState(true);
  const [loadError, setLoadError] = useState('');
  const [sendError, setSendError] = useState('');
  const [sending, setSending] = useState(false);
  const [connectionState, setConnectionState] = useState('connecting');
  const [hasNewMessages, setHasNewMessages] = useState(false);
  const listRef = useRef(null);
  const lastMessageIdRef = useRef(null);
  const nearBottomRef = useRef(true);
  const forceScrollRef = useRef(true);
  const sendInFlightRef = useRef(false);

  const scrollToBottom = useCallback((behavior = 'smooth') => {
    const element = listRef.current;
    if (!element) return;
    element.scrollTo({ top: element.scrollHeight, behavior });
    nearBottomRef.current = true;
    setHasNewMessages(false);
  }, []);

  const addMessages = useCallback((incoming, forceScroll = false) => {
    if (!incoming.length) return;
    forceScrollRef.current = forceScroll || nearBottomRef.current;
    if (!forceScrollRef.current) setHasNewMessages(true);
    setMessages((current) => {
      const next = mergeMessages(current, incoming);
      lastMessageIdRef.current = next.at(-1)?.id ?? lastMessageIdRef.current;
      return next;
    });
  }, []);

  useEffect(() => {
    if (!messages.length) return;
    if (forceScrollRef.current) {
      forceScrollRef.current = false;
      window.requestAnimationFrame(() => scrollToBottom(initialLoading ? 'auto' : 'smooth'));
    }
  }, [initialLoading, messages, scrollToBottom]);

  const loadInitialMessages = useCallback(async () => {
    setInitialLoading(true);
    setLoadError('');
    setConnectionState('connecting');
    try {
      const result = await chatApi.getMessages(projectId);
      lastMessageIdRef.current = result.at(-1)?.id ?? null;
      forceScrollRef.current = true;
      setMessages(sortMessages(result));
      setConnectionState('connected');
    } catch (error) {
      setLoadError(getLoadError(error));
      setConnectionState('retrying');
    } finally {
      setInitialLoading(false);
    }
  }, [projectId]);

  useEffect(() => {
    let active = true;
    let requestInProgress = false;

    async function initialLoad() {
      setInitialLoading(true);
      setLoadError('');
      setConnectionState('connecting');
      try {
        const result = await chatApi.getMessages(projectId);
        if (!active) return;
        lastMessageIdRef.current = result.at(-1)?.id ?? null;
        forceScrollRef.current = true;
        setMessages(sortMessages(result));
        setConnectionState('connected');
      } catch (error) {
        if (!active) return;
        setLoadError(getLoadError(error));
        setConnectionState('retrying');
      } finally {
        if (active) setInitialLoading(false);
      }
    }

    async function pollMessages() {
      if (!active || requestInProgress || document.visibilityState !== 'visible') return;
      requestInProgress = true;
      try {
        const result = await chatApi.getMessages(projectId, lastMessageIdRef.current);
        if (!active) return;
        addMessages(result);
        setLoadError('');
        setConnectionState('connected');
      } catch {
        if (active) setConnectionState('retrying');
      } finally {
        requestInProgress = false;
      }
    }

    initialLoad();
    const intervalId = window.setInterval(pollMessages, POLLING_INTERVAL);
    return () => {
      active = false;
      window.clearInterval(intervalId);
    };
  }, [addMessages, projectId]);

  async function sendMessage(content) {
    if (sendInFlightRef.current) return false;
    sendInFlightRef.current = true;
    setSending(true);
    setSendError('');
    try {
      const sentMessage = await chatApi.sendMessage(projectId, content);
      if (sentMessage) addMessages([sentMessage], true);
      else forceScrollRef.current = true;
      setConnectionState('connected');
      return true;
    } catch (error) {
      setSendError(getSendError(error));
      if (error?.status == null) setConnectionState('retrying');
      return false;
    } finally {
      sendInFlightRef.current = false;
      setSending(false);
    }
  }

  function handleNearBottomChange(isNearBottom) {
    nearBottomRef.current = isNearBottom;
    if (isNearBottom) setHasNewMessages(false);
  }

  return <section className="project-chat" aria-label={`${projectTitle || 'Proje'} sohbeti`}>
    <ChatHeader connectionState={connectionState} projectTitle={projectTitle} role={role} />
    <div className="project-chat__body">
      {initialLoading && <div className="chat-loading" role="status">Mesajlar yükleniyor…</div>}
      {!initialLoading && loadError && <div className="chat-load-error" role="alert"><strong>Mesajlar yüklenemedi.</strong><p>{loadError}</p><button type="button" onClick={loadInitialMessages}>Tekrar Dene</button></div>}
      {!initialLoading && !loadError && <ChatMessageList currentUserId={currentUserId} listRef={listRef} messages={messages} onNearBottomChange={handleNearBottomChange} />}
      <NewMessagesIndicator visible={hasNewMessages} onClick={() => scrollToBottom()} />
    </div>
    <ChatComposer disabled={initialLoading || Boolean(loadError)} error={sendError} onSend={sendMessage} sending={sending} />
  </section>;
}

export default ProjectChatWindow;
