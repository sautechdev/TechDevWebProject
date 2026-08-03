import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext.jsx';
import ProjectCard from '../../components/ProjectCard/ProjectCard.jsx';
import { projectApi } from '../../services/projectApi.js';
import './ProjectPage.css';

function ProjectPage() {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const [projects, setProjects] = useState([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const [notice, setNotice] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    let ignore = false;

    async function loadProjects() {
      setLoading(true);
      setError('');
      try {
        const response = await projectApi.list({ status: 'ACTIVE' });
        const source = Array.isArray(response?.content) ? response.content : Array.isArray(response) ? response : [];
        const projectsWithAreas = await Promise.all(source.map(async (project) => {
          if (Array.isArray(project.expertiseAreas)) return project;
          try {
            const expertiseAreas = await projectApi.getExpertiseAreas(project.id);
            return { ...project, expertiseAreas: Array.isArray(expertiseAreas) ? expertiseAreas : [] };
          } catch {
            return { ...project, expertiseAreas: [] };
          }
        }));
        if (!ignore) {
          setProjects(projectsWithAreas);
        }
      } catch (requestError) {
        if (!ignore) {
          setProjects([]);
          setError(requestError.message);
        }
      } finally {
        if (!ignore) {
          setLoading(false);
        }
      }
    }

    loadProjects();

    return () => {
      ignore = true;
    };
  }, []);

  const filteredProjects = useMemo(() => {
    const query = search.trim().toLowerCase();
    return projects.filter((project) => {
      const roles = (project.roles ?? project.expertiseAreas
        ?.map((area) => area.techField?.name || area.note || '') ?? [])
        .join(' ')
        .toLowerCase();
      const technologies = (project.technologies ?? []).join(' ').toLowerCase();
      const matchesSearch =
        !query ||
        project.title?.toLowerCase().includes(query) ||
        project.description?.toLowerCase().includes(query) ||
        roles?.includes(query) ||
        technologies.includes(query);
      return matchesSearch;
    });
  }, [projects, search]);

  async function handleApply(project, area) {
    setNotice('');
    setError('');

    if (!area?.id) {
      setError(`${project.title} için başvuru yapılabilecek bir rol bulunamadı.`);
      return;
    }

    try {
      await projectApi.apply(
        area.id,
        `${project.title} projesine katılmak istiyorum. TechDev üzerinden başvurdum.`,
      );
      setNotice(`${project.title} için başvurun alındı.`);
    } catch (err) {
      setError(
        `${project.title} başvurusu gönderilemedi. Giriş yapman gerekebilir veya daha önce başvurmuş olabilirsin. (${err.message})`,
      );
    }
  }

  function handleCreateProject() {
    if (isAuthenticated) {
      navigate('/projects/new');
      return;
    }
    navigate('/login', { state: { from: { pathname: '/projects/new' } } });
  }

  return (
    <section className="project-page">
      <div className="projects-hero">
        <div className="page-heading">
          <p className="page-heading__eyebrow">Topluluk Projeleri</p>
          <h1>Fikirlerin ekiple buluştuğu proje alanı.</h1>
          <p>
            TechDev üyelerinin geliştirdiği yazılım projelerini keşfedin, ekip
            kurun veya ilginizi çeken projelere katılın.
          </p>
        </div>
        <button className="projects-hero__create" type="button" onClick={handleCreateProject}>Proje Fikri Ekle</button>
      </div>

      <div className="project-filters">
        <label>
          Proje ara
          <input
            placeholder="Ad, açıklama veya teknoloji"
            value={search}
            onChange={(event) => setSearch(event.target.value)}
          />
        </label>
      </div>

      {notice && <p className="project-notice">{notice}</p>}
      {error && <p className="project-notice project-notice--error">{error}</p>}

      <div className="project-grid">
        {loading ? (
          <p className="project-empty">Projeler yükleniyor…</p>
        ) : filteredProjects.length ? (
          filteredProjects.map((project) => (
            <ProjectCard key={project.id} project={project} onApply={handleApply} />
          ))
        ) : (
          <p className="project-empty">Bu aramaya uygun proje bulunamadı.</p>
        )}
      </div>
    </section>
  );
}

export default ProjectPage;
