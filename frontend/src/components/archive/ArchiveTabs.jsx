import { useRef } from 'react';
import ArchiveTab from './ArchiveTab.jsx';

function ArchiveTabs({ records, activeId, onSelect, renderActiveArchive }) {
  const tabRefs = useRef([]);

  function handleKeyDown(event, currentIndex) {
    if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault();
      onSelect(records[currentIndex].id);
      return;
    }

    let nextIndex = currentIndex;
    if (event.key === 'ArrowDown' || event.key === 'ArrowRight') nextIndex = (currentIndex + 1) % records.length;
    else if (event.key === 'ArrowUp' || event.key === 'ArrowLeft') nextIndex = (currentIndex - 1 + records.length) % records.length;
    else if (event.key === 'Home') nextIndex = 0;
    else if (event.key === 'End') nextIndex = records.length - 1;
    else return;

    event.preventDefault();
    tabRefs.current[nextIndex]?.focus();
  }

  return (
    <div className="archive-tabs" role="tablist" aria-label="Arşiv kayıtları">
      {records.map((record, index) => {
        const isActive = record.id === activeId;
        const tabOffsets = [4, 24, 44, 14, 34, 54];

        return (
          <div
            className={`archive-stack__item${isActive ? ' archive-stack__item--active' : ''}`}
            key={record.id}
            role="presentation"
            style={{
              '--archive-layer': index + 1,
              '--archive-active-bump': isActive ? 1 : 0,
            }}
          >
            <ArchiveTab
              record={record}
              index={index}
              tabOffset={tabOffsets[index % tabOffsets.length]}
              active={isActive}
              focusable={isActive}
              onSelect={() => onSelect(record.id)}
              onKeyDown={(event) => handleKeyDown(event, index)}
              tabRef={(node) => { tabRefs.current[index] = node; }}
            />
            <div className={`archive-file-body${isActive ? ' archive-file-body--active' : ''}`}>
              {isActive && renderActiveArchive(record)}
            </div>
          </div>
        );
      })}
    </div>
  );
}

export default ArchiveTabs;
