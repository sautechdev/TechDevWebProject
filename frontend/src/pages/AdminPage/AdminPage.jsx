import { useEffect, useMemo, useState } from 'react';
import { NavLink } from 'react-router-dom';
import { adminApi } from '../../services/adminApi.js';
import { archiveApi } from '../../services/archiveApi.js';
import { asList, API_BASE_URL } from '../../services/apiClient.js';
import '../shared-pages.css';

const emptyUser = { fullName: '', email: '', password: '', role: 'USER' };

function AdminShell({ children, title, description }) {
  return <section className="admin-page page-stack"><div className="page-heading"><p className="page-heading__eyebrow">Yönetim merkezi</p><h1>{title}</h1><p>{description}</p></div><nav className="admin-nav" aria-label="Admin bölümleri"><NavLink end to="/admin">Özet</NavLink><NavLink to="/admin/projects/pending">Onay kuyruğu</NavLink><NavLink end to="/admin/projects">Tüm projeler</NavLink><NavLink to="/admin/users">Kullanıcılar</NavLink><NavLink to="/admin/skills">Yetenekler</NavLink><NavLink to="/admin/archive">Dijital Arşiv</NavLink></nav>{children}</section>;
}

function Feedback({ value }) {
  return value.text ? <p className={`feedback feedback--${value.type}`} role="status">{value.text}</p> : null;
}

export function AdminDashboard() {
  const [counts, setCounts] = useState(null);
  const [error, setError] = useState('');
  useEffect(() => { let active = true; Promise.all([adminApi.getUsers(), adminApi.getProjects(), adminApi.getPendingProjects()]).then(([u,p,q]) => active && setCounts({ users: asList(u).length, projects: asList(p).length, pending: asList(q).length })).catch((e) => active && setError(e.message)); return () => { active = false; }; }, []);
  return <AdminShell title="Admin paneli" description="Kullanıcıları ve proje moderasyonunu tek yerden yönetin.">{error ? <p className="feedback feedback--error">{error}</p> : !counts ? <p className="state-card">Özet yükleniyor…</p> : <div className="stats-grid"><article><strong>{counts.users}</strong><span>Toplam kullanıcı</span></article><article><strong>{counts.projects}</strong><span>Toplam proje</span></article><article><strong>{counts.pending}</strong><span>Onay bekleyen</span></article></div>}</AdminShell>;
}

export function PendingProjects() {
  const [projects, setProjects] = useState([]); const [loading, setLoading] = useState(true); const [processing, setProcessing] = useState(null); const [feedback, setFeedback] = useState({ type: '', text: '' });
  async function load() { setLoading(true); try { setProjects(asList(await adminApi.getPendingProjects())); } catch (e) { setFeedback({ type: 'error', text: e.message }); } finally { setLoading(false); } }
  useEffect(() => { load(); }, []);
  async function moderate(project, action) { setProcessing(project.id); setFeedback({ type: '', text: '' }); try { await (action === 'approve' ? adminApi.approveProject(project.id) : adminApi.rejectProject(project.id)); setProjects((items) => items.filter((item) => item.id !== project.id)); setFeedback({ type: 'success', text: `${project.title} ${action === 'approve' ? 'onaylandı' : 'reddedildi'}.` }); } catch (e) { setFeedback({ type: 'error', text: e.message }); } finally { setProcessing(null); } }
  return <AdminShell title="Proje onay kuyruğu" description="Bekleyen projeleri inceleyip yayına alın veya reddedin."><Feedback value={feedback} />{loading ? <p className="state-card">Projeler yükleniyor…</p> : projects.length ? <div className="card-grid">{projects.map((p) => <article className="mini-card" key={p.id}><span className="badge badge--pending">Onay bekliyor</span><h2>{p.title}</h2><p>{p.description}</p>{p.coverImageUrl && <a href={p.coverImageUrl} target="_blank" rel="noreferrer">Kapak görselini incele</a>}<div className="inline-actions"><button disabled={processing === p.id} onClick={() => moderate(p, 'approve')}>Onayla</button><button className="button-danger" disabled={processing === p.id} onClick={() => moderate(p, 'reject')}>Reddet</button></div></article>)}</div> : <p className="state-card">Onay bekleyen proje yok.</p>}</AdminShell>;
}

