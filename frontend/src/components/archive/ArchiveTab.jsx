function ArchiveTab({ record, index, tabOffset, active, focusable, onSelect, onKeyDown, tabRef }) {
  const formattedDate = record.isCover
    ? record.tabDate
    : new Intl.DateTimeFormat('tr-TR', {
      day: 'numeric',
      month: 'long',
      year: 'numeric',
    }).format(new Date(record.date));
  const tabDate = record.tabDate || new Intl.DateTimeFormat('tr-TR').format(new Date(record.date));
  const accessibleLabel = record.isCover
    ? 'TechDev Dijital Arşiv kapağını aç'
    : `${record.title}, ${formattedDate} arşiv dosyasını aç`;

  return (
    <button
      ref={tabRef}
      id={`archive-tab-${record.id}`}
      className={`archive-tab${active ? ' archive-tab--active' : ''}`}
      type="button"
      role="tab"
      aria-label={accessibleLabel}
      aria-selected={active}
      aria-controls={active ? `archive-panel-${record.id}` : undefined}
      tabIndex={focusable ? 0 : -1}
      onClick={onSelect}
      onKeyDown={onKeyDown}
      style={{
        '--archive-tab-index': index,
        '--archive-tab-offset': tabOffset,
      }}
    >
      <span className="archive-tab__copy">
        <strong>{record.title}</strong>
        <time dateTime={record.date}>{tabDate}</time>
      </span>
    </button>
  );
}

export default ArchiveTab;
