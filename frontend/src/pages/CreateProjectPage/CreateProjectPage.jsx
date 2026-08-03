import { useEffect, useRef, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import ExpertiseAreaEditor from '../../components/projects/ExpertiseAreaEditor.jsx';
import ProjectBasicInfoFields from '../../components/projects/ProjectBasicInfoFields.jsx';
import '../../components/projects/project-form.css';
import { useAuth } from '../../contexts/AuthContext.jsx';
import { asList, getApiErrorMessage } from '../../services/apiClient.js';
import { expertiseAreaApi } from '../../services/expertiseAreaApi.js';
import { projectApi } from '../../services/projectApi.js';
import { techFieldApi } from '../../services/techFieldApi.js';
import '../shared-pages.css';

const successMessage = 'Proje fikrin ve ekip ihtiyaçların başarıyla gönderildi. Projen admin onayından sonra yayınlanacaktır.';
const partialMessage = 'Proje oluşturuldu ancak bazı ekip ihtiyaçları kaydedilemedi. Projenizi Profil > Projelerim alanından düzenleyebilirsiniz.';

function isGenericBackendMessage(message) {
  return typeof message === 'string'
    && message.toLocaleLowerCase('tr-TR').replaceAll('ş', 's').replaceAll('ı', 'i')
      .includes('beklenmeyen bir hata olustu');
}

function getProjectCreationError(error) {
  const backendMessage = getApiErrorMessage(error, '');
  if (backendMessage && !isGenericBackendMessage(backendMessage)) return backendMessage;
  if (!error?.status) return 'Sunucuya bağlanılamadı. Backend servisinin çalıştığını ve CORS ayarlarını kontrol edin.';
  if (error.status === 401) return 'Oturumunuz geçersiz. Lütfen yeniden giriş yapın.';
  if (error.status === 403) return 'Bu işlem için yetkiniz bulunmuyor veya oturum bilgileriniz geçersiz.';
  if (error.status === 404) return 'Proje oluşturma servisi bulunamadı.';
  if (error.status >= 500) return 'Proje oluşturulurken sunucu tarafında bir hata oluştu.';
  return backendMessage || 'Proje fikri oluşturulurken bir hata oluştu.';
}

function CreateProjectPage() {
  const navigate = useNavigate();
  const { token } = useAuth();
  const nextRoleKey = useRef(2);
  const [form, setForm] = useState({ title: '', description: '' });
  const [roles, setRoles] = useState([{ key: 1, techFieldId: '', requiredCount: 1 }]);
  const [techFields, setTechFields] = useState([]);
  const [techFieldsLoading, setTechFieldsLoading] = useState(true);
  const [techFieldsError, setTechFieldsError] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [stage, setStage] = useState('');
  const [error, setError] = useState('');
  const [partialResult, setPartialResult] = useState(null);

  useEffect(() => {
    let active = true;
    techFieldApi.list()
      .then((response) => active && setTechFields(asList(response)))
      .catch(() => active && setTechFieldsError('Teknoloji alanları yüklenemedi. Lütfen tekrar deneyin.'))
      .finally(() => active && setTechFieldsLoading(false));
    return () => { active = false; };
  }, []);

  function updateForm(field, value) {
    setForm((current) => ({ ...current, [field]: value }));
  }

  function addRole() {
    setRoles((current) => [...current, { key: nextRoleKey.current++, techFieldId: '', requiredCount: 1 }]);
    setError('');
  }

  function updateRole(key, field, value) {
    if (field === 'techFieldId' && value
      && roles.some((role) => role.key !== key && String(role.techFieldId) === String(value))) {
      setError('Aynı teknoloji alanını birden fazla kez ekleyemezsiniz.');
      return;
    }
    setRoles((current) => current.map((role) => role.key === key ? { ...role, [field]: value } : role));
    setError('');
  }

  function removeRole(key) {
    setRoles((current) => current.length === 1
      ? [{ ...current[0], techFieldId: '', requiredCount: 1 }]
      : current.filter((role) => role.key !== key));
    setError('');
  }

  function validate() {
    if (!form.title.trim()) return 'Proje başlığı zorunludur.';
    if (!form.description.trim()) return 'Projenizi ayrıntılı olarak açıklayın.';
    if (!roles.length || roles.some((role) => !role.techFieldId)) return 'En az bir aranan rol eklemelisiniz.';
    const ids = roles.map((role) => String(role.techFieldId));
    if (new Set(ids).size !== ids.length) return 'Aynı teknoloji alanını birden fazla kez ekleyemezsiniz.';
    const hasInvalidCount = roles.some((role) => {
      const count = Number(role.requiredCount);
      return !Number.isInteger(count) || count < 1;
    });
    if (hasInvalidCount) return 'Gereken kişi sayısı en az 1 olmalıdır.';
    return '';
  }

  async function saveExpertiseAreas(projectId, targetRoles) {
    const settled = await Promise.allSettled(targetRoles.map((role) =>
      expertiseAreaApi.create(projectId, role)));
    return targetRoles.reduce((result, role, index) => {
      const requestResult = settled[index];
      if (requestResult.status === 'fulfilled') result.successful.push(role);
      else result.failed.push({ ...role, error: getApiErrorMessage(requestResult.reason, 'Ekip ihtiyacı kaydedilemedi.') });
      return result;
    }, { successful: [], failed: [] });
  }

  function finishSuccessfully() {
    setForm({ title: '', description: '' });
    setRoles([{ key: nextRoleKey.current++, techFieldId: '', requiredCount: 1 }]);
    navigate('/profile?tab=projects', { replace: true, state: { notice: successMessage } });
  }

  async function handleSubmit(event) {
    event.preventDefault();
    if (submitting || partialResult) return;
    setError('');
    const validationMessage = validate();
    if (validationMessage) return setError(validationMessage);
    if (!token) {
      setError('Proje fikri eklemek için giriş yapmalısınız.');
      navigate('/login', { state: { from: { pathname: '/projects/new' } } });
      return;
    }

    const payload = { title: form.title.trim(), description: form.description.trim() };
    setSubmitting(true);
    setStage('Proje oluşturuluyor…');
    try {
      const createdProject = await projectApi.create(payload);
      const projectId = Number(createdProject?.id);
      if (!Number.isInteger(projectId) || projectId < 1) {
        setPartialResult({ projectId: null, successfulCount: 0, failedRoles: roles, missingProjectId: true });
        setError('Proje oluşturuldu ancak response içinde proje ID’si bulunmadığı için ekip ihtiyaçları kaydedilemedi.');
        return;
      }

      setStage('Ekip ihtiyaçları kaydediliyor…');
      const result = await saveExpertiseAreas(projectId, roles);
      if (!result.failed.length) {
        finishSuccessfully();
        return;
      }
      setPartialResult({ projectId, successfulCount: result.successful.length, failedRoles: result.failed, missingProjectId: false });
      setError(partialMessage);
    } catch (requestError) {
      if (import.meta.env.DEV) {
        console.error('Project creation failed', {
          url: requestError?.url,
          method: requestError?.method,
          requestData: payload,
          status: requestError?.status,
          responseData: requestError?.data,
          hasAuthorizationHeader: requestError?.hasAuthorizationHeader,
        });
      }
      setError(getProjectCreationError(requestError));
    } finally {
      setSubmitting(false);
      setStage('');
    }
  }

  async function retryFailedRoles() {
    if (!partialResult?.projectId || !partialResult.failedRoles.length || submitting) return;
    setSubmitting(true);
    setStage('Başarısız ekip ihtiyaçları yeniden kaydediliyor…');
    setError('');
    try {
      const result = await saveExpertiseAreas(partialResult.projectId, partialResult.failedRoles);
      if (!result.failed.length) {
        finishSuccessfully();
        return;
      }
      setPartialResult((current) => ({
        ...current,
        successfulCount: current.successfulCount + result.successful.length,
        failedRoles: result.failed,
      }));
      setError(partialMessage);
    } finally {
      setSubmitting(false);
      setStage('');
    }
  }

  const fieldsById = new Map(techFields.map((field) => [String(field.id), field.name]));
  const formLocked = submitting || Boolean(partialResult);

  return <section className="create-project-page page-stack">
    <div className="page-heading"><p className="page-heading__eyebrow">Yeni proje</p><h1>Proje fikrini paylaş.</h1><p>Projenizi ayrıntıları ve ekip ihtiyaçlarıyla birlikte topluluğa gönderin.</p></div>
    <form className="panel stack-form" onSubmit={handleSubmit} noValidate>
      <ProjectBasicInfoFields form={form} onChange={updateForm} disabled={formLocked} />
      <ExpertiseAreaEditor roles={roles} techFields={techFields} loading={techFieldsLoading}
        error={techFieldsError} disabled={formLocked} onAdd={addRole} onChange={updateRole} onRemove={removeRole} />
      <section className="project-form-section"><h2>Gönderim</h2>
        <p className="submission-note">Projeniz gönderildikten sonra admin incelemesine alınacaktır. Onaylanan projeler Projeler sayfasında yayınlanır.</p>
        {stage && <p className="feedback" role="status">{stage}</p>}
        {error && <p className="feedback feedback--error" role="alert">{error}</p>}
        {partialResult && <div className="partial-result">
          {partialResult.failedRoles.length > 0 && <ul>{partialResult.failedRoles.map((role) =>
            <li key={role.key}>{fieldsById.get(String(role.techFieldId)) || 'Seçili rol'}: {role.error || 'Kaydedilemedi'}</li>)}</ul>}
          {!partialResult.missingProjectId && <button type="button" className="button-primary" disabled={submitting} onClick={retryFailedRoles}>Yalnızca Başarısız Rolleri Tekrar Dene</button>}
          <Link className="button-secondary" to="/profile?tab=projects">Profil &gt; Projelerim</Link>
        </div>}
        {!partialResult && <div className="inline-actions"><button className="button-primary" disabled={submitting || techFieldsLoading || Boolean(techFieldsError)}>{submitting ? stage || 'Gönderiliyor…' : 'Proje Fikrini Gönder'}</button><Link className="button-secondary" to="/projects">Vazgeç</Link></div>}
      </section>
    </form>
  </section>;
}

export default CreateProjectPage;
