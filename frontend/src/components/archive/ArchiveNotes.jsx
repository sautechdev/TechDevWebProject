function ArchiveNotes({ notes }) {
  return (
    <aside className="archive-notes">
      <div className="archive-notes__heading">
        <span aria-hidden="true">{'//'}</span>
        <h3>Dosya notları</h3>
      </div>
      <ol>
        {notes.map((note) => <li key={note}>{note}</li>)}
      </ol>
    </aside>
  );
}

export default ArchiveNotes;
