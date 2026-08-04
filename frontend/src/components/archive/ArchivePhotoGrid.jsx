function ArchivePhotoGrid({ photos }) {
  return (
    <div className="archive-photo-grid" aria-label="Etkinlik fotoğrafları">
      {photos.map((photo) => (
        <figure key={photo.id} className="archive-photo archive-photo--real">
          <img src={photo.url} alt={photo.alt} loading="lazy" />
          {photo.caption && <figcaption>{photo.caption}</figcaption>}
        </figure>
      ))}
    </div>
  );
}

export default ArchivePhotoGrid;
