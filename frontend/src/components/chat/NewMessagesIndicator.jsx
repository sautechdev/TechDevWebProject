function NewMessagesIndicator({ onClick, visible }) {
  if (!visible) return null;
  return <button className="new-messages-indicator" type="button" onClick={onClick}>Yeni mesajlar var ↓</button>;
}

export default NewMessagesIndicator;
