import { getRemainingCapacity } from '../../utils/eventUtils.js';

function EventCapacity({ event, detailed = false }) {
  const remaining = getRemainingCapacity(event);
  if (event.capacity === null || event.capacity === undefined) {
    return <p className="event-capacity"><strong>{event.registeredCount || 0}</strong> katılımcı</p>;
  }
  const percentage = Math.min(100, (Number(event.registeredCount || 0) / Number(event.capacity || 1)) * 100);
  return (
    <div className="event-capacity">
      <div><strong>{event.registeredCount || 0} / {event.capacity}</strong><span>{remaining === 0 ? 'Kontenjan doldu' : `${remaining} kişilik yer kaldı`}</span></div>
      {detailed && <span className="event-capacity__bar" aria-label={`${remaining} kişilik yer kaldı`}><i style={{ width: `${percentage}%` }} /></span>}
    </div>
  );
}

export default EventCapacity;
