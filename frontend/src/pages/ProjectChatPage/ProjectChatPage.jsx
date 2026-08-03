import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import ProjectChatWindow from '../../components/chat/ProjectChatWindow.jsx';
import { useAuth } from '../../contexts/AuthContext.jsx';
import { getApiErrorMessage } from '../../services/apiClient.js';
import { projectApi } from '../../services/projectApi.js';

const allowedRoles = new Set(['OWNER', 'MEMBER']);

function ProjectChatPage() {
  const { projectId } = useParams();
  const { currentUser } = useAuth();
  const [project, setProject] = useState(null);
  const [role, setRole] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let active = true;
    setLoading(true);
    setError('');

    Promise.all([projectApi.get(projectId), projectApi.getMyRole(projectId)])
      .then(([projectResult, roleResult]) => {
        if (!active) return;
        setProject(projectResult);
        setRole(roleResult);
      })
      .catch((requestError) => {
        if (!active) return;
        if (requestError?.status === 403) setError('Bu proje sohbetine erişim yetkiniz bulunmuyor.');
        else if (requestError?.status === 404) setError('Proje bulunamadı.');
        else setError(getApiErrorMessage(requestError, 'Sohbet bilgileri yüklenemedi. Lütfen tekrar deneyin.'));
      })
      .finally(() => active && setLoading(false));

    return () => { active = false; };
  }, [projectId]);

  if (loading) return <section className="chat-page"><div className="chat-access-state" role="status">Sohbet erişimi kontrol ediliyor…</div></section>;

  if (error) return <section className="chat-page"><div className="chat-access-state" role="alert"><h1>Proje Sohbeti</h1><p>{error}</p><Link to="/projects">← Projelere dön</Link></div></section>;

  if (!allowedRoles.has(role)) return <section className="chat-page"><div className="chat-access-state"><h1>Proje Sohbeti</h1><p>Bu proje sohbetine yalnızca proje üyeleri erişebilir.</p><Link to="/projects">← Projelere dön</Link></div></section>;

  return <section className="chat-page">
    <ProjectChatWindow currentUserId={currentUser?.userId} projectId={projectId} projectTitle={project?.title} role={role} />
  </section>;
}

export default ProjectChatPage;
