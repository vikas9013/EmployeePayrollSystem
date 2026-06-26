import { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import api from '../services/api';
import {
  User, Mail, Briefcase, Star, CreditCard,
  MessageSquare, GraduationCap, Sparkles, Shield
} from 'lucide-react';
import './Profile.css';

interface EmployeeProfile {
  id: number;
  name: string;
  designation: string;
  type: string;
  salary: number;
  workEmail: string;
  slackInviteSent: boolean;
  trainingAssigned: boolean;
  payrollConfigured: boolean;
  aiOnboardingMessage?: string;
}

interface Rating {
  id: number;
  projectName: string;
  score: number;
  feedback: string;
  createdAt: string;
  createdBy: string;
}

interface UserData {
  username: string;
  role: string;
  employeeId?: number;
}

const StarDisplay = ({ score }: { score: number }) => (
  <div className="star-display">
    {[1, 2, 3, 4, 5].map((s) => (
      <Star
        key={s}
        size={14}
        fill={s <= score ? 'currentColor' : 'none'}
        className={s <= score ? 'star-filled' : 'star-empty'}
      />
    ))}
  </div>
);

const Profile = () => {
  const { user } = useAuth();
  const { addToast } = useToast();

  const isEmployee = user?.roles?.includes('ROLE_EMPLOYEE');
  const isAdmin    = user?.roles?.includes('ROLE_ADMIN');
  const isHr       = user?.roles?.includes('ROLE_HR');

  const [userData, setUserData]       = useState<UserData | null>(null);
  const [profile, setProfile]         = useState<EmployeeProfile | null>(null);
  const [ratings, setRatings]         = useState<Rating[]>([]);
  const [loading, setLoading]         = useState(true);

  useEffect(() => {
    const load = async () => {
      setLoading(true);
      try {
        // 1. Fetch user record to get employeeId
        const meRes = await api.get<UserData>('/auth/me');
        setUserData(meRes.data);

        if (meRes.data.employeeId) {
          // 2. Fetch employee profile
          const empRes = await api.get<EmployeeProfile>(`/employees/${meRes.data.employeeId}`);
          setProfile(empRes.data);

          // 3. Fetch own ratings
          try {
            const ratingRes = await api.get<Rating[]>(`/ratings/employee/${meRes.data.employeeId}`);
            setRatings(ratingRes.data || []);
          } catch {
            // ratings may not exist yet — that's fine
          }
        }
      } catch {
        // If /auth/me doesn't exist, fall back to showing role-based info only
        setUserData({ username: user?.username || '', role: user?.roles?.[0] || '' });
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  const roleBadge = isAdmin ? 'Administrator' : isHr ? 'HR Manager' : 'Employee';
  const roleColor = isAdmin ? 'var(--accent-primary)' : isHr ? '#22c55e' : 'var(--accent-secondary)';

  const avgRating =
    ratings.length > 0
      ? (ratings.reduce((s, r) => s + r.score, 0) / ratings.length).toFixed(1)
      : null;

  if (loading) {
    return (
      <div className="profile-container">
        <div className="profile-loading glass-panel">Loading profile...</div>
      </div>
    );
  }

  return (
    <div className="profile-container">
      <h1>My Profile</h1>
      <p className="profile-subtitle">Your account details and performance overview</p>

      <div className="profile-grid">
        {/* ── Identity Card ── */}
        <div className="profile-identity-card glass-panel">
          <div className="profile-avatar-large">
            {(profile?.name || user?.username || '?')[0].toUpperCase()}
          </div>
          <h2 className="profile-name">{profile?.name || user?.username}</h2>
          <span className="role-badge" style={{ background: `${roleColor}22`, color: roleColor, borderColor: `${roleColor}44` }}>
            <Shield size={12} />
            {roleBadge}
          </span>

          <div className="profile-details-list">
            <div className="profile-detail-row">
              <User size={15} />
              <span className="detail-label">Username</span>
              <span className="detail-val">{user?.username}</span>
            </div>
            {profile?.workEmail && (
              <div className="profile-detail-row">
                <Mail size={15} />
                <span className="detail-label">Work Email</span>
                <span className="detail-val">{profile.workEmail}</span>
              </div>
            )}
            {profile?.designation && (
              <div className="profile-detail-row">
                <Briefcase size={15} />
                <span className="detail-label">Designation</span>
                <span className="detail-val">{profile.designation}</span>
              </div>
            )}
            {profile?.salary != null && (
              <div className="profile-detail-row">
                <CreditCard size={15} />
                <span className="detail-label">Monthly Salary</span>
                <span className="detail-val accent">
                  ₹{Math.round(profile.type === 'PartTimeEmployee' || profile.type === 'PARTTIME' ? profile.salary : (profile.salary / 12)).toLocaleString('en-IN')}
                </span>
              </div>
            )}
            {profile?.id && (
              <div className="profile-detail-row">
                <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', width: 15, textAlign: 'center' }}>#</span>
                <span className="detail-label">Employee ID</span>
                <span className="detail-val">EMP-{profile.id}</span>
              </div>
            )}
          </div>
        </div>

        {/* ── Right Column ── */}
        <div className="profile-right-col">
          {/* Onboarding Status — only for employees with a linked profile */}
          {profile && (
            <div className="profile-section glass-panel">
              <h3 className="section-title">Onboarding Status</h3>
              <div className="onboarding-checklist">
                <div className={`checklist-row ${profile.workEmail ? 'done' : 'pending'}`}>
                  <Mail size={16} />
                  <div>
                    <p className="check-label">Work Email</p>
                    <p className="check-val">{profile.workEmail || 'Not configured'}</p>
                  </div>
                  <span className="check-badge">{profile.workEmail ? 'Active' : 'Pending'}</span>
                </div>
                <div className={`checklist-row ${profile.slackInviteSent ? 'done' : 'pending'}`}>
                  <MessageSquare size={16} />
                  <div>
                    <p className="check-label">Slack Invite</p>
                    <p className="check-val">{profile.slackInviteSent ? 'Sent' : 'Not sent'}</p>
                  </div>
                  <span className="check-badge">{profile.slackInviteSent ? 'Done' : 'Pending'}</span>
                </div>
                <div className={`checklist-row ${profile.trainingAssigned ? 'done' : 'pending'}`}>
                  <GraduationCap size={16} />
                  <div>
                    <p className="check-label">Training</p>
                    <p className="check-val">{profile.trainingAssigned ? 'Curriculum assigned' : 'Not assigned'}</p>
                  </div>
                  <span className="check-badge">{profile.trainingAssigned ? 'Done' : 'Pending'}</span>
                </div>
                <div className={`checklist-row ${profile.payrollConfigured ? 'done' : 'pending'}`}>
                  <CreditCard size={16} />
                  <div>
                    <p className="check-label">Payroll Setup</p>
                    <p className="check-val">{profile.payrollConfigured ? 'Configured' : 'Not configured'}</p>
                  </div>
                  <span className="check-badge">{profile.payrollConfigured ? 'Done' : 'Pending'}</span>
                </div>
              </div>

              {profile.aiOnboardingMessage && (
                <div className="ai-welcome-block">
                  <div className="ai-welcome-header">
                    <Sparkles size={15} />
                    <span>AI Welcome Message</span>
                  </div>
                  <p className="ai-welcome-text">"{profile.aiOnboardingMessage}"</p>
                </div>
              )}
            </div>
          )}

          {/* Performance Ratings */}
          {isEmployee && (
            <div className="profile-section glass-panel">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem' }}>
                <h3 className="section-title" style={{ margin: 0 }}>My Performance Ratings</h3>
                {avgRating && (
                  <span className="avg-badge">
                    <Star size={13} fill="currentColor" />
                    {avgRating} avg
                  </span>
                )}
              </div>
              {ratings.length === 0 ? (
                <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>No ratings recorded yet.</p>
              ) : (
                <div className="ratings-list">
                  {ratings.map((r) => (
                    <div key={r.id} className="rating-row glass-panel">
                      <div className="rating-row-header">
                        <span className="rating-project">{r.projectName}</span>
                        <StarDisplay score={r.score} />
                      </div>
                      {r.feedback && (
                        <p className="rating-feedback">"{r.feedback}"</p>
                      )}
                      <p className="rating-meta">
                        Rated by <strong>{r.createdBy || 'admin'}</strong>
                        {r.createdAt && ` · ${new Date(r.createdAt).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' })}`}
                      </p>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          {/* For admin/HR who have no linked employee — show role summary */}
          {!isEmployee && !profile && (
            <div className="profile-section glass-panel">
              <h3 className="section-title">Account Summary</h3>
              <p style={{ color: 'var(--text-secondary)', fontSize: '0.95rem' }}>
                You are logged in as <strong>{roleBadge}</strong>. This role has elevated privileges to manage employees, onboard new hires, and view payroll data across the organisation.
              </p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Profile;
