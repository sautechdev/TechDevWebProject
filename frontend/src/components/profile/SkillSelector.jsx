import { useMemo, useState } from 'react';
import { getSkillName, normalizeSkillNames } from './skillUtils.js';

function SkillSelector({ skills, catalog, onAdd, disabled = false }) {
  const [value, setValue] = useState('');
  const [validationMessage, setValidationMessage] = useState('');
  const existingNames = useMemo(() => normalizeSkillNames(skills), [skills]);
  const availableSkills = useMemo(() => catalog.filter((skill) => {
    const name = getSkillName(skill);
    return name && !existingNames.some((current) => current.toLocaleLowerCase('tr-TR') === name.toLocaleLowerCase('tr-TR'));
  }), [catalog, existingNames]);
  const filteredOptions = useMemo(() => {
    const query = value.trim().toLocaleLowerCase('tr-TR');
    return availableSkills
      .filter((skill) => !query || getSkillName(skill).toLocaleLowerCase('tr-TR').includes(query))
      .slice(0, 8);
  }, [availableSkills, value]);

  async function addSkill(candidate) {
    const skill = candidate || availableSkills.find((item) =>
      getSkillName(item).toLocaleLowerCase('tr-TR') === value.trim().toLocaleLowerCase('tr-TR'));
    setValidationMessage('');
    if (!value.trim() && !candidate) return setValidationMessage('Eklemek istediğiniz yeteneği seçin.');
    if (!skill) return setValidationMessage('Yalnızca yetenek kataloğunda bulunan seçenekleri ekleyebilirsiniz.');
    const saved = await onAdd(skill);
    if (saved !== false) setValue('');
  }

  function handleKeyDown(event) {
    if (event.key === 'Enter') {
      event.preventDefault();
      addSkill();
    }
  }

  return <div className="skill-selector">
    <label htmlFor="skill-input">Yetenek kataloğunda ara</label>
    <div className="skill-selector__entry">
      <input id="skill-input" value={value} disabled={disabled || availableSkills.length === 0}
        placeholder={availableSkills.length ? 'Örn. React veya Docker' : 'Eklenebilecek başka yetenek yok'}
        onChange={(event) => setValue(event.target.value)} onKeyDown={handleKeyDown} />
      <button type="button" disabled={disabled || !value.trim()} onClick={() => addSkill()}>Ekle</button>
    </div>
    {validationMessage && <p className="skill-message skill-message--error" role="alert">{validationMessage}</p>}
    {filteredOptions.length > 0 && <div className="skill-suggestions" aria-label="Hazır yetenek seçenekleri">
      {filteredOptions.map((skill) => <button type="button" disabled={disabled} key={skill.id} onClick={() => addSkill(skill)}>{getSkillName(skill)}</button>)}
    </div>}
  </div>;
}

export default SkillSelector;
