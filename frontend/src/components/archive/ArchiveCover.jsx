import techDevLogo from '../../assets/TechDevseffaflogo.png';

function ArchiveCover() {
  return (
    <article
      id="archive-panel-cover"
      className="archive-cover"
      role="tabpanel"
      aria-labelledby="archive-tab-cover"
      tabIndex="0"
    >
      <div className="archive-cover__identity">
        <div className="archive-cover__logo">
          <img src={techDevLogo} alt="SAÜ TechDev" />
        </div>
        <p>TOPLULUK HAFIZASI / 2026</p>
      </div>

      <div className="archive-cover__content">
        <span className="archive-cover__kicker">Koleksiyon 001</span>
        <h2 id="archive-cover-title">TechDev<br /><em>Dijital Arşivi</em></h2>
        <p>Etkinliklerimizi, anılarımızı, notlarımızı ve topluluk yolculuğumuzu keşfedin.</p>
      </div>

      <div className="archive-cover__footer">
        <dl>
          <div><dt>Arşiv No</dt><dd>TD-001</dd></div>
          <div><dt>Dönem</dt><dd>2026</dd></div>
          <div><dt>Durum</dt><dd>Aktif Koleksiyon</dd></div>
        </dl>
        <p>Bir etkinlik dosyasını açmak için sekmesini seçin.</p>
      </div>
    </article>
  );
}

export default ArchiveCover;