export function AdminProjects() {
  const [projects, setProjects] = useState([]); const [status, setStatus] = useState(''); const [editing, setEditing] = useState(null); const [loading, setLoading] = useState(true); const [feedback, setFeedback] = useState({ type: '', text: '' });
  useEffect(() => { let active = true; setLoading(true); adminApi.getProjects(status).then((r) => active && setProjects(asList(r))).catch((e) => active && setFeedback({ type: 'error', text: e.message })).finally(() => active && setLoading(false)); return () => { active = false; }; }, [status]);
  async function save(event) { event.preventDefault(); try { const body = { title: editing.title, description: editing.description, coverImageUrl: editing.coverImageUrl || '', status: editing.status }; const result = await adminApi.updateProject(editing.id, body); setProjects((items) => items.map((item) => item.id === editing.id ? (result || { ...item, ...body }) : item)); setEditing(null); setFeedback({ type: 'success', text: 'Proje güncellendi.' }); } catch (e) { setFeedback({ type: 'error', text: e.message }); } }
  async function remove(project) { if (!window.confirm(`“${project.title}” projesini kalıcı olarak silmek istediğinize emin misiniz?`)) return; try { await adminApi.deleteProject(project.id); setProjects((items) => items.filter((item) => item.id !== project.id)); setFeedback({ type: 'success', text: 'Proje silindi.' }); } catch (e) { setFeedback({ type: 'error', text: e.message }); } }
  return <AdminShell title="Tüm projeler" description="Projeleri duruma göre filtreleyin, düzenleyin veya silin."><Feedback value={feedback} /><label className="filter-control">Durum<select value={status} onChange={(e) => setStatus(e.target.value)}><option value="">Tümü</option><option>PENDING</option><option>ACTIVE</option><option>REJECTED</option></select></label>{editing && <form className="panel stack-form" onSubmit={save}><h2>Projeyi düzenle</h2><div className="form-grid"><label>Başlık<input required value={editing.title || ''} onChange={(e) => setEditing({ ...editing, title: e.target.value })} /></label><label>Durum<select value={editing.status || 'PENDING'} onChange={(e) => setEditing({ ...editing, status: e.target.value })}><option>PENDING</option><option>ACTIVE</option><option>REJECTED</option></select></label><label>Kapak görseli URL<input value={editing.coverImageUrl || ''} onChange={(e) => setEditing({ ...editing, coverImageUrl: e.target.value })} /></label></div><label>Açıklama<textarea required rows="5" value={editing.description || ''} onChange={(e) => setEditing({ ...editing, description: e.target.value })} /></label><div className="inline-actions"><button>Kaydet</button><button type="button" className="button-secondary" onClick={() => setEditing(null)}>Vazgeç</button></div></form>}{loading ? <p className="state-card">Projeler yükleniyor…</p> : projects.length ? <div className="card-grid">{projects.map((p) => <article className="mini-card" key={p.id}><span className={`badge badge--${p.status?.toLowerCase()}`}>{p.status}</span><h3>{p.title}</h3><p>{p.description}</p><div className="inline-actions"><button onClick={() => setEditing({ ...p })}>Düzenle</button><button className="button-danger" onClick={() => remove(p)}>Sil</button></div></article>)}</div> : <p className="state-card">Bu filtrede proje bulunamadı.</p>}</AdminShell>;
}

