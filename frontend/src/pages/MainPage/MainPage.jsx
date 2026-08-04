import './MainPage.css';
import ekipRobot1 from '../../assets/events/ekip-robot-1.jpg';
import robotDemo1 from '../../assets/events/robot-demo-1.jpg';
import elektronikMontaj from '../../assets/events/elektronik-montaj.jpg';
import standGenel from '../../assets/events/stant-genel.jpg';

const galleryPhotos = [
  { src: ekipRobot1, alt: 'TechDev ekibi SAÜ Mezunlar Etkinliği standında robot projesiyle' },
  { src: robotDemo1, alt: 'Robot yarış pisti demosu izleyicilerle birlikte' },
  { src: elektronikMontaj, alt: 'Standda elektronik devre ve robot montajı yapan üye' },
  { src: standGenel, alt: 'SAÜ Mezunlar Etkinliği\'nde TechDev standının genel görünümü' },
];

const steps = [
  {
    title: 'Fikrini Paylaş',
    text: 'Yazılım, yapay zekâ, web, mobil veya siber güvenlik alanındaki fikrini toplulukla paylaş.',
  },
  {
    title: 'Ekibini Kur',
    text: 'Benzer ilgi alanlarına sahip üyelerle bir araya gel ve proje rollerini netleştir.',
  },
  {
    title: 'Projeni Geliştir',
    text: 'Gerçek bir ürün ortaya çıkararak portföyünü güçlendir ve deneyim kazan.',
  },
];

function MainPage() {
  return (
    <section className="main-page">
      <div className="hero-section">
        <div className="hero-section__content">
          
          <p className="page-heading__eyebrow">Sakarya Üniversitesi TechDev</p>
          <h1>Keşfet. Geliştir. Dönüştür.</h1>
          <p>
            TechDev, teknolojiye ilgi duyan öğrencileri bir araya getirerek
            yazılım geliştirme, proje üretme ve ekip çalışması kültürünü
            güçlendirmeyi amaçlayan bir topluluktur.
          </p>
          <div className="hero-section__actions">
            <a href="https://forms.gle/WsSoT92zCTkhgB3a8" target="_blank" rel="noopener noreferrer">Bize Katıl</a>
            <a href="/projects">Projeleri Keşfet</a>
          </div>
        </div>
        <div className="hero-visual" aria-hidden="true">
          <div className="hero-visual__code">
            <span>const community = buildTogether();</span>
            <span>deploy(ideas, teamwork);</span>
            <span>learn.byDoing();</span>
          </div>
          <div className="hero-visual__screen">
            <div className="hero-visual__toolbar">
              <span />
              <span />
              <span />
            </div>
            <div className="hero-visual__terminal">
              <i />
              <i />
              <i />
              <i />
            </div>
          </div>
          <div className="hero-visual__chip">
            <span />
            <span />
            <span />
            <span />
          </div>
          <div className="hero-visual__team">
            <span />
            <span />
            <span />
          </div>
        </div>
      </div>

      <div className="about-section">
        <div>
          <p className="page-heading__eyebrow">TechDev Neden Var?</p>
          <h2>Fikirleri konuşulan değil, geliştirilen projelere dönüştürmek için.</h2>
        </div>
        <p>
          TechDev, teknolojiye ilgi duyan öğrencilerin birlikte öğrenmesi,
          üretmesi ve gerçek yazılım projelerinde deneyim kazanması için
          kurulmuş bir topluluktur. Amacımız, fikirleri yalnızca konuşulan
          değil, geliştirilen ve paylaşılan projelere dönüştürmektir.
        </p>
      </div>

      <div className="steps-section">
        <div>
          <p className="page-heading__eyebrow">Nasıl çalışıyoruz?</p>
          <h2>Üç adımda fikirden ürüne.</h2>
        </div>
        <div className="steps-grid">
          {steps.map((step, index) => (
            <article key={step.title}>
              <span>{String(index + 1).padStart(2, '0')}</span>
              <h3>{step.title}</h3>
              <p>{step.text}</p>
            </article>
          ))}
        </div>
      </div>

      <div className="gallery-section">
        <div className="gallery-section__heading">
          <p className="page-heading__eyebrow">Etkinliklerimizden</p>
          <h2>Standımızda, sahada, ekip hâlinde.</h2>
          <p>SAÜ Mezunlar Etkinliği'nde robot projelerimizi tanıttık, ziyaretçilerle tanıştık. Daha fazla an için Dijital Arşiv'e göz atın.</p>
          <a href="/archive">Tüm anılara göz at →</a>
        </div>
        <div className="gallery-section__grid">
          {galleryPhotos.map((photo) => (
            <img key={photo.src} src={photo.src} alt={photo.alt} loading="lazy" />
          ))}
        </div>
      </div>

      <div className="cta-section" id="join">
        <div>
          <p className="page-heading__eyebrow">Topluluğa katıl</p>
          <h2>Geleceği birlikte inşa etmeye hazır mısınız?</h2>
          <p>TechDev topluluğuna katılın, fikirlerinizi projeye dönüştürün.</p>
        </div>
        <a href="https://forms.gle/WsSoT92zCTkhgB3a8" target="_blank" rel="noopener noreferrer">Bize Katıl</a>
      </div>
    </section>
  );
}

export default MainPage;
