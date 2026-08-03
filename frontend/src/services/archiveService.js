import archiveMockData from '../data/archiveMockData.js';

// Temporary frontend archive data.
// Replace with backend integration when archive content endpoints are ready.
export async function getArchiveRecords() {
  return archiveMockData;
}

export async function getArchiveRecord(archiveId) {
  return archiveMockData.find((record) => record.id === archiveId) || null;
}
