import SkillBadge from '../profile/SkillBadge.jsx';
import { normalizeSkillNames } from '../profile/skillUtils.js';
import '../profile/skills.css';

function ApplicantSkills({ skills }) {
  const names = normalizeSkillNames(skills);

  return <div className="applicant-skills">
    <strong>Yetenekler</strong>
    {names.length > 0
      ? <div className="skill-badges">{names.map((name) => <SkillBadge key={name.toLocaleLowerCase('tr-TR')} skill={name} />)}</div>
      : <p className="skills-empty">Yetenek bilgisi henüz paylaşılmamış.</p>}
  </div>;
}

export default ApplicantSkills;
