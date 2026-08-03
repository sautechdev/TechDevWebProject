import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext.jsx';

function AdminRoute() {
  const { currentUser } = useAuth();
  return currentUser?.role === 'ADMIN' ? <Outlet /> : <Navigate to="/" replace />;
}

export default AdminRoute;
