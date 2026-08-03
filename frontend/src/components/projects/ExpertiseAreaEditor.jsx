import ExpertiseAreaRow from './ExpertiseAreaRow.jsx';
import TeamRequirementSummary from './TeamRequirementSummary.jsx';

function ExpertiseAreaEditor({ roles, techFields, loading, error, disabled, onAdd, onChange, onRemove }) {
  const selectedIds = new Set(roles.map((role) => String(role.techFieldId)).filter(Boolean));

  return <fieldset className="project-form-section" disabled={disabled}>
    <legend>Projede Aranan Roller</legend>
    <p className="field-help">Projeniz için ihtiyaç duyduğunuz teknoloji alanlarını ve her alan için kaç ekip arkadaşı aradığınızı belirtin.</p>
    {loading && <p className="state-card">Teknoloji alanları yükleniyor…</p>}
    {error && <p className="feedback feedback--error" role="alert">{error}</p>}
    {!loading && !error && <div className="expertise-editor">
      {roles.map((role, index) => <ExpertiseAreaRow key={role.key} role={role} index={index}
        techFields={techFields} selectedIds={selectedIds} disabled={disabled}
        onChange={onChange} onRemove={onRemove} />)}
      <button className="button-secondary expertise-editor__add" type="button" disabled={disabled || roles.length >= techFields.length} onClick={onAdd}>Rol Ekle</button>
      <TeamRequirementSummary roles={roles} techFields={techFields} />
    </div>}
  </fieldset>;
}

export default ExpertiseAreaEditor;
