import { Link } from 'react-router-dom';
import { formatEventDateRange, getEventPlace } from '../../utils/eventUtils.js';
import EventAdminActions from './EventAdminActions.jsx';
import EventCapacity from './EventCapacity.jsx';
import EventRegistrationButton from './EventRegistrationButton.jsx';
import EventStatusBadge from './EventStatusBadge.jsx';

function EventCard({ event, registration, actionLoading, onRegister, onCancel, canManage, onEdit, onDelete }) {
  const start = new Date(event.startDateTime);
  return (
    <article className="events-card">
      <div className="events-card__visual">
        {event.coverImageUrl ? <img src={event.coverImageUrl} alt="" /> : <div className="events-card__date"><strong>{start.getDate()}</strong><span>{new Intl.DateTimeFormat('tr-TR', { month: 'short' }).format(start)}</span></div>}
        <EventStatusBadge event={event} />
      </div>
      <div className="events-card__content">
        <p className="events-card__time">{formatEventDateRange(event)}</p>
        <h3>{event.title}</h3>
        <p className="events-card__summary">{event.description || 'Bu etkinlik için açıklama eklenmemiş.'}</p>
        <div className="events-card__facts">
          {getEventPlace(event) && <span><b aria-hidden="true">⌖</b>{getEventPlace(event)}</span>}
          <EventCapacity event={event} />
        </div>
        {event.tags?.length > 0 && <div className="events-card__tags">{event.tags.map((tag) => <span key={tag}>{tag}</span>)}</div>}
        <div className="events-card__actions">
          <Link to={`/events/${event.id}`} aria-label={`${event.title} etkinlik detaylarını görüntüle`}>Detayları Gör</Link>
          <EventRegistrationButton event={event} registration={registration} loading={actionLoading} onRegister={onRegister} onCancel={onCancel} />
        </div>
        {canManage && <EventAdminActions event={event} onEdit={onEdit} onDelete={onDelete} />}
      </div>
    </article>
  );
}

export default EventCard;
