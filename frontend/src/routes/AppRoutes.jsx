import { Navigate, Route, Routes } from 'react-router-dom';
import MainLayout from '../layouts/MainLayout.jsx';
import MainPage from '../pages/MainPage/MainPage.jsx';
import ProjectPage from '../pages/ProjectPage/ProjectPage.jsx';
import TracksPage from '../pages/TracksPage/TracksPage.jsx';
import ArchivePage, { ArchiveDetailPage } from '../pages/ArchivePage/ArchivePage.jsx';
import EventsPage, { EventDetailPage } from '../pages/EventsPage/EventsPage.jsx';
import NotificationsPage from '../pages/NotificationsPage/NotificationsPage.jsx';
import LoginPage from '../pages/LoginPage/LoginPage.jsx';
import RegisterPage from '../pages/RegisterPage/RegisterPage.jsx';
import VerifyEmailPage from '../pages/VerifyEmailPage/VerifyEmailPage.jsx';
import CreateProjectPage from '../pages/CreateProjectPage/CreateProjectPage.jsx';
import DevSkillsPreview from '../pages/DevSkillsPreview/DevSkillsPreview.jsx';
import ProfilePage from '../pages/ProfilePage/ProfilePage.jsx';
import ProjectChatPage from '../pages/ProjectChatPage/ProjectChatPage.jsx';
import AboutPage from '../pages/AboutPage/AboutPage.jsx';
import { AdminDashboard, AdminProjects, AdminUsers, PendingProjects } from '../pages/AdminPage/AdminPage.jsx';
import ProtectedRoute from './ProtectedRoute.jsx';
import AdminRoute from './AdminRoute.jsx';

function AppRoutes() {
  return (
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
          </Route>
        </Route>
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

export default AppRoutes;
