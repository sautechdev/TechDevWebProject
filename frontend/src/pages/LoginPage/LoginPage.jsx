import { useState } from 'react';
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext.jsx';
import { getApiErrorMessage } from '../../services/apiClient.js';
import { setPendingVerificationEmail } from '../../services/pendingVerification.js';
import '../shared-pages.css';

function LoginPage() {
  const { isAuthenticated, isLoading, login, resendVerification, sessionMessage } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [form, setForm] = useState({ email: '', password: '' });
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');

  if (isAuthenticated) return <Navigate to="/" replace />;

  async function handleSubmit(event) {
    event.preventDefault();
    setError('');
    if (!form.email.trim() || !form.password) {
      setError('E-posta ve şifre alanlarını doldurun.');
      return;
    }
    try {
      const email = form.email.trim();
      await login({ email, password: form.password });
      navigate(location.state?.from?.pathname || '/', { replace: true });
    } catch (err) {
      const errorData = err?.data ?? err?.response?.data;
      if (errorData?.errorCode === 'EMAIL_NOT_VERIFIED') {
        const email = form.email.trim();
        let notice = 'E-posta adresiniz henüz doğrulanmamış.';
        let noticeType = 'error';
        let resendCooldown = false;

        setPendingVerificationEmail(email);
        try {
          await resendVerification(email);
          notice = 'E-posta adresiniz henüz doğrulanmamış. Yeni bir doğrulama kodu gönderdik.';
          noticeType = 'success';
          resendCooldown = true;
        } catch (resendError) {
          notice = getApiErrorMessage(
            resendError,
            'Yeni doğrulama kodu gönderilemedi. Doğrulama ekranından tekrar deneyebilirsiniz.',
          );
        }

        navigate('/verify-email', {
          replace: true,
          state: {
            email,
            source: 'login',
            notice,
            noticeType,
            resendCooldown,
            returnTo: location.state?.from?.pathname || '/',
          },
        });
        return;
      }

      const status = err?.status ?? err?.response?.status;
      if (status === null || status === undefined) {
        setError('Sunucuya bağlanılamadı. Lütfen daha sonra tekrar deneyin.');
      } else if (status === 401 || status === 403) {
        setError('E-posta veya şifre hatalı.');
      } else {
        setError(getApiErrorMessage(err, 'Giriş işlemi tamamlanamadı.'));
      }
    }
  }

  return (
    <section className="auth-page">
      <div className="auth-card">
        <div className="page-heading">
          <p className="page-heading__eyebrow">TechDev hesabı</p>
          <h1>Tekrar hoş geldiniz.</h1>
          <p>Projelerinizi ve başvurularınızı yönetmek için giriş yapın.</p>
        </div>
        {sessionMessage && <p className="feedback feedback--error" role="alert">{sessionMessage}</p>}
        <form className="stack-form" onSubmit={handleSubmit} noValidate>
          <label htmlFor="login-email">E-posta
            <input id="login-email" type="email" autoComplete="email" required value={form.email}
              onChange={(event) => setForm({ ...form, email: event.target.value })} />
          </label>
          <label htmlFor="login-password">Şifre
            <span className="password-field">
              <input id="login-password" type={showPassword ? 'text' : 'password'} autoComplete="current-password"
                required value={form.password} onChange={(event) => setForm({ ...form, password: event.target.value })} />
              <button type="button" className="button-secondary" aria-pressed={showPassword}
                onClick={() => setShowPassword((value) => !value)}>{showPassword ? 'Gizle' : 'Göster'}</button>
            </span>
          </label>
          {error && <p className="feedback feedback--error" role="alert">{error}</p>}
          <button className="button-primary" type="submit" disabled={isLoading}>
            {isLoading ? 'Giriş yapılıyor…' : 'Giriş yap'}
          </button>
          <p className="auth-switch">Hesabınız yok mu? <Link to="/register">Hesap oluşturun.</Link></p>
        </form>
      </div>
    </section>
  );
}

export default LoginPage;