export function AdminSkills() {
  const [skills, setSkills] = useState([]); const [query, setQuery] = useState(''); const [name, setName] = useState(''); const [loading, setLoading] = useState(true); const [saving, setSaving] = useState(false); const [feedback, setFeedback] = useState({ type: '', text: '' });
  useEffect(() => { let active = true; adminApi.getSkills().then((r) => active && setSkills(asList(r))).catch((e) => active && setFeedback({ type: 'error', text: e.message })).finally(() => active && setLoading(false)); return () => { active = false; }; }, []);
  const filtered = useMemo(() => { const q = query.trim().toLocaleLowerCase('tr-TR'); return skills.filter((s) => (s.name || '').toLocaleLowerCase('tr-TR').includes(q)); }, [query, skills]);
  async function submit(event) { event.preventDefault(); const trimmed = name.trim(); if (!trimmed) return; setSaving(true); setFeedback({ type: '', text: '' }); try { const result = await adminApi.createSkill(trimmed); setSkills((items) => [...items, result || { id: Date.now(), name: trimmed }]); setName(''); setFeedback({ type: 'success', text: `"${trimmed}" kataloğa eklendi.` }); } catch (e) { setFeedback({ type: 'error', text: e.message }); } finally { setSaving(false); } }
  async function remove(skill) { if (!window.confirm(`"${skill.name}" yeteneğini kataloğdan kalıcı olarak silmek istediğinize emin misiniz? (Bu yeteneği profiline eklemiş kullanıcılardan da kaldırılabilir.)`)) return; try { await adminApi.deleteSkill(skill.id); setSkills((items) => items.filter((s) => s.id !== skill.id)); setFeedback({ type: 'success', text: 'Yetenek kataloğdan silindi.' }); } catch (e) { setFeedback({ type: 'error', text: e.message }); } }
  return <AdminShell title="Yetenek kataloğu" description="Kullanıcıların profillerine ekleyebileceği yetenek listesini (React, Docker, Python vb.) buradan yönetin."><Feedback value={feedback} /><form className="panel stack-form" onSubmit={submit}><h2>Yeni yetenek ekle</h2><div className="form-grid"><label>Yetenek adı<input required placeholder="Örn. React, Docker, Python" value={name} onChange={(e) => setName(e.target.value)} /></label></div><div className="inline-actions"><button disabled={saving || !name.trim()}>{saving ? 'Ekleniyor…' : 'Ekle'}</button></div></form><label className="filter-control">Kataloğda ara<input placeholder="Yetenek adı" value={query} onChange={(e) => setQuery(e.target.value)} /></label>{loading ? <p className="state-card">Kataloğ yükleniyor…</p> : filtered.length ? <div className="table-wrap"><table><thead><tr><th>Yetenek</th><th>İşlemler</th></tr></thead><tbody>{filtered.map((s) => <tr key={s.id}><td>{s.name}</td><td><button className="button-danger" onClick={() => remove(s)}>Sil</button></td></tr>)}</tbody></table></div> : <p className="state-card">{skills.length ? 'Aramanızla eşleşen yetenek yok.' : 'Kataloğda henüz hiç yetenek yok, yukarıdan ilk yeteneği ekleyin.'}</p>}</AdminShell>;
}

