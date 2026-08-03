function EventAdminActions({ event, onEdit, onDelete }) {
  return (
    <div className="event-admin-actions" aria-label={`${event.title} etkinliği yönetimi`}>
      <span>Yönetim</span>
      <div>
        <button
          type="button"
          className="button-secondary"
          onClick={() => onEdit(event)}
          aria-label={`${event.title} etkinliğini düzenle`}
        >
          Düzenle
        </button>
        <button
          type="button"
          className="button-danger"
          onClick={() => onDelete(event)}
          aria-label={`${event.title} etkinliğini sil`}
        >
          Sil
        </button>
      </div>
    </div>
  );
}

export default EventAdminActions;
