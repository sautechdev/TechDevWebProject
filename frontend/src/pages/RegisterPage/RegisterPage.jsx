import { useState } from 'react';
import { Link, Navigate, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext.jsx';
import { getApiErrorMessage } from '../../services/apiClient.js';
import { setPendingVerificationEmail } from '../../services/pendingVerification.js';
import '../shared-pages.css';

const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

function RegisterPage() {
  const { isAuthenticated, isLoading, register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ fullName: '', email: '', password: '', confirmation: '' });
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');

  if (isAuthenticated) return <Navigate to="/" replace />;

  async function handleSubmit(event) {
    event.preventDefault();
    if (isLoading) return;
    setError('');
    const fullName = form.fullName.trim();
    const email = form.email.trim();
    if (!fullName) return setError('Ad soyad boş bırakılamaz.');
    if (!emailPattern.test(email)) return setError('Geçerli bir e-posta adresi girin.');
    if (!form.password) return setError('Şifre boş bırakılamaz.');
    if (form.password !== form.confirmation) return setError('Şifre ve şifre tekrarı aynı olmalıdır.');

    try {
      const response = await register({ fullName, email, password: form.password });
      const verificationEmail = response?.email?.trim() || email;
      setPendingVerificationEmail(verificationEmail);
      navigate('/verify-email', {
        replace: true,
        state: {
          email: verificationEmail,
          source: 'register',
          notice: 'Kayıt işlemi başarılı. E-posta adresinize gönderilen doğrulama kodunu girin.',
        },
      });
    } catch (requestError) {
      setError(getApiErrorMessage(requestError, 'Kayıt işlemi tamamlanamadı.'));
    }
  }

  return <section className="auth-page"><div className="auth-card">
    <div className="page-heading"><p className="page-heading__eyebrow">TechDev’e katıl</p><h1>Hesap oluştur.</h1><p>Projeleri takip etmek ve fikirlerinizi paylaşmak için topluluğa katılın.</p></div>
    <form className="stack-form" onSubmit={handleSubmit} noValidate>
      <label htmlFor="register-name">Ad soyad<input id="register-name" autoComplete="name" required value={form.fullName} onChange={(e) => setForm({ ...form, fullName: e.target.value })} /></label>
      <label htmlFor="register-email">E-posta<input id="register-email" type="email" autoComplete="email" required value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} /></label>
      <label htmlFor="register-password">Şifre<span className="password-field"><input id="register-password" type={showPassword ? 'text' : 'password'} autoComplete="new-password" required value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} /><button className="button-secondary" type="button" aria-pressed={showPassword} onClick={() => setShowPassword((value) => !value)}>{showPassword ? 'Gizle' : 'Göster'}</button></span></label>
      <label htmlFor="register-confirmation">Şifre tekrarı<input id="register-confirmation" type={showPassword ? 'text' : 'password'} autoComplete="new-password" required value={form.confirmation} onChange={(e) => setForm({ ...form, confirmation: e.target.value })} /></label>
      {error && <p className="feedback feedback--error" role="alert">{error}</p>}
      <button className="button-primary" disabled={isLoading}>{isLoading ? 'Hesap oluşturuluyor…' : 'Hesap oluştur'}</button>
      <p className="auth-switch">Zaten hesabın var mı? <Link to="/login">Giriş yap</Link></p>
    </form>
  </div></section>;
}

export default RegisterPage;
