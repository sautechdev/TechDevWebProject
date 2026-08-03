import { useEffect, useState } from 'react';
import { addUserSkill, getSkillCatalog, getUserSkills, removeUserSkill } from '../../services/skillsService.js';
import SkillBadge from './SkillBadge.jsx';
import SkillSelector from './SkillSelector.jsx';
import './skills.css';

function SkillsSection() {
  const [skills, setSkills] = useState([]);
  const [catalog, setCatalog] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [feedback, setFeedback] = useState({ type: '', text: '' });

  useEffect(() => {
    let active = true;
    Promise.all([getUserSkills(), getSkillCatalog()])
      .then(([userSkills, skillCatalog]) => {
        if (!active) return;
        setSkills(userSkills);
        setCatalog(skillCatalog);
      })
      .catch((error) => active && setFeedback({ type: 'error', text: error.message }))
      .finally(() => active && setLoading(false));
    return () => { active = false; };
  }, []);

  async function addSkill(skill) {
    const previousSkills = skills;
    setSkills([...skills, skill]);
    setSaving(true);
    setFeedback({ type: '', text: '' });
    try {
      await addUserSkill(skill.id);
      setFeedback({ type: 'success', text: 'Yeteneğiniz profilinize eklendi.' });
      return true;
    } catch (error) {
      setSkills(previousSkills);
      setFeedback({ type: 'error', text: error.message });
      return false;
    } finally {
      setSaving(false);
    }
  }

  async function removeSkill(skill) {
    const previousSkills = skills;
    setSkills(skills.filter((item) => item.id !== skill.id));
    setSaving(true);
    setFeedback({ type: '', text: '' });
    try {
      await removeUserSkill(skill.id);
      setFeedback({ type: 'success', text: 'Yeteneğiniz profilinizden kaldırıldı.' });
    } catch (error) {
      setSkills(previousSkills);
      setFeedback({ type: 'error', text: error.message });
    } finally {
      setSaving(false);
    }
  }

  return <section className="panel skills-section">
    <div><h2>Yetenekler</h2><p>Teknik bilgi ve deneyimlerinizi ekleyerek proje ekiplerinin sizi daha yakından tanımasını sağlayın.</p></div>
    {feedback.text && <p className={`skill-message skill-message--${feedback.type}`} role="status">{feedback.text}</p>}
    {loading ? <p className="skills-empty">Yetenekleriniz yükleniyor…</p> : <>
      {skills.length > 0 ? <div className="skill-badges" aria-label="Eklenen yetenekler">{skills.map((skill) => <SkillBadge key={skill.id} skill={skill} onRemove={saving ? undefined : removeSkill} />)}</div> : <p className="skills-empty">Henüz bir yetenek eklemediniz. Katalogdan bildiğiniz teknolojileri seçerek profilinizi güçlendirebilirsiniz.</p>}
      <SkillSelector skills={skills} catalog={catalog} onAdd={addSkill} disabled={saving} />
    </>}
  </section>;
}

export default SkillsSection;
