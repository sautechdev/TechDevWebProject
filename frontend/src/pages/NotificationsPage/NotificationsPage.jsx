import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Pagination from '../../components/common/Pagination.jsx';
import { useNotifications } from '../../contexts/NotificationContext.jsx';
import { getApiErrorMessage } from '../../services/apiClient.js';
import { getNotificationTarget, notificationApi } from '../../services/notificationApi.js';

const typeLabels = {
  EVENT_REGISTRATION_CONFIRMED: 'Etkinlik kaydı',
  EVENT_REGISTRATION_PENDING: 'Etkinlik kaydı',
  EVENT_REGISTRATION_APPROVED: 'Etkinlik kaydı',
  EVENT_REGISTRATION_REJECTED: 'Etkinlik kaydı',
  EVENT_CANCELLED: 'Etkinlik',
  EVENT_REMINDER: 'Hatırlatma',
  APPLICATION_RECEIVED: 'Proje başvurusu',
  APPLICATION_ACCEPTED: 'Proje başvurusu',
  APPLICATION_REJECTED: 'Proje başvurusu',
};

function formatNotificationDate(value) {
  return new Intl.DateTimeFormat('tr-TR', {
    day: '2-digit', month: 'long', year: 'numeric', hour: '2-digit', minute: '2-digit',
  }).format(new Date(value));
}

function NotificationsPage() {
  const navigate = useNavigate();
  const { markAsRead, markAllAsRead, refresh } = useNotifications();
  const [notifications, setNotifications] = useState([]);
  const [onlyUnread, setOnlyUnread] = useState(false);
  const [page, setPage] = useState(0);
  const [pageInfo, setPageInfo] = useState({ pageNumber: 0, totalPages: 0, totalElements: 0 });
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    let active = true;
    setLoading(true);
    notificationApi.list({ onlyUnread, page, size: 12, sort: 'createdAt,desc' })
      .then((data) => {
        if (!active) return;
        setNotifications(data?.content || []);
        setPageInfo(data || { pageNumber: page, totalPages: 0, totalElements: 0 });
        setError('');
      })
      .catch((requestError) => active && setError(getApiErrorMessage(requestError, 'Bildirimler alınamadı.')))
      .finally(() => active && setLoading(false));
    return () => { active = false; };
  }, [onlyUnread, page]);

  async function openNotification(notification) {
    try {
      if (!notification.read) await markAsRead(notification.id);
      setNotifications((items) => items.map((item) => item.id === notification.id ? { ...item, read: true } : item));
      navigate(getNotificationTarget(notification));
    } catch (requestError) {
      setError(getApiErrorMessage(requestError, 'Bildirim açılamadı.'));
    }
  }

  async function readAll() {
    setActionLoading(true);
    try {
      await markAllAsRead();
      if (onlyUnread) {
        setNotifications([]);
        setPageInfo((current) => ({ ...current, totalElements: 0, totalPages: 0 }));
      } else {
        setNotifications((items) => items.map((item) => ({ ...item, read: true })));
      }
      await refresh();
    } catch (requestError) {
      setError(getApiErrorMessage(requestError, 'Bildirimler güncellenemedi.'));
    } finally {
      setActionLoading(false);
    }
  }

  function changeFilter(value) {
    setOnlyUnread(value);
    setPage(0);
  }

  return (
    <section className="feature-page notifications-page">
      <div className="feature-hero feature-hero--notifications">
        <div className="page-heading"><p className="page-heading__eyebrow">Bildirim merkezi</p><h1>Topluluktaki gelişmeler tek bir akışta.</h1><p>Etkinlik katılımı ve proje başvurularıyla ilgili bildirimlerini buradan takip et.</p></div>
        <div className="feature-hero__metric"><strong>{pageInfo.totalElements || 0}</strong><span>{onlyUnread ? 'okunmamış' : 'bildirim'}</span></div>
      </div>

      <div className="notification-toolbar">
        <div className="segmented-control" aria-label="Bildirim filtresi"><button className={!onlyUnread ? 'is-active' : ''} type="button" onClick={() => changeFilter(false)}>Tümü</button><button className={onlyUnread ? 'is-active' : ''} type="button" onClick={() => changeFilter(true)}>Okunmamış</button></div>
        <button className="button-secondary" type="button" disabled={actionLoading || notifications.length === 0} onClick={readAll}>{actionLoading ? 'Güncelleniyor…' : 'Tümünü okundu yap'}</button>
      </div>

      {loading && <div className="state-card">Bildirimler yükleniyor…</div>}
      {error && <div className="state-card state-card--error" role="alert"><strong>Bildirimlere ulaşılamadı</strong><p>{error}</p></div>}
      {!loading && !error && notifications.length === 0 && <div className="state-card"><strong>{onlyUnread ? 'Okunmamış bildirimin yok' : 'Henüz bildirimin yok'}</strong><p>Yeni bir gelişme olduğunda burada göreceksin.</p></div>}
      {!error && notifications.length > 0 && (
        <div className="notification-list">
          {notifications.map((notification) => (
            <button className={notification.read ? 'notification-row' : 'notification-row is-unread'} key={notification.id} type="button" onClick={() => openNotification(notification)}>
              <span className="notification-row__dot" aria-hidden="true" />
              <span className="notification-row__body"><small>{typeLabels[notification.type] || 'Bildirim'}</small><strong>{notification.title}</strong><p>{notification.message}</p></span>
              <time dateTime={notification.createdAt}>{formatNotificationDate(notification.createdAt)}</time>
              <b aria-hidden="true">→</b>
            </button>
          ))}
        </div>
      )}
      <Pagination pageNumber={pageInfo.pageNumber || 0} totalPages={pageInfo.totalPages || 0} onPageChange={setPage} disabled={loading} />
    </section>
  );
}

export default NotificationsPage;
