import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import ArchiveCabinet from '../../components/archive/ArchiveCabinet.jsx';
import { archiveApi } from '../../services/archiveApi.js';
import { asList } from '../../services/apiClient.js';
import './ArchivePage.css';

// Backend'ten gelen ArchiveEventResponse'u, arşiv dolabı bileşenlerinin
// beklediği görüntüleme şekline dönüştürür.
function mapArchiveEvent(event, index) {
  const items = event.items || [];
  return {
    id: String(event.id),
    title: event.title,
    date: event.eventDate,
    summary: event.description || '',
    meta: { collection: `Dosya ${String(index + 1).padStart(2, '0')}` },
    photos: items
      .filter((item) => item.type === 'PHOTO')
      .map((item) => ({ id: item.id, url: item.fileUrl, caption: item.caption, alt: item.caption || event.title })),
    notes: items.filter((item) => item.type === 'NOTE').map((item) => item.caption).filter(Boolean),
  };
}

function ArchivePage({ initialArchiveId }) {
  const [records, setRecords] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let active = true;
    archiveApi.list({ size: 100 })
      .then(async (data) => {
        if (!active) return;
        const events = asList(data);
        // Liste endpoint'i fotoğrafları içermiyor (performans için), her etkinlik
        // için ayrıca fotoğrafları çekip birleştiriyoruz.
        const eventsWithItems = await Promise.all(events.map(async (event) => {
          try {
            const items = await archiveApi.getItems(event.id);
            return { ...event, items: asList(items) };
          } catch {
            return { ...event, items: [] };
          }
        }));
        if (active) setRecords(eventsWithItems.map(mapArchiveEvent));
      })
      .catch(() => {
        if (active) setError('Arşiv kayıtları şu anda görüntülenemiyor. Lütfen yeniden deneyin.');
      })
      .finally(() => {
        if (active) setLoading(false);
      });

    return () => { active = false; };
  }, []);

  return (
    <section className="archive-page">
      <header className="archive-page__hero">
        <div>
          <p className="archive-page__eyebrow"><span aria-hidden="true">●</span> Dijital Arşiv / 2026</p>
          <h1>Topluluğun hafızası,<br /><em>tek bir çekmecede.</em></h1>
        </div>
        <div className="archive-page__intro">
          <span className="archive-page__rule" aria-hidden="true" />
          <p>TechDev’in etkinliklerinden kalan fotoğrafları, notları ve küçük anları dosya sekmelerinden keşfet.</p>
          <small>Bir dosyayı açmak için sekmesine dokun.</small>
        </div>
      </header>

      {loading && <div className="state-card" role="status">Arşiv çekmecesi hazırlanıyor…</div>}
      {error && <div className="state-card state-card--error" role="alert">{error}</div>}
      {!loading && !error && records.length === 0 && (
        <div className="state-card"><strong>Arşiv henüz boş.</strong><p>Yeni topluluk anıları burada dosyalanacak.</p></div>
      )}
      {!loading && !error && records.length > 0 && <ArchiveCabinet records={records} initialId={initialArchiveId} />}
    </section>
  );
}

export function ArchiveDetailPage() {
  const { archiveId } = useParams();
  return <ArchivePage initialArchiveId={archiveId} />;
}

export default ArchivePage;
