import { isEventRegistrationOpen } from '../../utils/eventUtils.js';

function EventRegistrationButton({ event, registration, loading, onRegister, onCancel }) {
  const registrationOpen = isEventRegistrationOpen(event);
  if (registration) {
    return (
      <div className="event-registration-actions">
        <span className="event-registration-state">{registration.status === 'PENDING' ? 'Onay Bekliyor' : 'Kayıt Yapıldı'}</span>
        <button type="button" className="button-secondary" onClick={() => onCancel(event)} disabled={loading} aria-label={`${event.title} etkinlik kaydını iptal et`}>
          {loading ? 'İşleniyor…' : 'Kaydı İptal Et'}
        </button>
      </div>
    );
  }
  return (
    <button type="button" onClick={() => onRegister(event)} disabled={!registrationOpen || loading} aria-label={`${event.title} etkinliğine kayıt ol`}>
      {loading ? 'İşleniyor…' : (registrationOpen ? 'Kayıt Ol' : 'Kontenjan Doldu')}
    </button>
  );
}

export default EventRegistrationButton;
