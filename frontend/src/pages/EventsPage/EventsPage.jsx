import { useCallback, useEffect, useMemo, useState } from 'react';
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom';
import EventCapacity from '../../components/events/EventCapacity.jsx';
import EventDeleteDialog from '../../components/events/EventDeleteDialog.jsx';
import EventForm from '../../components/events/EventForm.jsx';
import EventRegistrationButton from '../../components/events/EventRegistrationButton.jsx';
import EventSection from '../../components/events/EventSection.jsx';
import EventStatusBadge from '../../components/events/EventStatusBadge.jsx';
import { useAuth } from '../../contexts/AuthContext.jsx';
import { getApiErrorMessage } from '../../services/apiClient.js';
import { eventRegistrationService } from '../../services/eventRegistrationService.js';
import { eventService } from '../../services/eventService.js';
import { formatEventDateRange, getEventPlace, getEventStatus } from '../../utils/eventUtils.js';
import './EventsPage.css';

function sortByStart(events) {
  return [...events].sort((a, b) => new Date(a.startDateTime) - new Date(b.startDateTime));
}

function updateCount(event, delta) {
  if (event.capacity === null || event.capacity === undefined) {
    return { ...event, registeredCount: Math.max(0, Number(event.registeredCount || 0) + delta) };
  }
  const registeredCount = Math.min(Number(event.capacity), Math.max(0, Number(event.registeredCount || 0) + delta));
  return { ...event, registeredCount, full: registeredCount >= Number(event.capacity) };
}

