import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { LayoutDashboard, Users, UserPlus, Star, LogOut, User } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import './Sidebar.css';

interface SidebarLink {
  path: string;
  label: string;
  icon: React.ComponentType<{ size?: number; className?: string }>;
}

const Sidebar = () => {
  const { user, logout } = useAuth();
  const location = useLocation();

  const isAdmin = user?.roles?.includes('ROLE_ADMIN');
  const isHr = user?.roles?.includes('ROLE_HR');
  const showAdminFeatures = isAdmin || isHr;

  const links: SidebarLink[] = [
    ...(showAdminFeatures ? [
      { path: '/', label: 'Dashboard', icon: LayoutDashboard },
      { path: '/employees', label: 'Employees', icon: Users },
      { path: '/onboarding', label: 'Onboarding', icon: UserPlus },
    ] : []),
    { path: '/profile', label: 'My Profile', icon: User },
    { path: '/ratings', label: 'Ratings', icon: Star },
  ];

  return (
    <div className="sidebar glass-panel">
      <div className="sidebar-header">
        <h2 className="gradient-text">PayrollPro</h2>
      </div>
      
      <div className="sidebar-user">
        <div className="avatar">
          {user?.username?.[0]?.toUpperCase()}
        </div>
        <div className="user-info">
          <p className="username">{user?.username}</p>
          <p className="role">{isAdmin ? 'Administrator' : isHr ? 'HR Manager' : 'Employee'}</p>
        </div>
      </div>

      <nav className="sidebar-nav">
        {links.map((link) => {
          const Icon = link.icon;
          const isActive = location.pathname === link.path;
          return (
            <Link 
              key={link.path} 
              to={link.path} 
              className={`nav-link ${isActive ? 'active' : ''}`}
            >
              <Icon size={20} />
              <span>{link.label}</span>
            </Link>
          );
        })}
      </nav>

      <div className="sidebar-footer">
        <button className="logout-btn" onClick={logout}>
          <LogOut size={20} />
          <span>Logout</span>
        </button>
      </div>
    </div>
  );
};

export default Sidebar;
