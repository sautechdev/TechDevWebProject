function ProjectBasicInfoFields({ form, onChange, disabled }) {
  return <fieldset className="project-form-section" disabled={disabled}>
    <legend>Proje Bilgileri</legend>
    <label htmlFor="project-title">Proje Başlığı
      <input id="project-title" required value={form.title} onChange={(event) => onChange('title', event.target.value)} />
    </label>
    <label htmlFor="project-description">Projenizi Ayrıntılı Olarak Açıklayın</label>
    <span className="field-help" id="project-description-help">Projenin amacını, hedef kitlesini, çözmek istediği problemi, kullanılabilecek teknolojileri ve beklenen çıktıları açıklayın.</span>
    <textarea id="project-description" aria-describedby="project-description-help" rows="10" required value={form.description} onChange={(event) => onChange('description', event.target.value)} />
  </fieldset>;
}

export default ProjectBasicInfoFields;
