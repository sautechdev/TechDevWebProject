import { useEffect, useState } from 'react';
import { useAuth } from '../../contexts/AuthContext.jsx';
import { useLocation, useNavigate } from 'react-router-dom';
import { asList } from '../../services/apiClient.js';
import { profileApi } from '../../services/profileApi.js';
import { formatDate } from '../../utils/formatDate.js';
import SkillsSection from '../../components/profile/SkillsSection.jsx';
import ApplicantSkills from '../../components/applications/ApplicantSkills.jsx';
import '../shared-pages.css';

const statusLabels = { PENDING: 'Onay bekliyor', ACTIVE: 'Aktif', REJECTED: 'Reddedildi' };

function UnknownRecord({ record }) {
  const entries = Object.entries(record || {}).filter(([, value]) =>
    ['string', 'number', 'boolean'].includes(typeof value));
  return <dl className="record-list">{entries.map(([key, value]) => <div key={key}><dt>{key}</dt><dd>{String(value)}</dd></div>)}</dl>;
}

function ProfilePage() {
  const location = useLocation();
  const navigate = useNavigate();
  const { logout, updateCurrentUser } = useAuth();
  const [profile, setProfile] = useState(null);
  const [projects, setProjects] = useState([]);
  const [applications, setApplications] = useState([]);
  const [form, setForm] = useState({ fullName: '', currentPassword: '', newPassword: '', confirmation: '' });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [feedback, setFeedback] = useState(() => location.state?.notice
    ? { type: 'success', text: location.state.notice }
    : { type: '', text: '' });

  useEffect(() => {
    let active = true;
    Promise.all([profileApi.getMe(), profileApi.getProjects(), profileApi.getApplications()])
      .then(([me, myProjects, myApplications]) => {
        if (!active) return;
        setProfile(me);
        setForm((current) => ({ ...current, fullName: me.fullName || '' }));
        setProjects(asList(myProjects));
        setApplications(asList(myApplications));
      })
      .catch((error) => active && setFeedback({ type: 'error', text: error.message }))
      .finally(() => active && setLoading(false));
    return () => { active = false; };
  }, []);

  async function handleSubmit(event) {
    event.preventDefault();
    setFeedback({ type: '', text: '' });
    if (!form.fullName.trim()) return setFeedback({ type: 'error', text: 'Ad soyad boş bırakılamaz.' });
    const hasPasswordInput = form.currentPassword || form.newPassword || form.confirmation;
    if (hasPasswordInput && (!form.currentPassword || !form.newPassword || !form.confirmation))
      return setFeedback({ type: 'error', text: 'Şifre değiştirmek için üç şifre alanını da doldurun.' });
    if (hasPasswordInput && form.newPassword !== form.confirmation)
      return setFeedback({ type: 'error', text: 'Yeni şifreler eşleşmiyor.' });
    const body = { fullName: form.fullName.trim() };
    if (hasPasswordInput) Object.assign(body, { currentPassword: form.currentPassword, newPassword: form.newPassword });
    setSaving(true);
    try {
      const updated = await profileApi.updateMe(body);
      const next = updated || { ...profile, fullName: body.fullName };
      setProfile(next);
      updateCurrentUser({ fullName: next.fullName || body.fullName });
      setForm((current) => ({ ...current, currentPassword: '', newPassword: '', confirmation: '' }));
      setFeedback({ type: 'success', text: 'Profiliniz güncellendi.' });
    } catch (error) {
      setFeedback({ type: 'error', text: error.message });
    } finally { setSaving(false); }
  }

  function handleLogout() {
    logout();
    navigate('/login', { replace: true });
  }

  if (loading) return <p className="state-card">Profil bilgileri yükleniyor…</p>;

  return (
    <section className="profile-page page-stack">
      <div className="page-heading"><p className="page-heading__eyebrow">Hesabım</p><h1>Profil</h1><p>Bilgilerinizi, projelerinizi ve başvurularınızı tek yerden yönetin.</p></div>
      {feedback.text && <p className={`feedback feedback--${feedback.type}`} role="status">{feedback.text}</p>}
      <section className="panel"><h2>Profil bilgileri</h2>
        <form className="stack-form" onSubmit={handleSubmit}>
          <div className="form-grid">
            <label>Ad soyad<input value={form.fullName} onChange={(e) => setForm({ ...form, fullName: e.target.value })} /></label>
            <label>E-posta<input value={profile?.email || ''} readOnly /></label>
            <label>Rol<input value={profile?.role || ''} readOnly /></label>
            <label>Hesap oluşturma tarihi<input value={formatDate(profile?.createdAt)} readOnly /></label>
          </div>
          <h3>Şifre değiştir</h3><div className="form-grid form-grid--three">
            <label>Mevcut şifre<input type="password" autoComplete="current-password" value={form.currentPassword} onChange={(e) => setForm({ ...form, currentPassword: e.target.value })} /></label>
            <label>Yeni şifre<input type="password" autoComplete="new-password" value={form.newPassword} onChange={(e) => setForm({ ...form, newPassword: e.target.value })} /></label>
            <label>Yeni şifre tekrar<input type="password" autoComplete="new-password" value={form.confirmation} onChange={(e) => setForm({ ...form, confirmation: e.target.value })} /></label>
          </div><button className="button-primary" disabled={saving}>{saving ? 'Kaydediliyor…' : 'Değişiklikleri kaydet'}</button>
        </form>
      </section>
      <SkillsSection />
      <section className="panel"><h2>Projelerim</h2>{projects.length ? <div className="card-grid">{projects.map((project) => <article className="mini-card" key={project.id}><span className={`badge badge--${project.status?.toLowerCase()}`}>{statusLabels[project.status] || project.status}</span><h3>{project.title}</h3><p>{project.description}</p></article>)}</div> : <p className="state-card">Henüz bir projeniz yok.</p>}</section>
      <section className="panel"><h2>Başvurularım</h2>{applications.length ? <div className="card-grid">{applications.map((application, index) => <article className="mini-card" key={application.id ?? index}><UnknownRecord record={application} /><ApplicantSkills skills={application.skills || application.applicant?.skills || application.user?.skills} /></article>)}</div> : <p className="state-card">Henüz bir başvurunuz yok.</p>}</section>
      <section className="profile-logout" aria-labelledby="profile-logout-title">
        <div><h2 id="profile-logout-title">Oturumu kapat</h2><p>Bu cihazdaki TechDev oturumunuzu güvenli şekilde sonlandırın.</p></div>
        <button className="button-danger" type="button" onClick={handleLogout}>Çıkış Yap</button>
      </section>
    </section>
  );
}

export default ProfilePage;
