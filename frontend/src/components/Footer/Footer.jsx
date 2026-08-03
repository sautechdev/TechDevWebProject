import { Link } from 'react-router-dom';
import { FaInstagram, FaLinkedin } from 'react-icons/fa'; // İkonları import ettik
import './Footer.css';

function Footer() {
  return (
    <footer className="site-footer">
      <div className="footer-brand">
        <strong>SAÜ TechDev</strong>
        <p>Üreten, paylaşan ve birlikte gelişen teknoloji topluluğu.</p>
      </div>

      <nav aria-label="Alt navigasyon" className="footer-nav">
        <Link to="/tracks">Teknoloji Alanları</Link>
        <Link to="/archive">Dijital Arşiv</Link>
        <Link to="/events">Etkinlikler</Link>
        <Link to="/about">Hakkımızda</Link>
      </nav>

      <div className="footer-contact">
        <strong>Bize Ulaşın</strong>
        <div className="social-links">
          <a href="https://www.instagram.com/sautechdev?igsh=MXFrY29pMWp0Z2dndg==" target="_blank" rel="noreferrer" aria-label="Instagram">
            <FaInstagram size={24} />
          </a>
          <a href="https://www.linkedin.com/company/techdevsau/" target="_blank" rel="noreferrer" aria-label="LinkedIn">
            <FaLinkedin size={24} />
          </a>
        </div>
      </div>
    </footer>
  );
}

export default Footer;