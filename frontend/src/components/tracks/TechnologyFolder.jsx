function TechnologyFolder({ field, onOpen }) {
  const hasRemoteIcon = field.icon?.startsWith('http');

  return (
    <button
      className="technology-folder"
      type="button"
      aria-label={`${field.name} klasörünü aç`}
      onClick={() => onOpen(field.id)}
    >
      <span className="technology-folder__art" aria-hidden="true">
        <span className="technology-folder__back" />
        <span className="technology-folder__paper">{hasRemoteIcon ? <img src={field.icon} alt="" /> : (field.icon || '</>')}</span>
        <span className="technology-folder__front" />
      </span>
      <span className="technology-folder__name">{field.name}</span>
      <span className="technology-folder__description">{field.description || 'Bu alandaki teknolojileri görüntüleyin.'}</span>
      <span className="technology-folder__action">Klasörü aç <b aria-hidden="true">↗</b></span>
    </button>
  );
}

export default TechnologyFolder;
