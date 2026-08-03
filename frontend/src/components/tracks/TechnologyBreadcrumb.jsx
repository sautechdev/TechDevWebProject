function TechnologyBreadcrumb({ fieldName, technologyName, onHome, onField }) {
  return (
    <nav className="technology-breadcrumb" aria-label="Teknoloji konumu">
      <span aria-hidden="true">~/</span>
      <button type="button" onClick={onHome}>Teknoloji Alanları</button>
      {fieldName && (
        <>
          <i aria-hidden="true">/</i>
          <button type="button" onClick={onField}>{fieldName}</button>
        </>
      )}
      {technologyName && (
        <>
          <i aria-hidden="true">/</i>
          <strong>{technologyName}</strong>
        </>
      )}
    </nav>
  );
}

export default TechnologyBreadcrumb;
