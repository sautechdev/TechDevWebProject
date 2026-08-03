import { useEffect, useMemo, useState } from 'react';
import TechnologyFolderGrid from '../../components/tracks/TechnologyFolderGrid.jsx';
import TechnologyWindow from '../../components/tracks/TechnologyWindow.jsx';
import { useAuth } from '../../contexts/AuthContext.jsx';
import { getApiErrorMessage } from '../../services/apiClient.js';
import { techFieldApi } from '../../services/techFieldApi.js';
import './TracksPage.css';

function TracksPage() {
  const { isAuthenticated } = useAuth();
  const [fields, setFields] = useState([]);
  const [activeFieldId, setActiveFieldId] = useState(null);
  const [searchMode, setSearchMode] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const [submittedSearch, setSubmittedSearch] = useState('');
  const [stacks, setStacks] = useState([]);
  const [selectedStack, setSelectedStack] = useState(null);
  const [content, setContent] = useState(null);
  const [loadingFields, setLoadingFields] = useState(true);
  const [loadingStacks, setLoadingStacks] = useState(false);
  const [loadingContent, setLoadingContent] = useState(false);
  const [fieldError, setFieldError] = useState('');
  const [stackError, setStackError] = useState('');
  const [contentError, setContentError] = useState('');

  useEffect(() => {
    let active = true;
    techFieldApi.list()
      .then((data) => {
        if (!active) return;
        setFields(Array.isArray(data) ? data : []);
        setFieldError('');
      })
      .catch((error) => active && setFieldError(getApiErrorMessage(error, 'Teknoloji alanları yüklenemedi. Lütfen tekrar deneyin.')))
      .finally(() => active && setLoadingFields(false));
    return () => { active = false; };
  }, []);

  useEffect(() => {
    if (!activeFieldId || !isAuthenticated || searchMode) return undefined;
    let active = true;
    setLoadingStacks(true);
    setStackError('');
    setStacks([]);
    setSelectedStack(null);
    setContent(null);
    techFieldApi.listStacks(activeFieldId)
      .then((data) => {
        if (active) setStacks(Array.isArray(data) ? data : []);
      })
      .catch((error) => active && setStackError(getApiErrorMessage(error, 'Teknolojiler yüklenemedi. Lütfen tekrar deneyin.')))
      .finally(() => active && setLoadingStacks(false));
    return () => { active = false; };
  }, [activeFieldId, isAuthenticated, searchMode]);

  useEffect(() => {
    if (!activeFieldId && !searchMode) return undefined;

    function closeOnEscape(event) {
      const isEditable = event.target instanceof Element
        && event.target.closest('input, textarea, select, [contenteditable="true"]');
      if (event.key === 'Escape' && !event.defaultPrevented && !isEditable) closeWindow();
    }

    document.addEventListener('keydown', closeOnEscape);
    return () => document.removeEventListener('keydown', closeOnEscape);
  });

  const selectedField = useMemo(
    () => fields.find((field) => field.id === activeFieldId) || null,
    [activeFieldId, fields],
  );

  const filteredFields = useMemo(() => {
    const normalized = searchTerm.trim().toLocaleLowerCase('tr-TR');
    if (!normalized || searchMode) return fields;
    return fields.filter((field) => `${field.name} ${field.description || ''}`.toLocaleLowerCase('tr-TR').includes(normalized));
  }, [fields, searchMode, searchTerm]);

  function resetTechnologySelection() {
    setSelectedStack(null);
    setContent(null);
    setContentError('');
  }

  function openFolder(fieldId) {
    setSearchMode(false);
    setSubmittedSearch('');
    setActiveFieldId(fieldId);
    resetTechnologySelection();
  }

  function closeWindow() {
    setActiveFieldId(null);
    setSearchMode(false);
    setSubmittedSearch('');
    setStacks([]);
    resetTechnologySelection();
  }

  async function handleSearch(event) {
    event.preventDefault();
    const normalized = searchTerm.trim();
    if (!normalized || !isAuthenticated) return;

    setSearchMode(true);
    setSubmittedSearch(normalized);
    setActiveFieldId(null);
    setLoadingStacks(true);
    setStackError('');
    setStacks([]);
    resetTechnologySelection();
    try {
      const data = await techFieldApi.searchStacks(normalized);
      setStacks(Array.isArray(data) ? data : []);
    } catch (error) {
      setStackError(getApiErrorMessage(error, 'Arama tamamlanamadı. Lütfen tekrar deneyin.'));
    } finally {
      setLoadingStacks(false);
    }
  }

  async function chooseStack(stack) {
    setSelectedStack(stack);
    setContent(null);
    setContentError('');
    setLoadingContent(true);
    try {
      setContent(await techFieldApi.getStackContent(stack.id));
    } catch (error) {
      setContentError(getApiErrorMessage(error, 'Teknoloji içeriği yüklenemedi. Lütfen tekrar deneyin.'));
    } finally {
      setLoadingContent(false);
    }
  }

  function handleSearchChange(event) {
    const nextValue = event.target.value;
    setSearchTerm(nextValue);
    if (!nextValue.trim() && searchMode) closeWindow();
  }

  const windowOpen = Boolean(activeFieldId || searchMode);
  const windowTitle = searchMode ? `Arama Sonuçları: “${submittedSearch}”` : selectedField?.name;

  return (
    <section className="tracks-page">
      <header className="tracks-hero">
        <div>
          <p className="tracks-hero__eyebrow"><span aria-hidden="true">●</span> Teknoloji Alanları / Workspace</p>
          <h1>Geliştirici<br /><em>masaüstünü keşfet.</em></h1>
        </div>
        <div className="tracks-hero__copy">
          <span aria-hidden="true">~/techdev/fields</span>
          <p>Bir klasör seçin; teknoloji listesini ve backend tarafından derlenen öğrenme kaynaklarını inceleyin.</p>
        </div>
      </header>

      <form className="technology-search" onSubmit={handleSearch} role="search">
        <label htmlFor="technology-search-input">Teknoloji alanı veya teknoloji ara</label>
        <div>
          <span aria-hidden="true">⌕</span>
          <input
            id="technology-search-input"
            type="search"
            value={searchTerm}
            placeholder="Teknoloji alanı veya teknoloji ara"
            onChange={handleSearchChange}
          />
          <button type="submit" disabled={!searchTerm.trim() || !isAuthenticated || loadingStacks}>Ara</button>
        </div>
        {!isAuthenticated && <small>Alanlar anında filtrelenir. Teknoloji araması için giriş yapmanız gerekir.</small>}
      </form>

      {fieldError && <div className="state-card state-card--error" role="alert"><strong>Teknoloji alanları yüklenemedi</strong><p>{fieldError}</p></div>}
      {loadingFields && (
        <div className="technology-folder-grid technology-folder-grid--skeleton" aria-label="Teknoloji alanları yükleniyor">
          {Array.from({ length: 8 }, (_, index) => <div key={index}><span /><i /></div>)}
          <p>Teknoloji alanları yükleniyor…</p>
        </div>
      )}
      {!loadingFields && !fieldError && fields.length === 0 && <div className="state-card">Henüz bir teknoloji alanı eklenmemiş.</div>}

      {!loadingFields && !fieldError && fields.length > 0 && !windowOpen && (
        <div className="technology-desktop">
          <div className="technology-desktop__bar"><span aria-hidden="true"><i /><i /><i /></span><strong>TechDev Workspace</strong><small>{filteredFields.length} klasör</small></div>
          {filteredFields.length > 0 ? (
            <TechnologyFolderGrid fields={filteredFields} onOpen={openFolder} />
          ) : (
            <div className="technology-empty-search"><span aria-hidden="true">⌕</span><strong>Sonuç bulunamadı</strong><p>Aramanızla eşleşen bir teknoloji alanı bulunamadı.</p></div>
          )}
        </div>
      )}

      {!loadingFields && !fieldError && windowOpen && windowTitle && (
        <TechnologyWindow
          title={windowTitle}
          stacks={stacks}
          selectedStack={selectedStack}
          content={content}
          loadingStacks={loadingStacks}
          loadingContent={loadingContent}
          stackError={stackError}
          contentError={contentError}
          isAuthenticated={isAuthenticated}
          onClose={closeWindow}
          onChooseStack={chooseStack}
          onBreadcrumbHome={closeWindow}
          onBreadcrumbField={resetTechnologySelection}
          emptyText={searchMode ? 'Aramanızla eşleşen bir teknoloji alanı bulunamadı.' : 'Bu alana henüz bir teknoloji eklenmemiş.'}
        />
      )}
    </section>
  );
}

export default TracksPage;
