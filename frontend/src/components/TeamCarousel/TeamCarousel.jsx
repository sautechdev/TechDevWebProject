import { useRef, useState, useEffect, useCallback } from 'react';
import { FiChevronLeft, FiChevronRight } from 'react-icons/fi';
import { FaLinkedin } from 'react-icons/fa';
import './TeamCarousel.css';

function TeamCarousel({ members }) {
  const trackRef = useRef(null);
  const [activeIndex, setActiveIndex] = useState(0);

  const scrollToIndex = useCallback((index) => {
    const track = trackRef.current;
    if (!track) return;
    const clamped = Math.max(0, Math.min(index, members.length - 1));
    const card = track.children[clamped];
    if (card) {
      track.scrollTo({ left: card.offsetLeft - track.offsetLeft, behavior: 'smooth' });
    }
  }, [members.length]);

  const handlePrev = () => scrollToIndex(activeIndex - 1);
  const handleNext = () => scrollToIndex(activeIndex + 1);

  // Kullanıcı elle kaydırdığında hangi kartın aktif olduğunu güncelle
  useEffect(() => {
    const track = trackRef.current;
    if (!track) return;

    let frame = null;
    const handleScroll = () => {
      if (frame) cancelAnimationFrame(frame);
      frame = requestAnimationFrame(() => {
        const children = Array.from(track.children);
        let closest = 0;
        let minDistance = Infinity;
        children.forEach((child, i) => {
          const distance = Math.abs(child.offsetLeft - track.offsetLeft - track.scrollLeft);
          if (distance < minDistance) {
            minDistance = distance;
            closest = i;
          }
        });
        setActiveIndex(closest);
      });
    };

    track.addEventListener('scroll', handleScroll, { passive: true });
    return () => {
      track.removeEventListener('scroll', handleScroll);
      if (frame) cancelAnimationFrame(frame);
    };
  }, []);

  return (
    <div className="team-carousel">
      <button
        type="button"
        className="team-carousel__arrow team-carousel__arrow--prev"
        onClick={handlePrev}
        disabled={activeIndex === 0}
        aria-label="Önceki üye"
      >
        <FiChevronLeft />
      </button>

      <div className="team-carousel__track" ref={trackRef}>
        {members.map((member) => (
          <article className="team-carousel__card" key={member.name}>
            <img className="team-carousel__photo" src={member.photo} alt={member.name} />
            <h3>{member.name}</h3>
            <strong>{member.role}</strong>
            <p>{member.description}</p>
            {member.linkedin && (
              <a
                className="team-carousel__linkedin"
                href={member.linkedin}
                target="_blank"
                rel="noopener noreferrer"
                aria-label={member.name + ' LinkedIn profili'}
              >
                <FaLinkedin />
              </a>
            )}
          </article>
        ))}
      </div>

      <button
        type="button"
        className="team-carousel__arrow team-carousel__arrow--next"
        onClick={handleNext}
        disabled={activeIndex === members.length - 1}
        aria-label="Sonraki üye"
      >
        <FiChevronRight />
      </button>

      <div className="team-carousel__dots">
        {members.map((member, i) => (
          <button
            type="button"
            key={member.name}
            className={'team-carousel__dot' + (i === activeIndex ? ' team-carousel__dot--active' : '')}
            onClick={() => scrollToIndex(i)}
            aria-label={member.name + ' kartına git'}
          />
        ))}
      </div>
    </div>
  );
}

export default TeamCarousel;
