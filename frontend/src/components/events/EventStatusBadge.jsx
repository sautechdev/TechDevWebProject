import { eventStatusLabels, getEventStatus } from '../../utils/eventUtils.js';

function EventStatusBadge({ event }) {
  const status = getEventStatus(event);
  return (
    <span className={`event-status event-status--${status.toLowerCase()}`}>
      {status === 'ONGOING' && <i aria-hidden="true" />}
      {eventStatusLabels[status] || status}
    </span>
  );
}

export default EventStatusBadge;
