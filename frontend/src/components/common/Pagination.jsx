function Pagination({ pageNumber, totalPages, onPageChange, disabled = false }) {
  if (totalPages <= 1) return null;

  return (
    <nav className="pagination" aria-label="Sayfalama">
      <button
        className="button-secondary"
        type="button"
        disabled={disabled || pageNumber <= 0}
        onClick={() => onPageChange(pageNumber - 1)}
      >
        Önceki
      </button>
      <span>{pageNumber + 1} / {totalPages}</span>
      <button
        className="button-secondary"
        type="button"
        disabled={disabled || pageNumber >= totalPages - 1}
        onClick={() => onPageChange(pageNumber + 1)}
      >
        Sonraki
      </button>
    </nav>
  );
}

export default Pagination;