export function AdminArchive() {
  const emptyEvent = { title: '', description: '', eventDate: '' };
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [form, setForm] = useState(emptyEvent);
  const [feedback, setFeedback] = useState({ type: '', text: '' });
  const [expandedId, setExpandedId] = useState(null);
  const [uploading, setUploading] = useState(false);
  const [captionDrafts, setCaptionDrafts] = useState({});

  async function load() {
    setLoading(true);
    try {
      const result = await archiveApi.list({ size: 100 });
      const list = asList(result);
      // Liste endpoint'i fotoğrafları içermiyor, her etkinlik için ayrıca çekiyoruz.
      const withItems = await Promise.all(list.map(async (archiveEvent) => {
        try {
          const items = await archiveApi.getItems(archiveEvent.id);
          return { ...archiveEvent, items: asList(items) };
        } catch {
          return { ...archiveEvent, items: [] };
        }
      }));
      setEvents(withItems);
    } catch (e) {
      setFeedback({ type: 'error', text: e.message });
    } finally {
      setLoading(false);
    }
  }
  useEffect(() => { load(); }, []);

  async function createEvent(event) {
    event.preventDefault();
    if (!form.title.trim() || !form.eventDate) return;
    setSaving(true);
    setFeedback({ type: '', text: '' });
    try {
      const created = await archiveApi.createEvent(form);
      setEvents((items) => [created, ...items]);
      setForm(emptyEvent);
      setFeedback({ type: 'success', text: `"${form.title}" arşive eklendi. Şimdi fotoğraf yükleyebilirsiniz.` });
      if (created?.id) setExpandedId(created.id);
    } catch (e) {
      setFeedback({ type: 'error', text: e.message });
    } finally {
      setSaving(false);
    }
  }

  async function removeEvent(archiveEvent) {
    if (!window.confirm(`"${archiveEvent.title}" etkinliğini ve tüm fotoğraflarını kalıcı olarak silmek istediğinize emin misiniz?`)) return;
    try {
      await archiveApi.deleteEvent(archiveEvent.id);
      setEvents((items) => items.filter((item) => item.id !== archiveEvent.id));
      setFeedback({ type: 'success', text: 'Etkinlik arşivden silindi.' });
    } catch (e) {
      setFeedback({ type: 'error', text: e.message });
    }
  }

  async function uploadPhoto(archiveEvent, fileList) {
    const files = Array.from(fileList || []);
    if (!files.length) return;
    setUploading(true);
    setFeedback({ type: '', text: '' });
    try {
      const caption = captionDrafts[archiveEvent.id] || '';
      const uploaded = [];
      for (const file of files) {
        uploaded.push(await archiveApi.uploadItem(archiveEvent.id, file, 'PHOTO', caption));
      }
      setEvents((items) => items.map((item) => item.id === archiveEvent.id
        ? { ...item, items: [...(item.items || []), ...uploaded] }
        : item));
      setCaptionDrafts((drafts) => ({ ...drafts, [archiveEvent.id]: '' }));
      setFeedback({ type: 'success', text: `${uploaded.length} fotoğraf yüklendi.` });
    } catch (e) {
      setFeedback({ type: 'error', text: e.message });
    } finally {
      setUploading(false);
    }
  }

  async function removePhoto(archiveEvent, item) {
    if (!window.confirm('Bu fotoğrafı silmek istediğinize emin misiniz?')) return;
    try {
      await archiveApi.deleteItem(item.id);
      setEvents((items) => items.map((eventItem) => eventItem.id === archiveEvent.id
        ? { ...eventItem, items: (eventItem.items || []).filter((photo) => photo.id !== item.id) }
        : eventItem));
    } catch (e) {
      setFeedback({ type: 'error', text: e.message });
    }
  }

  return <AdminShell title="Dijital arşiv yönetimi" description="Geçmiş etkinlikleri kaydedin, fotoğraflarını yükleyin. Kullanıcılar bunları 'Dijital Arşiv' sayfasında görür.">
    <Feedback value={feedback} />
    <form className="panel stack-form" onSubmit={createEvent}>
      <h2>Yeni arşiv kaydı</h2>
      <div className="form-grid">
        <label>Başlık<input required placeholder="Örn. SAÜ Mezunlar Etkinliği" value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} /></label>
        <label>Tarih<input required type="date" value={form.eventDate} onChange={(e) => setForm({ ...form, eventDate: e.target.value })} /></label>
      </div>
      <label>Açıklama<textarea rows="3" placeholder="Etkinlik hakkında kısa bir not (opsiyonel)" value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} /></label>
      <div className="inline-actions"><button disabled={saving || !form.title.trim() || !form.eventDate}>{saving ? 'Kaydediliyor…' : 'Arşive Ekle'}</button></div>
    </form>

    {loading ? <p className="state-card">Arşiv yükleniyor…</p> : events.length ? <div className="card-grid">
      {events.map((archiveEvent) => {
        const isOpen = expandedId === archiveEvent.id;
        const photos = (archiveEvent.items || []).filter((item) => item.type === 'PHOTO');
        return <article className="mini-card" key={archiveEvent.id}>
          <h3>{archiveEvent.title}</h3>
          <p>{archiveEvent.eventDate} · {photos.length} fotoğraf</p>
          <div className="inline-actions">
            <button type="button" onClick={() => setExpandedId(isOpen ? null : archiveEvent.id)}>{isOpen ? 'Kapat' : 'Fotoğrafları Yönet'}</button>
            <button className="button-danger" type="button" onClick={() => removeEvent(archiveEvent)}>Sil</button>
          </div>
          {isOpen && <div className="archive-admin-panel">
            {photos.length > 0 && <div className="archive-admin-thumbs">
              {photos.map((item) => <div className="archive-admin-thumb" key={item.id}>
                <img src={item.fileUrl?.startsWith('http') ? item.fileUrl : `${API_BASE_URL}${item.fileUrl}`} alt={item.caption || archiveEvent.title} />
                <button type="button" className="button-danger" onClick={() => removePhoto(archiveEvent, item)}>Sil</button>
              </div>)}
            </div>}
            <label>Fotoğraf açıklaması (opsiyonel)<input placeholder="Örn. Stant açılışı" value={captionDrafts[archiveEvent.id] || ''} onChange={(e) => setCaptionDrafts({ ...captionDrafts, [archiveEvent.id]: e.target.value })} /></label>
            <label>Fotoğraf yükle (birden fazla seçebilirsiniz)<input type="file" accept="image/*" multiple disabled={uploading} onChange={(e) => uploadPhoto(archiveEvent, e.target.files)} /></label>
            {uploading && <p className="state-card">Yükleniyor…</p>}
          </div>}
        </article>;
      })}
    </div> : <p className="state-card">Arşivde henüz kayıt yok, yukarıdan ilk etkinliği ekleyin.</p>}
  </AdminShell>;
}

