function ChatEmptyState() {
  return <div className="chat-empty-state">
    <span aria-hidden="true">•••</span>
    <strong>Henüz mesaj bulunmuyor.</strong>
    <p>Proje ekibine ilk mesajı siz gönderin.</p>
  </div>;
}

export default ChatEmptyState;