function EventsPage() {
  const navigate = useNavigate();
  const { isAuthenticated, currentUser } = useAuth();
  const [events, setEvents] = useState([]);
  const [filter, setFilter] = useState('ALL');
  const [searchTerm, setSearchTerm] = useState('');
  const [appliedSearch, setAppliedSearch] = useState('');
  const [registrations, setRegistrations] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [feedback, setFeedback] = useState('');
  const [actionEventId, setActionEventId] = useState(null);
  const [editor, setEditor] = useState(null);
  const [deleteTarget, setDeleteTarget] = useState(null);
  const isAdmin = currentUser?.role === 'ADMIN';

  const loadEvents = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const loadedEvents = await eventService.listVisible(appliedSearch);
      // Past events are intentionally excluded.
      // Archived event content is displayed on the Digital Archive page.
      setEvents(loadedEvents.filter((event) => ['ONGOING', 'UPCOMING'].includes(getEventStatus(event))));
    } catch (requestError) {
      setError(getApiErrorMessage(requestError, 'Etkinlikler yüklenemedi. Lütfen tekrar deneyin.'));
    } finally {
      setLoading(false);
    }
  }, [appliedSearch]);

  useEffect(() => { loadEvents(); }, [loadEvents]);
  useEffect(() => {
    setRegistrations(isAuthenticated ? eventRegistrationService.getRegistrationMap(currentUser?.userId) : {});
  }, [currentUser?.userId, isAuthenticated]);

  const ongoingEvents = useMemo(() => sortByStart(events.filter((event) => getEventStatus(event) === 'ONGOING')), [events]);
  const upcomingEvents = useMemo(() => sortByStart(events.filter((event) => getEventStatus(event) === 'UPCOMING')), [events]);
  const filteredOngoing = filter === 'UPCOMING' ? [] : ongoingEvents;
  const filteredUpcoming = filter === 'ONGOING' ? [] : upcomingEvents;

  function submitSearch(event) {
    event.preventDefault();
    setAppliedSearch(searchTerm.trim());
  }

  function openCreateForm() {
    if (isAdmin) setEditor({ mode: 'create', event: null });
  }

  function openEditForm(event) {
    if (isAdmin) setEditor({ mode: 'edit', event });
  }

  function openDeleteDialog(event) {
    if (isAdmin) setDeleteTarget(event);
  }

  function closeEditor() {
    setEditor(null);
  }

  function closeDeleteDialog() {
    setDeleteTarget(null);
  }

  async function refreshEvents(keyword, fallbackEvent) {
    try {
      const loadedEvents = await eventService.listVisible(keyword);
      setEvents(loadedEvents);
    } catch {
      if (!fallbackEvent) return;
      setEvents((current) => {
        const withoutCurrent = current.filter((item) => String(item.id) !== String(fallbackEvent.id));
        return ['ONGOING', 'UPCOMING'].includes(getEventStatus(fallbackEvent))
          ? [...withoutCurrent, fallbackEvent]
          : withoutCurrent;
      });
    }
  }

  async function saveEvent(payload) {
    if (!isAdmin) throw Object.assign(new Error('Bu işlem için admin yetkisine sahip değilsiniz.'), { status: 403 });
    const isEdit = editor?.mode === 'edit';
    const savedEvent = isEdit
      ? await eventService.update(editor.event.id, payload)
      : await eventService.create(payload);
    const nextSearch = isEdit ? appliedSearch : '';
    if (!isEdit) {
      setSearchTerm('');
      setAppliedSearch('');
      setFilter('ALL');
    }
    setEditor(null);
    setFeedback(isEdit ? 'Etkinlik başarıyla güncellendi.' : 'Etkinlik başarıyla oluşturuldu.');
    await refreshEvents(nextSearch, savedEvent);
  }

  async function deleteEvent(event) {
    if (!isAdmin) throw Object.assign(new Error('Bu işlem için admin yetkisine sahip değilsiniz.'), { status: 403 });
    await eventService.delete(event.id);
    setEvents((current) => current.filter((item) => String(item.id) !== String(event.id)));
    setDeleteTarget(null);
    setFeedback('Etkinlik başarıyla silindi.');
  }

  async function register(event) {
    if (!isAuthenticated) {
      setFeedback('Etkinliğe kayıt olmak için giriş yapmalısınız.');
      navigate('/login', { state: { from: { pathname: `/events/${event.id}` }, returnUrl: `/events/${event.id}` } });
      return;
    }
    setActionEventId(event.id);
    setFeedback('');
    try {
      const registration = await eventRegistrationService.register(currentUser.userId, event);
      setRegistrations((current) => ({ ...current, [String(event.id)]: registration }));
      if (registration.status === 'CONFIRMED') setEvents((current) => current.map((item) => String(item.id) === String(event.id) ? updateCount(item, 1) : item));
      setFeedback(registration.status === 'PENDING' ? 'Etkinlik kaydınız onaya gönderildi.' : 'Etkinlik kaydınız başarıyla oluşturuldu.');
    } catch (requestError) {
      if (requestError?.status === 409 && String(requestError.message).toLocaleLowerCase('tr-TR').includes('zaten')) {
        const map = eventRegistrationService.remember(currentUser.userId, event.id);
        setRegistrations(map);
        setFeedback('Bu etkinlik için daha önce oluşturulmuş bir kaydınız bulunuyor.');
      } else {
        setFeedback(getApiErrorMessage(requestError, 'Etkinlik kaydı oluşturulamadı. Lütfen tekrar deneyin.'));
      }
    } finally {
      setActionEventId(null);
    }
  }

  async function cancel(event) {
    setActionEventId(event.id);
    setFeedback('');
    const previous = registrations[String(event.id)];
    try {
      const map = await eventRegistrationService.cancel(currentUser.userId, event);
      setRegistrations(map);
      if (previous?.status !== 'PENDING') setEvents((current) => current.map((item) => String(item.id) === String(event.id) ? updateCount(item, -1) : item));
      setFeedback('Etkinlik kaydınız iptal edildi.');
    } catch (requestError) {
      setFeedback(getApiErrorMessage(requestError, 'Etkinlik kaydı iptal edilemedi. Lütfen tekrar deneyin.'));
    } finally {
      setActionEventId(null);
    }
  }

  const noResults = !loading && !error && filteredOngoing.length === 0 && filteredUpcoming.length === 0;

  return (
    <section className="events-page">
      <header className="events-hero">
        <div><p className="events-eyebrow">TechDev Programı</p><h1>Etkinlikler</h1><p>Devam eden ve yaklaşan TechDev etkinliklerini keşfedin, size uygun etkinliklere hemen kayıt olun.</p></div>
        <div className="events-hero__side">
          {isAdmin && <button type="button" className="events-add-button" onClick={openCreateForm}>Etkinlik Ekle</button>}
          <div className="events-hero__count"><strong>{ongoingEvents.length + upcomingEvents.length}</strong><span>aktif program</span></div>
        </div>
      </header>

      <div className="events-toolbar">
        <div className="events-filters" aria-label="Etkinlik filtresi">
          {[['ALL', 'Tümü'], ['ONGOING', 'Devam Edenler'], ['UPCOMING', 'Yaklaşanlar']].map(([value, label]) => (
            <button key={value} type="button" className={filter === value ? 'is-active' : ''} aria-pressed={filter === value} onClick={() => setFilter(value)}>{label}</button>
          ))}
        </div>
        <form onSubmit={submitSearch} role="search"><label className="sr-only" htmlFor="events-search">Etkinlik ara</label><input id="events-search" type="search" placeholder="Etkinlik ara" value={searchTerm} onChange={(event) => setSearchTerm(event.target.value)} /><button type="submit">Ara</button></form>
      </div>

      {feedback && <p className="events-feedback" role="status">{feedback}</p>}
      {loading && <div className="state-card">Etkinlikler yükleniyor…</div>}
      {error && <div className="state-card state-card--error" role="alert"><strong>Etkinlikler yüklenemedi</strong><p>{error}</p><button type="button" onClick={loadEvents}>Tekrar Dene</button></div>}
      {noResults && <div className="state-card"><strong>{appliedSearch ? 'Aramanızla eşleşen bir etkinlik bulunamadı.' : 'Şu anda devam eden veya yaklaşan bir etkinlik bulunmuyor.'}</strong></div>}

      {!loading && !error && !noResults && (
        <div className="events-sections">
          {filter !== 'UPCOMING' && <EventSection title="Şu Anda Devam Edenler" eyebrow="Canlı" events={filteredOngoing} emptyText="Şu anda devam eden bir etkinlik bulunmuyor." registrations={registrations} actionEventId={actionEventId} onRegister={register} onCancel={cancel} canManage={isAdmin} onEdit={openEditForm} onDelete={openDeleteDialog} featured />}
          {filter !== 'ONGOING' && <EventSection title="Yaklaşan Etkinlikler" eyebrow="Takvim" events={filteredUpcoming} emptyText="Henüz planlanmış bir etkinlik bulunmuyor." registrations={registrations} actionEventId={actionEventId} onRegister={register} onCancel={cancel} canManage={isAdmin} onEdit={openEditForm} onDelete={openDeleteDialog} />}
        </div>
      )}
      {isAdmin && editor && <EventForm key={`${editor.mode}-${editor.event?.id || 'new'}`} mode={editor.mode} event={editor.event} onSubmit={saveEvent} onClose={closeEditor} />}
      {isAdmin && deleteTarget && <EventDeleteDialog event={deleteTarget} onConfirm={deleteEvent} onClose={closeDeleteDialog} />}
    </section>
  );
}