export function AdminUsers() {
  const [users, setUsers] = useState([]); const [query, setQuery] = useState(''); const [form, setForm] = useState(emptyUser); const [editingId, setEditingId] = useState(null); const [loading, setLoading] = useState(true); const [saving, setSaving] = useState(false); const [feedback, setFeedback] = useState({ type: '', text: '' });
  useEffect(() => { let active = true; adminApi.getUsers().then((r) => active && setUsers(asList(r))).catch((e) => active && setFeedback({ type: 'error', text: e.message })).finally(() => active && setLoading(false)); return () => { active = false; }; }, []);
  const filtered = useMemo(() => { const q = query.toLowerCase(); return users.filter((u) => `${u.fullName} ${u.email}`.toLowerCase().includes(q)); }, [query, users]);
  function edit(user) { setEditingId(user.id); setForm({ fullName: user.fullName || '', email: user.email || '', password: '', role: user.role || 'USER' }); }
  function reset() { setEditingId(null); setForm(emptyUser); }
  async function submit(event) { event.preventDefault(); setSaving(true); try { if (editingId) { const body = { fullName: form.fullName, email: form.email, role: form.role }; const result = await adminApi.updateUser(editingId, body); setUsers((items) => items.map((u) => u.id === editingId ? (result || { ...u, ...body }) : u)); } else { const result = await adminApi.createUser(form); if (result) setUsers((items) => [...items, result]); } setFeedback({ type: 'success', text: editingId ? 'Kullanıcı güncellendi.' : 'Kullanıcı oluşturuldu.' }); reset(); } catch (e) { setFeedback({ type: 'error', text: e.message }); } finally { setSaving(false); } }
  async function remove(user) { if (!window.confirm(`“${user.fullName || user.email}” kullanıcısını silmek istediğinize emin misiniz?`)) return; try { await adminApi.deleteUser(user.id); setUsers((items) => items.filter((u) => u.id !== user.id)); setFeedback({ type: 'success', text: 'Kullanıcı silindi.' }); } catch (e) { setFeedback({ type: 'error', text: e.message }); } }
  return <AdminShell title="Kullanıcı yönetimi" description="Hesap oluşturun, rolleri yönetin ve kullanıcıları filtreleyin."><Feedback value={feedback} /><form className="panel stack-form" onSubmit={submit}><h2>{editingId ? 'Kullanıcıyı düzenle' : 'Yeni kullanıcı'}</h2><div className="form-grid"><label>Ad soyad<input required value={form.fullName} onChange={(e) => setForm({ ...form, fullName: e.target.value })} /></label><label>E-posta<input required type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} /></label>{!editingId && <label>Şifre<input required type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} /></label>}<label>Rol<select value={form.role} onChange={(e) => setForm({ ...form, role: e.target.value })}><option>USER</option><option>ADMIN</option></select></label></div><div className="inline-actions"><button disabled={saving}>{saving ? 'Kaydediliyor…' : 'Kaydet'}</button>{editingId && <button type="button" className="button-secondary" onClick={reset}>Vazgeç</button>}</div></form><label className="filter-control">Kullanıcı ara<input placeholder="Ad veya e-posta" value={query} onChange={(e) => setQuery(e.target.value)} /></label>{loading ? <p className="state-card">Kullanıcılar yükleniyor…</p> : filtered.length ? <div className="table-wrap"><table><thead><tr><th>Ad soyad</th><th>E-posta</th><th>Rol</th><th>İşlemler</th></tr></thead><tbody>{filtered.map((u) => <tr key={u.id}><td>{u.fullName}</td><td>{u.email}</td><td><span className="badge">{u.role}</span></td><td><div className="inline-actions"><button onClick={() => edit(u)}>Düzenle</button><button className="button-danger" onClick={() => remove(u)}>Sil</button></div></td></tr>)}</tbody></table></div> : <p className="state-card">Kullanıcı bulunamadı.</p>}</AdminShell>;
}
