import { useEffect, useState } from 'react';
import ArchiveCover from './ArchiveCover.jsx';
import ArchiveTabs from './ArchiveTabs.jsx';
import ArchiveViewer from './ArchiveViewer.jsx';

const coverFile = {
  id: 'cover',
  title: 'TechDev Dijital Arşivi',
  date: '2026',
  tabDate: '2026',
  isCover: true,
};

function ArchiveCabinet({ records, initialId }) {
  const safeInitialId = initialId && records.some((record) => record.id === initialId) ? initialId : 'cover';
  const [activeArchiveId, setActiveArchiveId] = useState(safeInitialId);
  const archiveFiles = [...records, coverFile];

  useEffect(() => {
    setActiveArchiveId(safeInitialId);
  }, [safeInitialId]);

  useEffect(() => {
    function handleEscape(event) {
      const isEditable = event.target instanceof Element
        && event.target.closest('input, textarea, select, [contenteditable="true"]');
      if (event.key === 'Escape' && !event.defaultPrevented && !isEditable) {
        setActiveArchiveId('cover');
      }
    }

    document.addEventListener('keydown', handleEscape);
    return () => document.removeEventListener('keydown', handleEscape);
  }, [activeArchiveId]);

  const openArchive = (archiveId) => {
    setActiveArchiveId(archiveId);
  };

  return (
    <div className="archive-cabinet">
      <div className="archive-cabinet__topbar">
        <span>TECHDEV ARŞİV SİSTEMİ</span>
        <span>{String(records.length).padStart(2, '0')} KAYIT</span>
      </div>
      <ArchiveTabs
        records={archiveFiles}
        activeId={activeArchiveId}
        onSelect={openArchive}
        renderActiveArchive={(record) => (
          record.isCover
            ? <ArchiveCover />
            : <ArchiveViewer key={record.id} record={record} />
        )}
      />
      <div className="archive-drawer" aria-hidden="true">
        <span className="archive-drawer__handle" />
      </div>
    </div>
  );
}

export default ArchiveCabinet;
