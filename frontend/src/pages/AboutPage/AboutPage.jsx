import { teamMembers } from '../../data/teamMembers.js';
import { communityFeatures } from '../../data/communityFeatures.js';
import TeamCarousel from '../../components/TeamCarousel/TeamCarousel.jsx';
import '../shared-pages.css';

function AboutPage() {
  return <section className="about-page page-stack">
    <div className="about-hero"><div className="page-heading"><p className="page-heading__eyebrow">TechDev hakkında</p><h1>Sakarya Üniversitesi'nin yazılım yarışmalarına yön veren teknoloji topluluğu.</h1><p>SAÜ Teknoloji ve Yazılım Geliştirme Topluluğu (TechDev), yazılım yarışmalarına katılan takımları tek çatı altında toplar, akrandan akrana eğitim ile öğrencilerin birbirinden öğrenmesini sağlar ve öğrencileri gerçek proje ekipleri içinde bir araya getirir.</p></div><div className="photo-placeholder" role="img" aria-label="TechDev üyelerinin birlikte çalıştığı ekip fotoğrafı için ayrılmış alan"><span>Topluluk fotoğrafı</span><small>Görsel daha sonra eklenecek</small></div></div>
    <section><div className="section-heading"><p className="page-heading__eyebrow">Toplulukta neler var?</p><h2>Öğrenmekten üretmeye uzanan ortak alanlar.</h2></div><div className="community-feature-grid">{communityFeatures.map((feature) => <article className="community-feature-card" key={feature.title}><span>{feature.title.slice(0, 2)}</span><h3>{feature.title}</h3><p>{feature.text}</p></article>)}</div></section>
    <div className="about-story"><div className="photo-placeholder photo-placeholder--wide" role="img" aria-label="TechDev etkinlik ve atölye fotoğrafı için ayrılmış alan"><span>Etkinlik fotoğrafı</span><small>Görsel daha sonra eklenecek</small></div><div><p className="page-heading__eyebrow">Fikirden çalışan ürüne</p><h2>Öğrencileri proje takımlarına, takımları ise yarışmalara hazırlıyoruz.</h2><p>Bu web sitesi üzerinden üyeler ilgi duydukları proje ekiplerine başvurur, uzmanlık alanlarına göre takımlara katılır ve ileride düzenlenecek online yarışmalara katılarak profillerini zenginleştirme fırsatı bulur.</p></div></div>
    <section><div className="section-heading"><p className="page-heading__eyebrow">2026-2027 Yönetim Ekibi</p><h2>Bizimle Tanışın</h2><p>TechDev'i birlikte geliştiren, üreten ve topluluğumuza değer katmak için çalışan ekibimizle tanışın.</p></div><TeamCarousel members={teamMembers} /></section>
  </section>;
}

export default AboutPage;
