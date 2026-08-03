# TechDev Backend

Kulüp web sitesi backend servisi. Spring Boot + PostgreSQL + Redis + Docker ile geliştirilmiştir.

## Teknolojiler

- Java 21
- Spring Boot 4.1.0
- PostgreSQL 16
- Redis 7
- MailHog (geliştirme ortamı için email test aracı)
- Docker & Docker Compose
- Gradle 8.14+
- Springdoc OpenAPI (Swagger UI)
- WebSocket (STOMP) — gerçek zamanlı bildirimler için
- Prometheus + Grafana — metrik izleme
- Sentry — hata takibi (error tracking)

## Gereksinimler

Projeyi çalıştırmak için bilgisayarında sadece **Docker Desktop** kurulu olması yeterli. Java, Gradle veya PostgreSQL'i ayrıca kurmana gerek yok, hepsi Docker container'ları içinde çalışır.

- [Docker Desktop](https://www.docker.com/products/docker-desktop/)

## Kurulum ve Çalıştırma

### 1. Projeyi Klonla

```bash
git clone <repo-url>
cd techDevWeb
```

### 2. Ortam Değişkenlerini Ayarla

Proje kök dizininde (`docker-compose.yml` ile aynı yerde) bir `.env` dosyası oluştur:

```
GITHUB_TOKEN=github_personal_access_token_buraya
DEEPL_API_KEY=deepl_api_key_buraya
SENTRY_DSN=sentry_dsn_buraya
SENTRY_AUTH_TOKEN=sentry_auth_token_buraya
REDIS_PASSWORD=redis_icin_guclu_bir_sifre
```

> - **GITHUB_TOKEN**: Teknoloji & Bilgi sayfası için ilgili teknolojilerin GitHub repo bilgilerini otomatik çekebilmek adına gerekli. [Buradan](https://github.com/settings/tokens) `public_repo` yetkisiyle bir token oluşturabilirsin. Token olmadan da proje çalışır, sadece GitHub içerik zenginleştirmesi atlanır / rate limit'e daha hızlı takılır.
> - **DEEPL_API_KEY**: Wikipedia ve Dev.to içeriklerinin otomatik Türkçe çevirisi için kullanılır. [DeepL API](https://www.deepl.com/pro-api) üzerinden ücretsiz bir key alabilirsin. Key yoksa çeviri atlanır, içerik İngilizce kalır.
> - **SENTRY_DSN / SENTRY_AUTH_TOKEN**: Hata takibi için. [Sentry](https://sentry.io) üzerinden ücretsiz bir proje oluşturup DSN ve auth token alabilirsin. Auth token, kaynak kod context'inin (`includeSourceContext`) Sentry'ye yüklenebilmesi için gerekli.
> - **REDIS_PASSWORD**: Redis'e erişim için zorunlu bir şifre. Güçlü bir değer üretmek için: `openssl rand -base64 24`

`.env` dosyasını git'e pushlama, `.gitignore`'da olduğundan emin ol.

### 3. Docker'ı Ayağa Kaldır

```bash
docker-compose up --build
```

İlk çalıştırmada:
- PostgreSQL, Redis, MailHog, Prometheus ve Grafana container'ları başlar
- Backend build edilir (Sentry Gradle plugin'i kaynak kodu Sentry'ye yükler, bu adım biraz zaman alabilir) ve başlar
- Veritabanı otomatik olarak teknoloji alanı/stack verileriyle doldurulur (seed)
- Wikipedia, Dev.to ve GitHub'dan içerikler otomatik çekilir ve Türkçeye çevrilir (GitHub rate limit'i ve çeviri istekleri nedeniyle bu adım birkaç dakika sürebilir)
- Veritabanında hiç admin kullanıcı yoksa, varsayılan bir admin hesabı otomatik oluşturulur (bkz. [Varsayılan Admin Hesabı](#varsayılan-admin-hesabı))

Şu logu görünce hazır demektir:

```
Started TechDevBackendApplication in X seconds
```

### 4. API'ye Eriş

Backend şu adreste çalışır:

```
http://localhost:8080
```

## Varsayılan Admin Hesabı

Uygulama ilk kez ayağa kalktığında (veritabanında hiç admin yoksa) otomatik bir admin hesabı oluşturulur:

| Alan | Varsayılan Değer |
|---|---|
| Email | `admin@techdev.com` |
| Şifre | `ChangeMe123!` |

Bu değerler `application.properties` üzerinden (`app.admin.email`, `app.admin.password`, `app.admin.full-name`) özelleştirilebilir. **Bu varsayılan hesap sadece geliştirme ortamı içindir**, prod profili aktifken (`app.admin` seeder'ı `@Profile("!prod")` ile korunur) çalışmaz — canlıya çıkarken admin hesabını güvenli bir yöntemle ayrıca oluşturman gerekir.

## Kullanılan Dış Servisler ve Araçlar

| Servis | Adres | Açıklama |
|---|---|---|
| Swagger UI | `http://localhost:8080/swagger-ui/index.html` | Tüm API endpoint'lerinin interaktif dokümantasyonu. Sağ üstten **Authorize** ile JWT token girip endpoint'leri doğrudan tarayıcıdan test edebilirsin. |
| MailHog | `http://localhost:8025` | Gönderilen tüm test e-postalarını (kayıt, bildirim vb.) görüntüler. Gerçek bir e-posta sunucusuna bağlanmaz, sadece geliştirme ortamı içindir. |
| WebSocket (STOMP) | `ws://localhost:8080/ws?token=<JWT>` | Gerçek zamanlı bildirim akışı. Bağlantı `token` query parametresiyle doğrulanır, bildirimler `/user/queue/notifications` kanalından gelir. |
| Prometheus | `http://localhost:9090` | Backend'in `/actuator/prometheus` endpoint'inden metrik topluyor (JVM memory, HTTP istek sayıları vb.). **Status → Targets** üzerinden `techdev-backend` job'ının `UP` olduğunu doğrulayabilirsin. |
| Grafana | `http://localhost:3000` | Prometheus verilerinin görselleştirildiği dashboard. Giriş: `admin` / `.env`'de tanımlı `GF_SECURITY_ADMIN_PASSWORD`. Hazır JVM dashboard'u için Dashboards → New → Import → ID: `4701`. |
| Sentry | [sentry.io](https://sentry.io) (kendi projen) | Yakalanamayan tüm hatalar (`GlobalExceptionHandler` üzerinden `Sentry.captureException`) otomatik olarak buraya düşer, **Issues** sekmesinden incelenir. |

## Ana Modüller

| Modül | Açıklama |
|---|---|
| **Auth** | Kayıt/giriş (JWT tabanlı), rol yönetimi (`USER` / `ADMIN`) |
| **Tech** | Teknoloji alanları ve stack'leri, Wikipedia/Dev.to/GitHub entegrasyonlu içerik, otomatik Türkçe çeviri |
| **Skill** | Kullanıcıların sahip olduğu yetkinliklerin (skill) yönetimi |
| **Archive** | Geçmiş etkinliklerin foto/video/döküman arşivi, dosya yükleme |
| **Event** | Online etkinlikler: oluşturma, kayıt olma, onay süreci, kontenjan, otomatik durum hesaplama |
| **Notification** | Uygulama içi + email + WebSocket bildirimleri, zamanlanmış hatırlatmalar |
| **Project** | Proje ilanları, uzmanlık alanları, başvuru/onay süreci, proje içi duyuru ve sohbet |
| **Profile** | Kullanıcının kendi profil bilgilerini görüntüleme/güncelleme |

## Temel Endpoint'ler

Aşağıdaki tablo sık kullanılan uç noktaları özetler; eksiksiz ve güncel liste için Swagger UI'ı kullanmanı öneririm.

| Endpoint | Açıklama |
|---|---|
| `POST /api/auth/register` | Kayıt ol |
| `POST /api/auth/login` | Giriş yap, JWT al |
| `GET /api/tech-fields` | Tüm teknoloji alanlarını listele |
| `GET /api/tech-stacks/field/{id}` | Bir alana ait teknolojiler |
| `GET /api/tech-stacks/search?keyword=...` | Teknoloji adına göre arama |
| `GET /api/tech-contents/stack/{id}` | Bir teknolojinin detaylı içeriği (Türkçe çevrilmiş) |
| `GET /api/archive-events?keyword=&year=&page=&size=` | Arşiv etkinliklerini listele (arama + yıl filtresi + sayfalama) |
| `POST /api/archive-items/upload` | Arşive dosya yükle (multipart/form-data) |
| `GET /api/events?status=&keyword=&page=&size=` | Etkinlikleri listele (durum filtresi + arama + sayfalama) |
| `POST /api/events/{id}/registrations` | Etkinliğe kayıt ol |
| `GET /api/notifications?onlyUnread=&page=&size=` | Bildirimleri listele |
| `GET /api/projects` | Aktif projeleri listele |
| `POST /api/expertise-areas/{areaId}/applications` | Bir projenin uzmanlık alanına başvur |
| `GET /actuator/health` | Uygulamanın sağlık durumu |
| `GET /actuator/prometheus` | Prometheus formatında ham metrikler |

## Sık Kullanılan Komutlar

**Projeyi durdur:**

```bash
docker-compose down
```

**Projeyi ve tüm verileri sıfırla (veritabanı + cache + yüklenen dosyalar silinir):**

```bash
docker-compose down -v
docker-compose up --build
```

**Sadece backend'i yeniden build et (DB/Redis'e dokunmaz):**

```bash
docker-compose up --build backend
```

**Logları canlı izle:**

```bash
docker-compose logs -f backend
```

**Belirli bir hata türünü loglarda ara:**

```bash
docker-compose logs backend | Select-String "ERROR"
```

**Redis'e şifreyle bağlan:**

```bash
docker exec -it techdev-redis redis-cli -a $REDIS_PASSWORD
```

## Proje Yapısı

```
techDevWeb/
├── docker-compose.yml
├── prometheus.yml                      # Prometheus scrape config
├── .env                                # git'e eklenmez
└── backend/
    └── techDevBackend/
        ├── Dockerfile
        ├── build.gradle
        └── src/main/java/com/techdevweb/techdevbackend/
            ├── Auth/            # Kayıt, giriş, JWT üretimi
            ├── Security/        # JWT filtresi, güvenlik yardımcıları
            ├── Tech/            # Teknoloji & Bilgi modülü (çeviri servisi dahil)
            ├── Skill/           # Kullanıcı özellik modülü
            ├── Archive/         # Dijital Arşiv modülü
            ├── Event/           # Etkinlikler modülü
            ├── Notification/    # Bildirim sistemi (in-app, email, WebSocket)
            ├── Project/         # Proje & Üye Alımı modülü
            ├── Profile/         # Kullanıcı profil modülü
            ├── Admin/           # Admin'e özel işlemler, varsayılan admin seeder
            ├── Exception/       # Merkezi hata yönetimi (Sentry entegrasyonu dahil)
            └── Config/          # Redis, WebConfig, WebSocket, Security, OpenAPI vb.
```

## İzleme ve Hata Takibi

- **Metrikler**: Backend, Spring Boot Actuator + Micrometer üzerinden `/actuator/prometheus` endpoint'inde metrik üretir. Prometheus bu endpoint'i her 15 saniyede bir (bkz. `prometheus.yml`) tarar.
- **Dashboard**: Grafana'da Prometheus veri kaynağı tanımlıdır; hazır JVM/Micrometer dashboard'u (ID: `4701`) import edilerek heap kullanımı, buffer pool, HTTP istek sayıları gibi metrikler görselleştirilebilir. Dashboard'daki `application` ve `instance` filtrelerini sırasıyla `techdev-backend` ve `backend:8080` olarak seçmen gerekir.
- **Hata takibi**: `GlobalExceptionHandler` içindeki genel `Exception` handler'ı, yakaladığı her hatayı `Sentry.captureException(ex)` ile Sentry'ye iletir. `includeSourceContext = true` sayesinde Sentry panelinde gerçek kaynak kod satırları görüntülenebilir (bunun için `SENTRY_AUTH_TOKEN` gereklidir).
- Tracing (performans izleme) şu an bilinçli olarak kapalı tutulmuştur (`sentry.traces-sample-rate=0.0`); yalnızca hata takibi aktiftir.

## Notlar

- Yüklenen dosyalar (`Archive` modülü) Docker volume'ünde saklanır, `docker-compose down -v` yaparsan **silinirler**. Sadece container'ı yeniden başlatmak (`down` + `up`, `-v` olmadan) dosyaları korur.
- GitHub API'nin dakikalık istek limiti olduğu için ilk seed işlemi (tüm teknoloji içeriklerinin çekilmesi ve çevrilmesi) birkaç dakika sürebilir, bu normaldir.
- Etkinlik/bildirim modüllerindeki liste endpoint'leri sayfalama (`page`, `size`, `sort` parametreleri) destekler; varsayılan sayfa boyutu endpoint'e göre değişir (bkz. Swagger).
- `@PreAuthorize` ile korunan admin endpoint'leri gerçekten çalışması için `SecurityConfig` üzerinde `@EnableMethodSecurity` aktif olmalı ve kullanıcı JWT'sinde ilgili rol authority'si (`ROLE_ADMIN`) bulunmalıdır.
- Redis artık şifre ile korunmaktadır; `.env`'deki `REDIS_PASSWORD` olmadan bağlantı reddedilir.
- DeepL ücretsiz planının aylık karakter kotası vardır; sık sık `docker-compose down -v` ile sıfırlamak (her seferinde tüm içerik yeniden çevrildiği için) kotayı hızlı tüketebilir.
