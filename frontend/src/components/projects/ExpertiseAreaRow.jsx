function ExpertiseAreaRow({ role, index, techFields, selectedIds, onChange, onRemove, disabled }) {
  return <div className="expertise-row">
    <label htmlFor={`role-tech-${role.key}`}>Teknoloji alanı
      <select id={`role-tech-${role.key}`} value={role.techFieldId} disabled={disabled}
        onChange={(event) => onChange(role.key, 'techFieldId', event.target.value)}>
        <option value="">Alan seçin</option>
        {techFields.map((field) => <option key={field.id} value={field.id}
          disabled={selectedIds.has(String(field.id)) && String(field.id) !== String(role.techFieldId)}>
          {field.icon ? `${field.icon} ` : ''}{field.name}
        </option>)}
      </select>
    </label>
    <label htmlFor={`role-count-${role.key}`}>Gereken kişi sayısı
      <input id={`role-count-${role.key}`} type="number" inputMode="numeric" min="1" step="1"
        value={role.requiredCount} disabled={disabled}
        onChange={(event) => onChange(role.key, 'requiredCount', event.target.value)} />
    </label>
    <button className="button-danger expertise-row__remove" type="button" disabled={disabled}
      aria-label={`${index + 1}. rol satırını kaldır`} onClick={() => onRemove(role.key)}>Satırı Kaldır</button>
  </div>;
}

export default ExpertiseAreaRow;
