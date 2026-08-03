import { Link } from 'react-router-dom';
import TechnologyBreadcrumb from './TechnologyBreadcrumb.jsx';
import TechnologyIcon from './TechnologyIcon.jsx';

function parseResources(value) {
  if (!value) return [];
  try {
    const parsed = JSON.parse(value);
    return Array.isArray(parsed) ? parsed : [];
  } catch {
    return [];
  }
}

function ResourceList({ title, items, emptyText }) {
  return (
    <section className="technology-resource-block">
      <h3>{title}</h3>
      {items.length === 0 && <p>{emptyText}</p>}
      <div className="technology-resource-list">
        {items.map((item, index) => (
          <a key={item.id || item.url || index} href={item.url} target="_blank" rel="noreferrer">
            <strong>{item.title || item.fullName || item.name}</strong>
            {item.description && <span>{item.description}</span>}
            {item.stars !== undefined && <small>★ {item.stars.toLocaleString('tr-TR')}</small>}
          </a>
        ))}
      </div>
    </section>
  );
}

function TechnologyWindow({
  title,
  stacks,
  selectedStack,
  content,
  loadingStacks,
  loadingContent,
  stackError,
  contentError,
  emptyText,
  isAuthenticated,
  onClose,
  onChooseStack,
  onBreadcrumbHome,
  onBreadcrumbField,
}) {
  const articles = parseResources(content?.devtoArticles);
  const repositories = parseResources(content?.githubRepos);

  return (
    <section className="technology-window" aria-label={`${title} teknoloji klasörü`}>
      <header className="technology-window__bar">
        <span className="technology-window__lights" aria-hidden="true"><i /><i /><i /></span>
        <strong>{title}</strong>
        <button type="button" aria-label="Teknoloji klasörünü kapat" onClick={onClose}>×</button>
      </header>

      <TechnologyBreadcrumb
        fieldName={title}
        technologyName={selectedStack?.name}
        onHome={onBreadcrumbHome}
        onField={onBreadcrumbField}
      />

      {!isAuthenticated ? (
        <div className="technology-window__locked">
          <span aria-hidden="true">⌘</span>
          <h2>Klasör ayrıntıları üyeler için açık.</h2>
          <p>Teknoloji listelerini ve öğrenme kaynaklarını görüntülemek için hesabınızla giriş yapın.</p>
          <Link className="button-primary" to="/login" state={{ from: { pathname: '/tracks' } }}>Giriş yap</Link>
        </div>
      ) : (
        <div className="technology-window__body">
          <nav className="technology-menu" aria-label={`${title} teknolojileri`}>
            <div className="technology-menu__heading"><span aria-hidden="true">$_</span><strong>Teknolojiler</strong></div>
            {loadingStacks && <p className="technology-inline-state">Teknolojiler yükleniyor…</p>}
            {stackError && <p className="feedback feedback--error" role="alert">{stackError}</p>}
            {!loadingStacks && !stackError && stacks.length === 0 && <p className="technology-inline-state">{emptyText}</p>}
            {stacks.map((stack) => (
              <button
                className={selectedStack?.id === stack.id ? 'technology-menu-item is-active' : 'technology-menu-item'}
                key={stack.id}
                type="button"
                aria-label={`${stack.name} teknolojisinin detaylarını görüntüle`}
                onClick={() => onChooseStack(stack)}
              >
                <TechnologyIcon technology={stack} />
                <span><strong>{stack.name}</strong><small>{stack.fieldName}</small></span>
                <b aria-hidden="true">›</b>
              </button>
            ))}
          </nav>

          <article className="technology-detail">
            {!selectedStack && (
              <div className="technology-detail__placeholder">
                <span aria-hidden="true">_</span>
                <h2>Bir teknoloji seçin</h2>
                <p>Açıklama, topluluk notları ve ilgili kaynaklar burada görüntülenecek.</p>
              </div>
            )}
            {selectedStack && (
              <>
                <header><span>{selectedStack.fieldName}</span><h2>{selectedStack.name}</h2></header>
                {loadingContent && <p className="technology-inline-state">İçerik hazırlanıyor…</p>}
                {contentError && <p className="feedback feedback--error" role="alert">{contentError}</p>}
                {content && (
                  <div className="technology-detail__body">
                    <section><h3>Açıklama</h3><p>{content.wikipediaSummary || 'Henüz içerik eklenmemiş.'}</p></section>
                    {content.customNotes && <section><h3>Topluluk notları</h3><p>{content.customNotes}</p></section>}
                    {content.relatedCourses && <section><h3>İlgili dersler</h3><p>{content.relatedCourses}</p></section>}
                    {content.relatedProjects && <section><h3>İlgili projeler</h3><p>{content.relatedProjects}</p></section>}
                    <ResourceList title="Öne çıkan makaleler" items={articles} emptyText="Bu teknoloji için makale bulunmuyor." />
                    <ResourceList title="Açık kaynak depoları" items={repositories} emptyText="Bu teknoloji için depo bulunmuyor." />
                  </div>
                )}
              </>
            )}
          </article>
        </div>
      )}
    </section>
  );
}

export default TechnologyWindow;
