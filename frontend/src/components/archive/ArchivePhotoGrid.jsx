import { optimizeCloudinaryUrl } from '../../services/archiveApi.js';

function ArchivePhotoGrid({ photos }) {
  return (
    <div className="archive-photo-grid" aria-label="Etkinlik fotoğrafları">
      {photos.map((photo) => (
        <figure key={photo.id} className="archive-photo archive-photo--real">
          <img src={optimizeCloudinaryUrl(photo.url, 700)} alt={photo.alt} loading="lazy" />
          {photo.caption && <figcaption>{photo.caption}</figcaption>}
        </figure>
      ))}
    </div>
  );
}

export default ArchivePhotoGrid;
