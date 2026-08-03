import { Link } from 'react-router-dom';
import './ProjectCard.css';

function getOwnerName(project) {
  const owner = project.members?.find((member) => member.role === 'OWNER');
  return project.ownerName || owner?.user?.fullName || owner?.user?.email || 'TechDev ekibi';
}

function isRecruiting(project) {
  if (project.recruitingLabel) {
    return project.recruitingLabel.toLowerCase().includes('acik');
  }

  return project.expertiseAreas?.some((area) => area.filledCount < area.requiredCount);
}

function formatDate(value) {
  if (!value) return 'Tarih yok';

  return new Intl.DateTimeFormat('tr-TR', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
  }).format(new Date(value));
}

function ProjectCard({ project, onApply }) {
  const expertiseAreas = Array.isArray(project.expertiseAreas) ? project.expertiseAreas : [];
  const legacyRoles = project.roles ?? [];
  const recruiting = isRecruiting(project);
  const firstOpenArea = expertiseAreas.find(
    (area) => Number(area.filledCount || 0) < Number(area.requiredCount || 0),
  );
  const canApply = Boolean(firstOpenArea || project.recruitingLabel);
  const totalRequired = expertiseAreas.reduce(
    (total, area) => total + (Number(area.requiredCount) || 0), 0,
  );

  return (
    <article className="project-card">
      <div className="project-card__body">
        <div className="project-card__topline">
          <span>{project.status}</span>
          <span className={recruiting ? 'project-card__status is-open' : 'project-card__status'}>
            {project.recruitingLabel || (recruiting ? 'Üye alımı açık' : 'Kadro tamam')}
          </span>
          <span>{formatDate(project.createdAt)}</span>
        </div>
        <h2>{project.title}</h2>
        <p className="project-card__description">{project.description || 'Bu proje için henüz açıklama eklenmemiş.'}</p>

        <div className="project-card__meta">
          <span>Proje sahibi</span>
          <strong>{getOwnerName(project)}</strong>
        </div>

        <div className="project-card__requirements">
          <h3>Aranan Roller</h3>
          {expertiseAreas.length ? <ul>{expertiseAreas.map((area) => <li key={area.id || area.techField?.id}>
            <span>{area.techField?.name || area.note || 'Teknoloji alanı'}</span>
            <strong>{Number(area.requiredCount) || 0} kişi</strong>
          </li>)}</ul> : legacyRoles.length ? <ul>{legacyRoles.map((role) => <li key={role}><span>{role}</span></li>)}</ul>
            : <p>Henüz ekip ihtiyacı paylaşılmamış.</p>}
          <strong className="project-card__total">Toplam İhtiyaç: {totalRequired} kişi</strong>
        </div>

        <details className="project-card__details">
          <summary>Ayrıntılı Açıklamayı Gör</summary>
          <p>{project.description || 'Bu proje için henüz açıklama eklenmemiş.'}</p>
        </details>

        <div className="project-card__actions">
          <Link to={`/projects/${project.id}/chat`}>Proje Sohbeti</Link>
          <button
            disabled={!canApply}
            type="button"
            onClick={() => canApply && onApply(project, firstOpenArea)}
          >
            {recruiting ? 'Projeye Katıl' : 'Başvuru Kapalı'}
          </button>
        </div>
      </div>
    </article>
  );
}

export default ProjectCard;
