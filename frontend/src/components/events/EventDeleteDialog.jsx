import { useState } from 'react';
import { getApiErrorMessage } from '../../services/apiClient.js';
import EventDialog from './EventDialog.jsx';

function EventDeleteDialog({ event, onConfirm, onClose }) {
  const [deleting, setDeleting] = useState(false);
  const [error, setError] = useState('');
  const titleId = `event-delete-title-${event.id}`;
  const descriptionId = `${titleId}-description`;

  async function confirmDelete() {
    if (deleting) return;
    setDeleting(true);
    setError('');
    try {
      await onConfirm(event);
    } catch (requestError) {
      setError(requestError?.status === 403
        ? 'Bu işlem için admin yetkisine sahip değilsiniz.'
        : getApiErrorMessage(requestError, 'Etkinlik silinemedi.'));
      setDeleting(false);
    }
  }

  return (
    <EventDialog titleId={titleId} descriptionId={descriptionId} onClose={onClose} closeDisabled={deleting} className="event-dialog--delete">
      <header className="event-dialog__header">
        <div>
          <p className="events-eyebrow">Dikkat</p>
          <h2 id={titleId}>Etkinliği Sil</h2>
        </div>
        <button type="button" className="event-dialog__close" onClick={onClose} disabled={deleting} aria-label="Silme penceresini kapat">×</button>
      </header>
      <p id={descriptionId}>“{event.title}” etkinliğini silmek istediğinizden emin misiniz? Bu işlem geri alınamaz.</p>
      {error && <p className="event-form__submit-error" role="alert">{error}</p>}
      <div className="event-dialog__actions">
        <button type="button" className="button-secondary" onClick={onClose} disabled={deleting}>Vazgeç</button>
        <button type="button" className="button-danger" data-autofocus onClick={confirmDelete} disabled={deleting}>{deleting ? 'Siliniyor…' : 'Etkinliği Sil'}</button>
      </div>
    </EventDialog>
  );
}

export default EventDeleteDialog;
