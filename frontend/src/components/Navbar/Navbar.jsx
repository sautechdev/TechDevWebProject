import { useEffect, useState } from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext.jsx';
import techDevdarkLogo from '../../assets/TechDevseffafdark.png';
import techDevlightLogo from '../../assets/TechDevseffaflogo.png';
import NotificationBell from '../notifications/NotificationBell.jsx';
import './Navbar.css';

const navItems = [
  { label: 'Ana Sayfa', path: '/' },
  { label: 'Projeler', path: '/projects' },
  { label: 'Teknoloji Alanları', path: '/tracks' },
  { label: 'Dijital Arşiv', path: '/archive' },
  { label: 'Etkinlikler', path: '/events' },
  
];

function Navbar() {
  const [theme, setTheme] = useState(() => localStorage.getItem('techdev.theme') || 'dark');
  const [isSpinning, setIsSpinning] = useState(false);
  const [menuOpen, setMenuOpen] = useState(false);
  const { currentUser, isAuthenticated } = useAuth();

  useEffect(() => {
    document.documentElement.dataset.theme = theme;
    localStorage.setItem('techdev.theme', theme);
  }, [theme]);

  function toggleTheme() {
    setIsSpinning(true);
    setTheme((current) => (current === 'dark' ? 'light' : 'dark'));
    window.setTimeout(() => setIsSpinning(false), 520);
  }

  return (
    <header className="navbar">
      <NavLink className="navbar__brand" to="/">
        <img 
          src={theme === 'dark' ? techDevdarkLogo : techDevlightLogo} 
          alt="SAU TechDev" 
        />
      </NavLink>
      
      <button className="navbar__menu" type="button" aria-expanded={menuOpen} aria-controls="main-navigation" onClick={() => setMenuOpen((value) => !value)}>Menü</button>
      
      <nav id="main-navigation" className={menuOpen ? 'navbar__links is-open' : 'navbar__links'} aria-label="Ana navigasyon">
        {navItems.map((item) => (
          <NavLink
            className={({ isActive }) =>
              isActive ? 'navbar__link navbar__link--active' : 'navbar__link'
            }
            key={item.path}
            to={item.path}
            onClick={() => setMenuOpen(false)}
          >
            {item.label}
          </NavLink>
        ))}
        {!isAuthenticated && <NavLink className="navbar__link" to="/login" onClick={() => setMenuOpen(false)}>Giriş Yap</NavLink>}
        {isAuthenticated && <NavLink className="navbar__link" to="/profile" onClick={() => setMenuOpen(false)}>Profil</NavLink>}
        {currentUser?.role === 'ADMIN' && <NavLink className="navbar__link" to="/admin" onClick={() => setMenuOpen(false)}>Admin Paneli</NavLink>}
      </nav>
      
      {/* Bildirim zili ve Tema butonu yan yana */}
      {isAuthenticated && <NotificationBell />}
      
      <button
        className={isSpinning ? 'navbar__theme is-spinning' : 'navbar__theme'}
        type="button"
        aria-label={theme === 'dark' ? 'Açık temaya geç' : 'Koyu temaya geç'}
        onClick={toggleTheme}
      >
        {theme === 'dark' ? (
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M21 14.7A8.2 8.2 0 0 1 9.3 3a7 7 0 1 0 11.7 11.7Z" />
          </svg>
        ) : (
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <circle cx="12" cy="12" r="4.2" />
            <path d="M12 2v3M12 19v3M4.9 4.9 7 7M17 17l2.1 2.1M2 12h3M19 12h3M4.9 19.1 7 17M17 7l2.1-2.1" />
          </svg>
        )}
      </button>
    </header>
  );
}

export default Navbar;
