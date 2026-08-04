import { useEffect, useRef, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { FaBell } from 'react-icons/fa';
import { useNotifications } from '../../contexts/NotificationContext.jsx';
import { getNotificationTarget } from '../../services/notificationApi.js';

function relativeTime(value) {
  const seconds = Math.round((new Date(value).getTime() - Date.now()) / 1000);
  const formatter = new Intl.RelativeTimeFormat('tr', { numeric: 'auto' });
  if (Math.abs(seconds) < 60) return formatter.format(seconds, 'second');
  const minutes = Math.round(seconds / 60);
  if (Math.abs(minutes) < 60) return formatter.format(minutes, 'minute');
  const hours = Math.round(minutes / 60);
  if (Math.abs(hours) < 24) return formatter.format(hours, 'hour');
  return formatter.format(Math.round(hours / 24), 'day');
}

function NotificationBell() {
  const [open, setOpen] = useState(false);
  const rootRef = useRef(null);
  const navigate = useNavigate();
  const { recentNotifications, unreadCount, isLoading, error, isLive, markAsRead } = useNotifications();

  useEffect(() => {
    function closeOnOutsideClick(event) {
      if (!rootRef.current?.contains(event.target)) setOpen(false);
    }
    document.addEventListener('pointerdown', closeOnOutsideClick);
    return () => document.removeEventListener('pointerdown', closeOnOutsideClick);
  }, []);

  async function openNotification(notification) {
    try {
      await markAsRead(notification.id);
    } catch {
      // Yönlendirme, okundu isteği geçici olarak başarısız olsa da kullanılabilir kalır.
    }
    setOpen(false);
    navigate(getNotificationTarget(notification));
  }

  return (
    <div className="notification-bell" ref={rootRef}>
      <button
        className="notification-bell__button"
        type="button"
        aria-label={unreadCount ? `${unreadCount} okunmamış bildirim` : 'Bildirimler'}
        aria-expanded={open}
        onClick={() => setOpen((value) => !value)}
      >
        <span aria-hidden="true"><FaBell /></span>
        {unreadCount > 0 && <strong>{unreadCount > 99 ? '99+' : unreadCount}</strong>}
      </button>
      {open && (
        <section className="notification-dropdown" aria-label="Son bildirimler">
          <header>
            <div>
              <h2>Bildirimler</h2>
              <span>{isLive ? 'Canlı bağlantı' : 'Düzenli yenileniyor'}</span>
            </div>
            <Link to="/notifications" onClick={() => setOpen(false)}>Tümünü gör</Link>
          </header>
          {isLoading && recentNotifications.length === 0 && <p className="notification-dropdown__state">Bildirimler yükleniyor…</p>}
          {error && recentNotifications.length === 0 && <p className="notification-dropdown__state">Bildirimler alınamadı.</p>}
          {!isLoading && !error && recentNotifications.length === 0 && <p className="notification-dropdown__state">Henüz bildiriminiz yok.</p>}
          <div className="notification-dropdown__list">
            {recentNotifications.map((notification) => (
              <button
                className={notification.read ? 'notification-mini' : 'notification-mini is-unread'}
                key={notification.id}
                type="button"
                onClick={() => openNotification(notification)}
              >
                <span>{notification.title}</span>
                <p>{notification.message}</p>
                <small>{relativeTime(notification.createdAt)}</small>
              </button>
            ))}
          </div>
        </section>
      )}
    </div>
  );
}

export default NotificationBell;
