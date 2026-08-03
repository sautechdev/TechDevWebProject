function ArchivePhotoGrid({ photos }) {
  return (
    <div className="archive-photo-grid" aria-label="Etkinlik fotoğrafları">
      {photos.map((photo, index) => (
        <div
          key={photo.id}
          className={`archive-photo archive-photo--${photo.tone}`}
          role="img"
          aria-label={photo.alt}
        >
          <span className="archive-photo__number" aria-hidden="true">{String(index + 1).padStart(2, '0')}</span>
          <span className="archive-photo__scene" aria-hidden="true"><i /><i /><i /></span>
          <strong>{photo.label}</strong>
        </div>
      ))}
    </div>
  );
}

export default ArchivePhotoGrid;
