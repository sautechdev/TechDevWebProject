import { useState } from 'react';

function ChatComposer({ disabled, error, onSend, sending }) {
  const [content, setContent] = useState('');

  async function submitMessage() {
    const normalized = content.trim();
    if (!normalized || sending || disabled) return;
    const sent = await onSend(normalized);
    if (sent) setContent('');
  }

  function handleKeyDown(event) {
    if (event.key === 'Enter' && !event.shiftKey && !event.nativeEvent.isComposing) {
      event.preventDefault();
      submitMessage();
    }
  }

  return <div className="chat-composer">
    {error && <p className="chat-composer__error" role="alert">{error}</p>}
    <div className="chat-composer__row">
      <label className="sr-only" htmlFor="chat-message-input">Mesajınızı yazın</label>
      <textarea id="chat-message-input" rows="2" value={content} disabled={disabled || sending}
        placeholder="Mesajınızı yazın…" onChange={(event) => setContent(event.target.value)} onKeyDown={handleKeyDown} />
      <button type="button" disabled={disabled || sending || !content.trim()} onClick={submitMessage}>
        {sending ? 'Gönderiliyor…' : 'Mesaj Gönder'}
      </button>
    </div>
    <small>Göndermek için Enter, yeni satır için Shift + Enter</small>
  </div>;
}

export default ChatComposer;
