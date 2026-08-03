import ArchiveNotes from './ArchiveNotes.jsx';
import ArchivePhotoGrid from './ArchivePhotoGrid.jsx';

function ArchiveViewer({ record }) {
  const formattedDate = new Intl.DateTimeFormat('tr-TR', { day: '2-digit', month: 'long', year: 'numeric' }).format(new Date(record.date));

  return (
    <article
      id={`archive-panel-${record.id}`}
      className="archive-viewer"
      role="tabpanel"
      aria-labelledby={`archive-tab-${record.id}`}
      tabIndex="0"
    >
      <header className="archive-viewer__header">
        <div>
          <p>TECHDEV / DİJİTAL HAFIZA</p>
          <h2>{record.title}</h2>
          <time dateTime={record.date}>{formattedDate}</time>
        </div>
        <div className="archive-viewer__seal" aria-label={record.meta.collection}>
          <span>ARŞİV</span>
          <strong>{record.meta.collection.split(' ')[1]}</strong>
        </div>
      </header>

      <p className="archive-viewer__summary">{record.summary}</p>

      <dl className="archive-meta">
        <div><dt>Konum</dt><dd>{record.meta.location}</dd></div>
        <div><dt>Katılım</dt><dd>{record.meta.participants}</dd></div>
        <div><dt>Koleksiyon</dt><dd>{record.meta.collection}</dd></div>
      </dl>

      <ArchivePhotoGrid photos={record.photos} />
      <ArchiveNotes notes={record.notes} />
    </article>
  );
}

export default ArchiveViewer;
