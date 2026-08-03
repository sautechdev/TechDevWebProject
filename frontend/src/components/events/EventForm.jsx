import { useState } from 'react';
import { getApiErrorMessage } from '../../services/apiClient.js';
import EventDialog from './EventDialog.jsx';

const PLATFORMS = [
  ['ZOOM', 'Zoom'],
  ['GOOGLE_MEET', 'Google Meet'],
  ['DISCORD', 'Discord'],
  ['YOUTUBE_LIVE', 'YouTube Live'],
  ['OTHER', 'Diğer'],
];

function toLocalInputValue(value) {
  if (!value) return '';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return '';
  const pad = (part) => String(part).padStart(2, '0');
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

function initialValues(event) {
  return {
    title: event?.title || '',
    description: event?.description || '',
    platform: event?.platform || 'ZOOM',
    meetingLink: event?.meetingLink || '',
    coverImageUrl: event?.coverImageUrl || '',
    startDateTime: toLocalInputValue(event?.startDateTime),
    endDateTime: toLocalInputValue(event?.endDateTime),
    capacity: event?.capacity ?? '',
    requiresApproval: Boolean(event?.requiresApproval),
  };
}

function validate(values) {
  const errors = {};
  const title = values.title.trim();
  const description = values.description.trim();
  const meetingLink = values.meetingLink.trim();
  const start = new Date(values.startDateTime);
  const end = new Date(values.endDateTime);

  if (!title) errors.title = 'Etkinlik adı boş bırakılamaz.';
  else if (title.length > 200) errors.title = 'Etkinlik adı 200 karakterden uzun olamaz.';
  if (description.length > 2000) errors.description = 'Açıklama 2000 karakterden uzun olamaz.';
  if (!values.platform) errors.platform = 'Platform seçilmelidir.';
  if (!meetingLink) errors.meetingLink = 'Etkinlik bağlantısı boş bırakılamaz.';
  if (!values.startDateTime || Number.isNaN(start.getTime())) errors.startDateTime = 'Başlangıç tarihi zorunludur.';
  else if (start <= new Date()) errors.startDateTime = 'Başlangıç tarihi gelecekte olmalıdır.';
  if (!values.endDateTime || Number.isNaN(end.getTime())) errors.endDateTime = 'Bitiş tarihi zorunludur.';
  else if (!errors.startDateTime && end < start) errors.endDateTime = 'Bitiş tarihi başlangıç tarihinden önce olamaz.';
  if (values.capacity !== '' && (!Number.isInteger(Number(values.capacity)) || Number(values.capacity) < 1)) {
    errors.capacity = 'Kontenjan en az 1 olan bir tam sayı olmalıdır.';
  }
  return errors;
}

function EventForm({ mode, event, onSubmit, onClose }) {
  const [values, setValues] = useState(() => initialValues(event));
  const [errors, setErrors] = useState({});
  const [submitError, setSubmitError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const isEdit = mode === 'edit';
  const titleId = `event-form-title-${isEdit ? event?.id : 'new'}`;
  const descriptionId = `${titleId}-description`;

  function changeValue(changeEvent) {
    const { name, value, type, checked } = changeEvent.target;
    setValues((current) => ({ ...current, [name]: type === 'checkbox' ? checked : value }));
    setErrors((current) => ({ ...current, [name]: '' }));
  }

  async function submit(submitEvent) {
    submitEvent.preventDefault();
    if (submitting) return;
    const nextErrors = validate(values);
    setErrors(nextErrors);
    setSubmitError('');
    if (Object.keys(nextErrors).length > 0) return;

    const payload = {
      title: values.title.trim(),
      description: values.description.trim(),
      platform: values.platform,
      meetingLink: values.meetingLink.trim(),
      coverImageUrl: values.coverImageUrl.trim() || null,
      startDateTime: values.startDateTime,
      endDateTime: values.endDateTime,
      capacity: values.capacity === '' ? null : Number(values.capacity),
      requiresApproval: values.requiresApproval,
    };

    setSubmitting(true);
    try {
      await onSubmit(payload);
    } catch (requestError) {
      setSubmitError(requestError?.status === 403
        ? 'Bu işlem için admin yetkisine sahip değilsiniz.'
        : getApiErrorMessage(requestError, 'Etkinlik işlemi tamamlanamadı.'));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <EventDialog titleId={titleId} descriptionId={descriptionId} onClose={onClose} closeDisabled={submitting} className="event-dialog--form">
      <header className="event-dialog__header">
        <div>
          <p className="events-eyebrow">Yönetim</p>
          <h2 id={titleId}>{isEdit ? 'Etkinliği Düzenle' : 'Etkinlik Ekle'}</h2>
          <p id={descriptionId}>{isEdit ? 'Etkinlik bilgilerini güncelleyin.' : 'Yeni etkinliğin program ve katılım bilgilerini girin.'}</p>
        </div>
        <button type="button" className="event-dialog__close" onClick={onClose} disabled={submitting} aria-label="Formu kapat">×</button>
      </header>
      <form className="event-form" onSubmit={submit} noValidate>
        <label className="event-form__wide">
          <span>Etkinlik adı</span>
          <input data-autofocus name="title" value={values.title} onChange={changeValue} maxLength="200" aria-invalid={Boolean(errors.title)} aria-describedby={errors.title ? 'event-title-error' : undefined} />
          {errors.title && <small id="event-title-error" className="event-form__error">{errors.title}</small>}
        </label>
        <label className="event-form__wide">
          <span>Açıklama</span>
          <textarea name="description" value={values.description} onChange={changeValue} maxLength="2000" rows="5" aria-invalid={Boolean(errors.description)} />
          {errors.description && <small className="event-form__error">{errors.description}</small>}
        </label>
        <label>
          <span>Platform</span>
          <select name="platform" value={values.platform} onChange={changeValue} aria-invalid={Boolean(errors.platform)}>
            {PLATFORMS.map(([value, label]) => <option key={value} value={value}>{label}</option>)}
          </select>
          {errors.platform && <small className="event-form__error">{errors.platform}</small>}
        </label>
        <label>
          <span>Kontenjan <em>(isteğe bağlı)</em></span>
          <input name="capacity" type="number" min="1" step="1" value={values.capacity} onChange={changeValue} aria-invalid={Boolean(errors.capacity)} />
          {errors.capacity && <small className="event-form__error">{errors.capacity}</small>}
        </label>
        <label className="event-form__wide">
          <span>Etkinlik bağlantısı</span>
          <input name="meetingLink" type="url" value={values.meetingLink} onChange={changeValue} placeholder="https://" aria-invalid={Boolean(errors.meetingLink)} />
          {isEdit && !event?.meetingLink && <small className="event-form__hint">Backend mevcut bağlantıyı bu yanıtta paylaşmadı; güncellemek için bağlantıyı yeniden girin.</small>}
          {errors.meetingLink && <small className="event-form__error">{errors.meetingLink}</small>}
        </label>
        <label className="event-form__wide">
          <span>Kapak görseli URL’si <em>(isteğe bağlı)</em></span>
          <input name="coverImageUrl" type="url" value={values.coverImageUrl} onChange={changeValue} placeholder="https://" />
        </label>
        <label>
          <span>Başlangıç tarihi ve saati</span>
          <input name="startDateTime" type="datetime-local" value={values.startDateTime} onChange={changeValue} aria-invalid={Boolean(errors.startDateTime)} />
          {isEdit && event?.startDateTime && new Date(event.startDateTime) <= new Date() && <small className="event-form__hint">Backend güncellemede de gelecekte bir başlangıç tarihi zorunlu tutuyor.</small>}
          {errors.startDateTime && <small className="event-form__error">{errors.startDateTime}</small>}
        </label>
        <label>
          <span>Bitiş tarihi ve saati</span>
          <input name="endDateTime" type="datetime-local" min={values.startDateTime || undefined} value={values.endDateTime} onChange={changeValue} aria-invalid={Boolean(errors.endDateTime)} />
          {errors.endDateTime && <small className="event-form__error">{errors.endDateTime}</small>}
        </label>
        <label className="event-form__checkbox event-form__wide">
          <input name="requiresApproval" type="checkbox" checked={values.requiresApproval} onChange={changeValue} />
          <span>Kayıtlar admin onayı gerektirsin</span>
        </label>
        {submitError && <p className="event-form__submit-error event-form__wide" role="alert">{submitError}</p>}
        <div className="event-dialog__actions event-form__wide">
          <button type="button" className="button-secondary" onClick={onClose} disabled={submitting}>Vazgeç</button>
          <button type="submit" disabled={submitting}>{submitting ? 'Kaydediliyor…' : (isEdit ? 'Güncelle' : 'Kaydet')}</button>
        </div>
      </form>
    </EventDialog>
  );
}

export default EventForm;
