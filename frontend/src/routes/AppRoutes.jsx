import { lazy, Suspense } from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import MainLayout from '../layouts/MainLayout.jsx';
import MainPage from '../pages/MainPage/MainPage.jsx';
import LoginPage from '../pages/LoginPage/LoginPage.jsx';
import RegisterPage from '../pages/RegisterPage/RegisterPage.jsx';
import VerifyEmailPage from '../pages/VerifyEmailPage/VerifyEmailPage.jsx';
import ProtectedRoute from './ProtectedRoute.jsx';
import AdminRoute from './AdminRoute.jsx';

// Az ziyaret edilen / ağır sayfalar sadece ihtiyaç anında indirilsin diye "lazy" yükleniyor.
// Bu, ilk açılışta indirilen JS miktarını azaltır - özellikle mobilde hissedilir fark yaratır.
const ProjectPage = lazy(() => import('../pages/ProjectPage/ProjectPage.jsx'));
const TracksPage = lazy(() => import('../pages/TracksPage/TracksPage.jsx'));
const ArchivePage = lazy(() => import('../pages/ArchivePage/ArchivePage.jsx').then((m) => ({ default: m.default })));
const ArchiveDetailPage = lazy(() => import('../pages/ArchivePage/ArchivePage.jsx').then((m) => ({ default: m.ArchiveDetailPage })));
const EventsPage = lazy(() => import('../pages/EventsPage/EventsPage.jsx').then((m) => ({ default: m.default })));
const EventDetailPage = lazy(() => import('../pages/EventsPage/EventsPage.jsx').then((m) => ({ default: m.EventDetailPage })));
const AboutPage = lazy(() => import('../pages/AboutPage/AboutPage.jsx'));
const NotificationsPage = lazy(() => import('../pages/NotificationsPage/NotificationsPage.jsx'));
const ProfilePage = lazy(() => import('../pages/ProfilePage/ProfilePage.jsx'));
const CreateProjectPage = lazy(() => import('../pages/CreateProjectPage/CreateProjectPage.jsx'));
const ProjectChatPage = lazy(() => import('../pages/ProjectChatPage/ProjectChatPage.jsx'));
const DevSkillsPreview = lazy(() => import('../pages/DevSkillsPreview/DevSkillsPreview.jsx'));
const AdminDashboard = lazy(() => import('../pages/AdminPage/AdminPage.jsx').then((m) => ({ default: m.AdminDashboard })));
const AdminProjects = lazy(() => import('../pages/AdminPage/AdminPage.jsx').then((m) => ({ default: m.AdminProjects })));
const PendingProjects = lazy(() => import('../pages/AdminPage/AdminPage.jsx').then((m) => ({ default: m.PendingProjects })));
const AdminUsers = lazy(() => import('../pages/AdminPage/AdminPage.jsx').then((m) => ({ default: m.AdminUsers })));
const AdminSkills = lazy(() => import('../pages/AdminPage/AdminPage.jsx').then((m) => ({ default: m.AdminSkills })));
const AdminArchive = lazy(() => import('../pages/AdminPage/AdminPage.jsx').then((m) => ({ default: m.AdminArchive })));

function PageFallback() {
  return <div className="page-loading" role="status" aria-live="polite">Yükleniyor…</div>;
}

function AppRoutes() {
  return (
    <Suspense fallback={<PageFallback />}>
      <Routes>
        <Route element={<MainLayout />}>
          <Route path="/" element={<MainPage />} />
          <Route path="/projects" element={<ProjectPage />} />
          <Route path="/tracks" element={<TracksPage />} />
          <Route path="/archive" element={<ArchivePage />} />
          <Route path="/archive/:archiveId" element={<ArchiveDetailPage />} />
          <Route path="/events" element={<EventsPage />} />
          <Route path="/events/:eventId" element={<EventDetailPage />} />
          <Route path="/about" element={<AboutPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/verify-email" element={<VerifyEmailPage />} />
          {import.meta.env.DEV && <Route path="/dev/skills-preview" element={<DevSkillsPreview />} />}
          {import.meta.env.DEV && <Route path="/dev/project-form-preview" element={<CreateProjectPage />} />}
          <Route element={<ProtectedRoute />}>
            <Route path="/notifications" element={<NotificationsPage />} />
            <Route path="/profile" element={<ProfilePage />} />
            <Route path="/projects/new" element={<CreateProjectPage />} />
            <Route path="/projects/:projectId/chat" element={<ProjectChatPage />} />
            <Route element={<AdminRoute />}>
              <Route path="/admin" element={<AdminDashboard />} />
              <Route path="/admin/projects" element={<AdminProjects />} />
              <Route path="/admin/projects/pending" element={<PendingProjects />} />
              <Route path="/admin/users" element={<AdminUsers />} />
              <Route path="/admin/skills" element={<AdminSkills />} />
              <Route path="/admin/archive" element={<AdminArchive />} />
            </Route>
          </Route>
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Suspense>
  );
}

export default AppRoutes;
