import { useEffect, useMemo, useState } from 'react';
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext.jsx';
import { getApiErrorMessage } from '../../services/apiClient.js';
import {
  getPendingVerificationEmail,
  setPendingVerificationEmail,
} from '../../services/pendingVerification.js';
import '../shared-pages.css';
import './VerifyEmailPage.css';

const RESEND_COOLDOWN_SECONDS = 60;

function getErrorDetails(error) {
  const status = error?.status ?? error?.response?.status;
  const data = error?.data ?? error?.response?.data;
  const backendMessage = typeof data === 'string' ? data : data?.message;
  const normalizedMessage = typeof backendMessage === 'string'
    ? backendMessage.toLocaleLowerCase('tr-TR')
    : '';

  if (status === 403) {
    return {
      message: 'Doğrulama kodu hatalı. Lütfen kodu kontrol ederek tekrar deneyin.',
      kind: 'invalid',
    };
  }

  if (status === 409 && normalizedMessage.includes('zaten')) {
    return {
      message: 'Bu hesap daha önce doğrulanmış. Giriş sayfasından oturum açabilirsiniz.',
      kind: 'already-verified',
    };
  }

  if (status === 409) {
    return {
      message: 'Doğrulama kodunun süresi dolmuş. Yeni bir kod gönderebilirsiniz.',
      kind: 'expired',
    };
  }

  return {
    message: getApiErrorMessage(error, 'Doğrulama işlemi tamamlanamadı. Lütfen tekrar deneyin.'),
    kind: 'other',
  };
}

function VerifyEmailPage() {
  const { isAuthenticated, isLoading, resendVerification, verifyEmail } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();
  const email = useMemo(
    () => location.state?.email?.trim() || getPendingVerificationEmail(),
    [location.state?.email],
  );
  const [code, setCode] = useState('');
  const [error, setError] = useState('');
  const [errorKind, setErrorKind] = useState('');
  const [success, setSuccess] = useState(
    location.state?.noticeType === 'success' ? location.state.notice : '',
  );
  const [initialNotice] = useState(
    location.state?.noticeType !== 'success' ? location.state?.notice || '' : '',
  );
  const [isResending, setIsResending] = useState(false);
  const [cooldown, setCooldown] = useState(
    location.state?.resendCooldown ? RESEND_COOLDOWN_SECONDS : 0,
  );

  useEffect(() => {
    if (email) setPendingVerificationEmail(email);
  }, [email]);

  useEffect(() => {
    if (cooldown <= 0) return undefined;
    const timer = window.setInterval(() => {
      setCooldown((current) => Math.max(0, current - 1));
    }, 1000);
    return () => window.clearInterval(timer);
  }, [cooldown]);

  if (isAuthenticated) return <Navigate to="/" replace />;

  function handleCodeChange(event) {
    setCode(event.target.value.replace(/\D/g, '').slice(0, 6));
    if (errorKind === 'invalid') {
      setError('');
      setErrorKind('');
    }
  }

  async function handleSubmit(event) {
    event.preventDefault();
    if (!email || code.length !== 6 || isLoading) return;
    setError('');
    setErrorKind('');
    setSuccess('');

    try {
      await verifyEmail(email, code);
      navigate(location.state?.returnTo || '/', {
        replace: true,
        state: { notice: 'E-posta adresiniz doğrulandı. Hoş geldiniz!' },
      });
    } catch (requestError) {
      const details = getErrorDetails(requestError);
      setError(details.message);
      setErrorKind(details.kind);
    }
  }

  async function handleResend() {
    if (!email || isResending || cooldown > 0) return;
    setIsResending(true);
    setError('');
    setErrorKind('');
    setSuccess('');

    try {
      await resendVerification(email);
      setSuccess('Yeni doğrulama kodu gönderildi. E-posta kutunuzu kontrol edin.');
      setCooldown(RESEND_COOLDOWN_SECONDS);
    } catch (requestError) {
      setError(getApiErrorMessage(
        requestError,
        'Yeni doğrulama kodu gönderilemedi. Lütfen daha sonra tekrar deneyin.',
      ));
      setErrorKind('resend');
    } finally {
      setIsResending(false);
    }
  }

  if (!email) {
    return (
      <section className="auth-page">
        <div className="auth-card verify-card">
          <div className="page-heading">
            <p className="page-heading__eyebrow">E-posta doğrulama</p>
            <h1>E-posta Adresinizi Doğrulayın</h1>
          </div>
          <p className="feedback feedback--error" role="alert">
            Doğrulama işlemi için e-posta adresi bulunamadı.
          </p>
          <Link className="button-primary" to="/register">Kayıt Sayfasına Dön</Link>
        </div>
      </section>
    );
  }

  return (
    <section className="auth-page">
      <div className="auth-card verify-card">
        <div className="page-heading">
          <p className="page-heading__eyebrow">Son bir adım</p>
          <h1>E-posta Adresinizi Doğrulayın</h1>
          <p>E-posta adresinize gönderilen 6 haneli doğrulama kodunu girin.</p>
        </div>

        <div className="verify-email-summary">
          <span>Kod şu adrese gönderildi:</span>
          <strong>{email}</strong>
        </div>

        {initialNotice && <p className="feedback" role="status">{initialNotice}</p>}
        {success && <p className="feedback feedback--success" role="status">{success}</p>}
        {error && <p id="verification-error" className="feedback feedback--error" role="alert">{error}</p>}

        <form className="stack-form" onSubmit={handleSubmit} noValidate>
          <label htmlFor="verification-code">
            Doğrulama Kodu
            <input
              id="verification-code"
              className="verification-code-input"
              type="text"
              inputMode="numeric"
              autoComplete="one-time-code"
              pattern="[0-9]{6}"
              maxLength={6}
              value={code}
              onChange={handleCodeChange}
              aria-describedby={error ? 'verification-error verification-code-help' : 'verification-code-help'}
              aria-invalid={Boolean(error)}
              autoFocus
            />
          </label>
          <small id="verification-code-help" className="verification-help">
            Doğrulama kodu 15 dakika boyunca geçerlidir.
          </small>
          <button className="button-primary" type="submit" disabled={isLoading || code.length !== 6}>
            {isLoading ? 'Oturumunuz açılıyor...' : 'Hesabımı Doğrula'}
          </button>
        </form>

        {errorKind === 'already-verified' ? (
          <Link className="button-secondary" to="/login">Giriş Sayfasına Git</Link>
        ) : (
          <div className={`resend-verification ${errorKind === 'expired' ? 'resend-verification--highlighted' : ''}`}>
            <span>Kod gelmedi mi?</span>
            <button
              className="text-button"
              type="button"
              onClick={handleResend}
              disabled={isResending || cooldown > 0}
            >
              {isResending
                ? 'Kod Gönderiliyor...'
                : cooldown > 0
                  ? `Yeni kodu ${cooldown} saniye sonra gönderebilirsiniz.`
                  : 'Yeni Kod Gönder'}
            </button>
          </div>
        )}

        {import.meta.env.DEV && (
          <p className="mailhog-note">
            Geliştirme ortamında doğrulama kodunu{' '}
            <a href="http://localhost:8025" target="_blank" rel="noopener noreferrer">MailHog üzerinden görüntüleyebilirsiniz.</a>
          </p>
        )}
      </div>
    </section>
  );
}

export default VerifyEmailPage;
