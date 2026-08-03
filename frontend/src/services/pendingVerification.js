const PENDING_VERIFICATION_EMAIL_KEY = 'techdev_pending_verification_email';

export function getPendingVerificationEmail() {
  try {
    return sessionStorage.getItem(PENDING_VERIFICATION_EMAIL_KEY)?.trim() || '';
  } catch {
    return '';
  }
}

export function setPendingVerificationEmail(email) {
  const normalizedEmail = typeof email === 'string' ? email.trim() : '';
  if (!normalizedEmail) return;

  try {
    sessionStorage.setItem(PENDING_VERIFICATION_EMAIL_KEY, normalizedEmail);
  } catch {
    // Router state remains available if session storage is unavailable.
  }
}

export function clearPendingVerificationEmail() {
  try {
    sessionStorage.removeItem(PENDING_VERIFICATION_EMAIL_KEY);
  } catch {
    // Authentication can still complete if session storage is unavailable.
  }
}
