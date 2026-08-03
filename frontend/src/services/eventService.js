import { getEventStatus } from '../utils/eventUtils.js';
import { eventApi } from './eventApi.js';

function normalizeEvent(event) {
  const status = getEventStatus(event);
  const full = event.full ?? (event.capacity !== null && event.capacity !== undefined
    && Number(event.registeredCount || 0) >= Number(event.capacity));
  return { ...event, status, full };
}

async function loadStatus(status, keyword) {
  const events = [];
  let page = 0;
  let totalPages = 1;
  do {
    const response = await eventApi.list({ keyword, status, page, size: 50, sort: 'startDateTime,asc' });
    events.push(...(response?.content || []));
    totalPages = Math.max(Number(response?.totalPages || 0), 1);
    page += 1;
  } while (page < totalPages && page < 100);
  return events;
}

export const eventService = {
  async listVisible(keyword = '') {
    const [ongoing, upcoming] = await Promise.all([
      loadStatus('ONGOING', keyword),
      loadStatus('UPCOMING', keyword),
    ]);
    const unique = new Map([...ongoing, ...upcoming].map((event) => [String(event.id), normalizeEvent(event)]));
    return [...unique.values()].filter((event) => ['ONGOING', 'UPCOMING'].includes(getEventStatus(event)));
  },

  async getById(eventId) {
    return normalizeEvent(await eventApi.getById(eventId));
  },

  async create(payload) {
    return normalizeEvent(await eventApi.createEvent(payload));
  },

  async update(eventId, payload) {
    return normalizeEvent(await eventApi.updateEvent(eventId, payload));
  },

  async delete(eventId) {
    await eventApi.deleteEvent(eventId);
  },
};
