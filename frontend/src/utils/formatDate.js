export function formatDate(dateValue, locale = 'tr-TR') {
  return new Intl.DateTimeFormat(locale, {
    day: '2-digit',
    month: 'long',
    year: 'numeric',
  }).format(new Date(dateValue));
}
