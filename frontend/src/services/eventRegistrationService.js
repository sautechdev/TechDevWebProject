import { eventApi } from './eventApi.js';

const STORAGE_PREFIX = 'techdev_event_registrations_';

function storageKey(userId) {
  return `${STORAGE_PREFIX}${userId}`;
}

function readEntries(userId) {
  if (!userId) return {};
  try {
    const parsed = JSON.parse(localStorage.getItem(storageKey(userId)) || '{}');
    return parsed && typeof parsed === 'object' ? parsed : {};
  } catch {
    return {};
  }
}

function writeEntries(userId, entries) {
  localStorage.setItem(storageKey(userId), JSON.stringify(entries));
  return entries;
}

export const eventRegistrationService = {
  getRegistrationMap(userId) {
    return readEntries(userId);
  },

  async register(userId, event) {
    const entries = readEntries(userId);
    const key = String(event.id);
    if (entries[key]) return entries[key];

    const response = await eventApi.register(event.id);
    const registration = { id: response?.id || null, status: response?.status || 'CONFIRMED' };
    writeEntries(userId, { ...entries, [key]: registration });
    return registration;
  },

  async cancel(userId, event) {
    const entries = readEntries(userId);
    const key = String(event.id);
    await eventApi.unregister(event.id);
    const nextEntries = { ...entries };
    delete nextEntries[key];
    writeEntries(userId, nextEntries);
    return nextEntries;
  },

  remember(userId, eventId, registration = { status: 'KNOWN' }) {
    const entries = readEntries(userId);
    return writeEntries(userId, { ...entries, [String(eventId)]: registration });
  },
};
