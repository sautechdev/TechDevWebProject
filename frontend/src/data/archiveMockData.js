const archiveMockData = [
  {
    id: 'hackathon-gunu',
    title: 'Hackathon Günü',
    date: '2026-05-12',
    summary: 'Fikirlerin prototipe dönüştüğü, ekiplerin aynı masa etrafında üretip öğrendiği yoğun bir geliştirme günü.',
    meta: { location: 'TechDev Atölye', participants: '64 katılımcı', collection: 'Dosya 05' },
    photos: [
      { id: 'hack-1', alt: 'Hackathon açılışında sahneyi dinleyen katılımcılar', label: 'Açılış', tone: 'cyan' },
      { id: 'hack-2', alt: 'Dizüstü bilgisayarlarıyla birlikte çalışan bir ekip', label: 'Ekip çalışması', tone: 'blue' },
      { id: 'hack-3', alt: 'Hackathon sunumunda prototipini anlatan ekip', label: 'Demo saati', tone: 'violet' },
      { id: 'hack-4', alt: 'Hackathon gününün toplu kapanış fotoğrafı', label: 'Final', tone: 'navy' },
    ],
    notes: ['12 ekip aynı gün içinde çalışan bir prototip geliştirdi.', 'Mentor masalarında ürün, tasarım ve yazılım desteği verildi.', 'Jüri sunumlarının ardından üç ekip özel ödül aldı.'],
  },
  {
    id: 'techdev-tanisma',
    title: 'TechDev Tanışma Etkinliği',
    date: '2026-04-03',
    summary: 'Yeni üyelerin topluluk çalışma alanlarını keşfettiği ve proje ekipleriyle ilk kez buluştuğu tanışma akşamı.',
    meta: { location: 'Kampüs Fuaye', participants: '92 katılımcı', collection: 'Dosya 04' },
    photos: [
      { id: 'meet-1', alt: 'TechDev tanışma masasındaki isim kartları', label: 'Karşılama', tone: 'blue' },
      { id: 'meet-2', alt: 'Topluluk ekiplerini anlatan kısa sunum', label: 'Topluluk', tone: 'cyan' },
      { id: 'meet-3', alt: 'Tanışma oyununa katılan öğrenciler', label: 'İlk temas', tone: 'navy' },
    ],
    notes: ['Topluluğun çalışma grupları ve dönem planı paylaşıldı.', 'Katılımcılar ilgi alanlarına göre proje masalarıyla eşleşti.', 'Açık proje çağrıları etkinlik sonunda duyuruldu.'],
  },
  {
    id: 'frontend-workshop',
    title: 'Frontend Atölyesi',
    date: '2026-03-18',
    summary: 'Modern web arayüzleri, erişilebilirlik ve duyarlı tasarım üzerine uygulamalı bir frontend atölyesi.',
    meta: { location: 'Lab 204', participants: '38 katılımcı', collection: 'Dosya 03' },
    photos: [
      { id: 'front-1', alt: 'Frontend atölyesi kod örneklerinin gösterildiği ekran', label: 'Canlı kodlama', tone: 'violet' },
      { id: 'front-2', alt: 'Arayüz bileşenleri üzerinde çalışan katılımcılar', label: 'Uygulama', tone: 'blue' },
      { id: 'front-3', alt: 'Erişilebilirlik kontrol listesinin incelendiği masa', label: 'Kontrol', tone: 'cyan' },
      { id: 'front-4', alt: 'Atölye sonunda tamamlanan örnek arayüzler', label: 'Çıktılar', tone: 'navy' },
    ],
    notes: ['Katılımcılar yeniden kullanılabilir kart ve form bileşenleri geliştirdi.', 'Klavye navigasyonu ve renk kontrastı örneklerle test edildi.', 'Oturum materyalleri topluluk üyeleriyle paylaşıldı.'],
  },
  {
    id: 'yapay-zeka-soylesisi',
    title: 'Yapay Zekâ Söyleşisi',
    date: '2026-02-25',
    summary: 'Yapay zekâ ürünlerinin bugünü, etik sorumluluklar ve genç geliştiriciler için yeni çalışma alanları üzerine söyleşi.',
    meta: { location: 'Konferans Salonu', participants: '118 katılımcı', collection: 'Dosya 02' },
    photos: [
      { id: 'ai-1', alt: 'Yapay zekâ söyleşisinin konuşmacı paneli', label: 'Panel', tone: 'navy' },
      { id: 'ai-2', alt: 'Söyleşi sırasında not alan katılımcılar', label: 'Oturum', tone: 'violet' },
      { id: 'ai-3', alt: 'Katılımcıların sorularını yönelttiği bölüm', label: 'Soru & cevap', tone: 'cyan' },
    ],
    notes: ['Üretken yapay zekâ uygulamalarında doğrulama süreçleri konuşuldu.', 'Veri gizliliği ve etik tasarım için pratik ilkeler paylaşıldı.', 'Söyleşi açık soru-cevap oturumuyla tamamlandı.'],
  },
  {
    id: 'topluluk-bulusmasi',
    title: 'Topluluk Buluşması',
    date: '2026-01-10',
    summary: 'Yeni dönemin hedeflerinin belirlendiği, proje sahiplerinin deneyimlerini aktardığı sıcak bir topluluk buluşması.',
    meta: { location: 'TechDev Hub', participants: '51 katılımcı', collection: 'Dosya 01' },
    photos: [
      { id: 'community-1', alt: 'Topluluk buluşmasında yuvarlak masa düzeni', label: 'Buluşma', tone: 'blue' },
      { id: 'community-2', alt: 'Dönem hedeflerinin yazıldığı fikir panosu', label: 'Yeni dönem', tone: 'cyan' },
      { id: 'community-3', alt: 'Proje deneyimlerini paylaşan topluluk üyesi', label: 'Deneyim paylaşımı', tone: 'violet' },
    ],
    notes: ['Bahar dönemi etkinlik takvimi birlikte şekillendirildi.', 'Yeni mentorluk eşleşmeleri için ilk başvurular toplandı.', 'Üyeler geliştirmek istedikleri proje fikirlerini paylaştı.'],
  },
];

export default archiveMockData;
