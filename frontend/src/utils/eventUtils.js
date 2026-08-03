export const eventStatusLabels = {
  UPCOMING: 'Yaklaşan',
  ONGOING: 'Devam Ediyor',
};

export const eventPlatformLabels = {
  ZOOM: 'Zoom',
  GOOGLE_MEET: 'Google Meet',
  DISCORD: 'Discord',
  YOUTUBE_LIVE: 'YouTube Live',
  OTHER: 'Çevrim içi',
};

export function getEventStatus(event, now = new Date()) {
  if (['UPCOMING', 'ONGOING', 'COMPLETED', 'CANCELLED'].includes(event.status)) return event.status;
  const start = new Date(event.startDateTime);
  const end = new Date(event.endDateTime);
  if (now < start) return 'UPCOMING';
  if (now <= end) return 'ONGOING';
  return 'COMPLETED';
}

export function formatEventDateRange(event) {
  const start = new Date(event.startDateTime);
  const end = new Date(event.endDateTime);
  const dateFormatter = new Intl.DateTimeFormat('tr-TR', { day: 'numeric', month: 'long', year: 'numeric' });
  const timeFormatter = new Intl.DateTimeFormat('tr-TR', { hour: '2-digit', minute: '2-digit', hour12: false });
  const sameDay = start.toDateString() === end.toDateString();
  if (sameDay) return `${dateFormatter.format(start)}, ${timeFormatter.format(start)}–${timeFormatter.format(end)}`;
  return `${dateFormatter.format(start)}, ${timeFormatter.format(start)} – ${dateFormatter.format(end)}, ${timeFormatter.format(end)}`;
}

export function getEventPlace(event) {
  return event.location || eventPlatformLabels[event.platform] || event.platform || null;
}

export function getRemainingCapacity(event) {
  if (event.capacity === null || event.capacity === undefined) return null;
  return Math.max(Number(event.capacity) - Number(event.registeredCount || 0), 0);
}

export function isEventRegistrationOpen(event) {
  const status = getEventStatus(event);
  return ['UPCOMING', 'ONGOING'].includes(status) && !event.full && getRemainingCapacity(event) !== 0;
}
