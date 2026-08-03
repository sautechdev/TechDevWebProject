import { getSkillName } from './skillUtils.js';

function SkillBadge({ skill, onRemove }) {
  const name = getSkillName(skill);
  if (!name) return null;

  return <span className="skill-badge">
    <span>{name}</span>
    {onRemove && <button type="button" onClick={() => onRemove(skill)} aria-label={`${name} yeteneğini kaldır`}>×</button>}
  </span>;
}

export default SkillBadge;
