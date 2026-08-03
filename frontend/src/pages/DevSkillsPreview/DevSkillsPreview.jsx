import SkillsSection from '../../components/profile/SkillsSection.jsx';
import ApplicantSkills from '../../components/applications/ApplicantSkills.jsx';
import '../shared-pages.css';

function DevSkillsPreview() {
  return <section className="page-stack">
    <div className="page-heading"><p className="page-heading__eyebrow">Geliştirme önizlemesi</p><h1>Yetenek bileşenleri</h1><p>Bu sayfa yalnızca geliştirme ortamında bileşen davranışlarını doğrulamak için kullanılır.</p></div>
    <SkillsSection />
    <section className="panel"><h2>Başvuran yetenekleri — metin modeli</h2><ApplicantSkills skills={['React', 'JavaScript']} /></section>
    <section className="panel"><h2>Başvuran yetenekleri — nesne modeli</h2><ApplicantSkills skills={[{ id: 1, name: 'Spring Boot' }, { id: 2, name: 'Docker' }]} /></section>
    <section className="panel"><h2>Başvuran yetenekleri — veri yok</h2><ApplicantSkills /></section>
  </section>;
}

export default DevSkillsPreview;
