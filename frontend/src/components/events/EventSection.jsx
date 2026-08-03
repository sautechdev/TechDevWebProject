import EventCard from './EventCard.jsx';

function EventSection({ title, eyebrow, events, emptyText, registrations, actionEventId, onRegister, onCancel, canManage, onEdit, onDelete, featured = false }) {
  return (
    <section className={featured ? 'events-section events-section--featured' : 'events-section'}>
      <header><div><span>{eyebrow}</span><h2>{title}</h2></div><strong>{events.length}</strong></header>
      {events.length === 0 ? <p className="events-section__empty">{emptyText}</p> : (
        <div className="events-grid">
          {events.map((event) => (
            <EventCard
              key={event.id}
              event={event}
              registration={registrations[String(event.id)]}
              actionLoading={String(actionEventId) === String(event.id)}
              onRegister={onRegister}
              onCancel={onCancel}
              canManage={canManage}
              onEdit={onEdit}
              onDelete={onDelete}
            />
          ))}
        </div>
      )}
    </section>
  );
}

export default EventSection;
