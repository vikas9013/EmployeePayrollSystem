import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import { ToastProvider } from './context/ToastContext';
import Sidebar from './components/Sidebar';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Onboarding from './pages/Onboarding';
import Employees from './pages/Employees';
import Ratings from './pages/Ratings';
import Profile from './pages/Profile';
import { ReactNode } from 'react';

interface ProtectedRouteProps {
  children: ReactNode;
  allowedRoles?: string[];
}

const ProtectedRoute = ({ children, allowedRoles }: ProtectedRouteProps) => {
  const { user, loading } = useAuth();
  
  if (loading) return null; // or a global loading spinner
  
  if (!user) {
    return <Navigate to="/login" replace />;
  }
  
  if (allowedRoles && !allowedRoles.some(role => user.roles.includes(role))) {
    return <Navigate to="/profile" replace />;
  }
  
  return (
    <div className="app-container">
      <Sidebar />
      <main className="main-content">
        {children}
      </main>
    </div>
  );
};

const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/" element={
        <ProtectedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_HR']}>
          <Dashboard />
        </ProtectedRoute>
      } />
      <Route path="/employees" element={
        <ProtectedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_HR']}>
          <Employees />
        </ProtectedRoute>
      } />
      <Route path="/onboarding" element={
        <ProtectedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_HR']}>
          <Onboarding />
        </ProtectedRoute>
      } />
      <Route path="/profile" element={
        <ProtectedRoute>
          <Profile />
        </ProtectedRoute>
      } />
      <Route path="/ratings" element={
        <ProtectedRoute allowedRoles={['ROLE_ADMIN', 'ROLE_MANAGER', 'ROLE_HR', 'ROLE_EMPLOYEE']}>
          <Ratings />
        </ProtectedRoute>
      } />
    </Routes>
  );
};

function App() {
  return (
    <ToastProvider>
      <AuthProvider>
        <Router>
          <AppRoutes />
        </Router>
      </AuthProvider>
    </ToastProvider>
  );
}

export default App;
