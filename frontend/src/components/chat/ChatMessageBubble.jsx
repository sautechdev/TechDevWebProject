function getSenderName(sender) {
  return sender?.fullName || sender?.email || 'Proje üyesi';
}

function getInitials(sender) {
  const name = getSenderName(sender).replace(/@.*$/, '').trim();
  const words = name.split(/\s+/).filter(Boolean);
  return words.slice(0, 2).map((word) => word[0]?.toLocaleUpperCase('tr-TR')).join('') || 'T';
}

function formatTime(value) {
  if (!value) return '';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return '';
  return new Intl.DateTimeFormat('tr-TR', { hour: '2-digit', minute: '2-digit', hour12: false })
    .format(date)
    .replace(':', '.');
}

function ChatMessageBubble({ isOwn, message }) {
  const senderName = getSenderName(message.sender);

  return <article className={isOwn ? 'chat-message is-own' : 'chat-message'}>
    {!isOwn && <span className="chat-message__avatar" aria-hidden="true">{getInitials(message.sender)}</span>}
    <div className="chat-message__content">
      <div className="chat-message__meta">
        <strong>{isOwn ? 'Siz' : senderName}</strong>
        <time dateTime={message.sentAt || undefined}>{formatTime(message.sentAt)}</time>
      </div>
      <p>{message.content}</p>
    </div>
  </article>;
}

export default ChatMessageBubble;
