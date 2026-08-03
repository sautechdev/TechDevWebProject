import { Fragment } from 'react';
import ChatEmptyState from './ChatEmptyState.jsx';
import ChatMessageBubble from './ChatMessageBubble.jsx';

function getDateKey(value) {
  if (!value) return 'unknown';
  const date = new Date(value);
  return Number.isNaN(date.getTime()) ? 'unknown' : `${date.getFullYear()}-${date.getMonth()}-${date.getDate()}`;
}

function formatDateLabel(value) {
  if (!value) return 'Tarih bilinmiyor';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return 'Tarih bilinmiyor';
  return new Intl.DateTimeFormat('tr-TR', { day: 'numeric', month: 'long', year: 'numeric' }).format(date);
}

function ChatMessageList({ currentUserId, listRef, messages, onNearBottomChange }) {
  function handleScroll(event) {
    const element = event.currentTarget;
    onNearBottomChange(element.scrollHeight - element.scrollTop - element.clientHeight < 96);
  }

  if (!messages.length) return <div className="chat-message-list chat-message-list--empty" ref={listRef}><ChatEmptyState /></div>;

  let previousDate = null;

  return <div className="chat-message-list" ref={listRef} role="log" aria-live="polite" aria-label="Proje sohbeti mesajları" onScroll={handleScroll}>
    {messages.map((message) => {
      const dateKey = getDateKey(message.sentAt);
      const showDate = dateKey !== previousDate;
      previousDate = dateKey;
      const isOwn = currentUserId != null && String(message.sender?.id) === String(currentUserId);

      return <Fragment key={message.id}>
        {showDate && <div className="chat-date-separator"><span>{formatDateLabel(message.sentAt)}</span></div>}
        <ChatMessageBubble isOwn={isOwn} message={message} />
      </Fragment>;
    })}
  </div>;
}

export default ChatMessageList;