export function EventDetailPage() {
  const { eventId } = useParams();
  const location = useLocation();
  const navigate = useNavigate();
  const { isAuthenticated, currentUser } = useAuth();
  const [event, setEvent] = useState(null);
  const [registration, setRegistration] = useState(null);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);
  const [error, setError] = useState('');
  const [feedback, setFeedback] = useState('');

  const loadEvent = useCallback(async () => {
    const loadedEvent = await eventService.getById(eventId);
    setEvent(loadedEvent);
    return loadedEvent;
  }, [eventId]);

  useEffect(() => {
    let active = true;
    loadEvent().catch((requestError) => active && setError(getApiErrorMessage(requestError, 'Etkinlik detayları yüklenemedi. Lütfen tekrar deneyin.'))).finally(() => active && setLoading(false));
    return () => { active = false; };
  }, [loadEvent]);

  useEffect(() => {
    const map = isAuthenticated ? eventRegistrationService.getRegistrationMap(currentUser?.userId) : {};
    setRegistration(map[String(eventId)] || null);
  }, [currentUser?.userId, eventId, isAuthenticated]);

  async function register(targetEvent) {
    if (!isAuthenticated) {
      navigate('/login', { state: { from: { pathname: location.pathname }, returnUrl: location.pathname } });
      return;
    }
    setActionLoading(true);
    setFeedback('');
    try {
      const result = await eventRegistrationService.register(currentUser.userId, targetEvent);
      setRegistration(result);
      if (result.status === 'CONFIRMED') setEvent((current) => updateCount(current, 1));
      setFeedback(result.status === 'PENDING' ? 'Etkinlik kaydınız onaya gönderildi.' : 'Etkinlik kaydınız başarıyla oluşturuldu.');
      await loadEvent();
    } catch (requestError) {
      setFeedback(getApiErrorMessage(requestError, 'Etkinlik kaydı oluşturulamadı. Lütfen tekrar deneyin.'));
    } finally {
      setActionLoading(false);
    }
  }

  async function cancel(targetEvent) {
    setActionLoading(true);
    setFeedback('');
    try {
      await eventRegistrationService.cancel(currentUser.userId, targetEvent);
      if (registration?.status !== 'PENDING') setEvent((current) => updateCount(current, -1));
      setRegistration(null);
      setFeedback('Etkinlik kaydınız iptal edildi.');
      await loadEvent();
    } catch (requestError) {
      setFeedback(getApiErrorMessage(requestError, 'Etkinlik kaydı iptal edilemedi. Lütfen tekrar deneyin.'));
    } finally {
      setActionLoading(false);
    }
  }

  if (loading) return <section className="events-page"><div className="state-card">Etkinlik detayları yükleniyor…</div></section>;
  if (error) return <section className="events-page"><Link className="back-link" to="/events">← Etkinliklere dön</Link><div className="state-card state-card--error" role="alert">{error}</div></section>;
  if (!['ONGOING', 'UPCOMING'].includes(getEventStatus(event))) return <section className="events-page"><Link className="back-link" to="/events">← Etkinliklere dön</Link><div className="state-card"><strong>Bu etkinlik artık aktif programda yer almıyor.</strong><p>Geçmiş etkinlik içerikleri Dijital Arşiv’de gösterilir.</p></div></section>;

  return (
    <section className="events-page events-detail-page">
      <Link className="back-link" to="/events">← Etkinliklere dön</Link>
      <div className="events-detail">
        <article className="events-detail__main">
          <div className="events-detail__visual">{event.coverImageUrl ? <img src={event.coverImageUrl} alt="" /> : <span aria-hidden="true">{new Date(event.startDateTime).getDate()}</span>}<EventStatusBadge event={event} /></div>
          <div className="events-detail__content">
            <p className="events-eyebrow">Etkinlik Detayları</p>
            <h1>{event.title}</h1>
            <p>{event.description || 'Bu etkinlik için açıklama eklenmemiş.'}</p>
            <dl>
              <div><dt>Tarih ve saat</dt><dd>{formatEventDateRange(event)}</dd></div>
              {getEventPlace(event) && <div><dt>Konum</dt><dd>{getEventPlace(event)}</dd></div>}
              {event.organizer && <div><dt>Düzenleyen</dt><dd>{event.organizer}</dd></div>}
              <div><dt>Katılım</dt><dd>{event.requiresApproval ? 'Onay gerektiriyor' : 'Doğrudan kayıt'}</dd></div>
            </dl>
            {event.tags?.length > 0 && <div className="events-card__tags">{event.tags.map((tag) => <span key={tag}>{tag}</span>)}</div>}
          </div>
        </article>
        <aside className="events-detail__aside">
          <h2>Katılım bilgisi</h2>
          <EventCapacity event={event} detailed />
          <EventRegistrationButton event={event} registration={registration} loading={actionLoading} onRegister={register} onCancel={cancel} />
          {event.meetingLink && <a className="button-primary" href={event.meetingLink} target="_blank" rel="noreferrer">Etkinlik bağlantısını aç</a>}
          {feedback && <p className="events-feedback" role="status">{feedback}</p>}
        </aside>
      </div>
    </section>
  );
}

export default EventsPage;
