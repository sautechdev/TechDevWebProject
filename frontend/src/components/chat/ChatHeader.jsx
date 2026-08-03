import { Link } from 'react-router-dom';

const roleLabels = {
  OWNER: 'Proje sahibi',
  MEMBER: 'Proje üyesi',
};

function ChatHeader({ connectionState, projectTitle, role }) {
  const connectionLabels = {
    connecting: 'Bağlantı kontrol ediliyor',
    connected: 'Mesajlar güncel',
    retrying: 'Bağlantı yeniden deneniyor',
  };

  return <header className="chat-header">
    <Link className="chat-header__back" to="/projects" aria-label="Projeler sayfasına dön">←</Link>
    <div className="chat-header__title">
      <span>Proje Sohbeti</span>
      <h1>{projectTitle || 'TechDev projesi'}</h1>
    </div>
    <div className="chat-header__status">
      <span className={`chat-status-dot chat-status-dot--${connectionState}`} aria-hidden="true" />
      <div><strong>{connectionLabels[connectionState]}</strong><small>{roleLabels[role] || 'Proje üyesi'}</small></div>
    </div>
  </header>;
}

export default ChatHeader;
