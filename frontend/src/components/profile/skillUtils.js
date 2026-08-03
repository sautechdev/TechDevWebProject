export function getSkillName(skill) {
  if (typeof skill === 'string') return skill.trim();
  if (skill && typeof skill.name === 'string') return skill.name.trim();
  return '';
}

export function normalizeSkillNames(skills) {
  if (!Array.isArray(skills)) return [];
  return skills.map(getSkillName).filter(Boolean);
}
