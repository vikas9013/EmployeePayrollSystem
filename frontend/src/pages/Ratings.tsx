import React, { useState, useEffect } from 'react';
import { useToast } from '../context/ToastContext';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';
import {
  Star,
  Plus,
  X,
  Search,
  ChevronLeft,
  ChevronRight,
  User,
  Briefcase,
  MessageSquare,
  Calendar,
  Shield
} from 'lucide-react';
import './Ratings.css';

interface RatingResponse {
  id: number;
  employeeId: number;
  projectName: string;
  score: number;
  feedback: string;
  createdAt: string;
  createdBy: string;
}

const StarRating = ({ score, size = 18 }: { score: number; size?: number }) => (
  <div className="star-display">
    {[1, 2, 3, 4, 5].map((s) => (
      <Star
        key={s}
        size={size}
        className={s <= score ? 'star-filled' : 'star-empty'}
        fill={s <= score ? 'currentColor' : 'none'}
      />
    ))}
  </div>
);

const InteractiveStars = ({
  value,
  onChange
}: {
  value: number;
  onChange: (v: number) => void;
}) => {
  const [hover, setHover] = useState(0);
  return (
    <div className="star-input">
      {[1, 2, 3, 4, 5].map((s) => (
        <button
          key={s}
          type="button"
          className={`star-btn ${s <= (hover || value) ? 'active' : ''}`}
          onMouseEnter={() => setHover(s)}
          onMouseLeave={() => setHover(0)}
          onClick={() => onChange(s)}
          aria-label={`Rate ${s} star${s > 1 ? 's' : ''}`}
        >
          <Star size={28} fill={s <= (hover || value) ? 'currentColor' : 'none'} />
        </button>
      ))}
      <span className="star-label">
        {(hover || value) > 0
          ? ['', 'Poor', 'Fair', 'Good', 'Great', 'Excellent'][hover || value]
          : 'Select a score'}
      </span>
    </div>
  );
};

const scoreColor = (s: number) =>
  s >= 5 ? '#22c55e' : s >= 4 ? '#84cc16' : s >= 3 ? '#eab308' : s >= 2 ? '#f97316' : '#ef4444';

const Ratings = () => {
  const { addToast } = useToast();
  const { user } = useAuth();
  const isAdmin = user?.roles?.includes('ROLE_ADMIN');
  const isManager = user?.roles?.includes('ROLE_MANAGER');
  const canAdd = isAdmin || isManager;

  const [ratings, setRatings] = useState<RatingResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const PAGE_SIZE = 8;

  // Add Rating Modal
  const [showModal, setShowModal] = useState(false);
  const [saving, setSaving] = useState(false);
  const [form, setForm] = useState({
    employeeId: '',
    projectName: '',
    score: 0,
    feedback: ''
  });

  const fetchRatings = async () => {
    setLoading(true);
    try {
      const res = await api.get<RatingResponse[]>('/ratings');
      setRatings(res.data);
    } catch (err: any) {
      addToast(err.response?.data?.detail || 'Failed to load ratings', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRatings();
  }, []);

  const filtered = ratings.filter(
    (r) =>
      r.projectName.toLowerCase().includes(search.toLowerCase()) ||
      String(r.employeeId).includes(search) ||
      (r.createdBy || '').toLowerCase().includes(search.toLowerCase())
  );

  const totalPages = Math.ceil(filtered.length / PAGE_SIZE);
  const paginated = filtered.slice(page * PAGE_SIZE, (page + 1) * PAGE_SIZE);

  const handleFormChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (form.score === 0) {
      addToast('Please select a star rating before submitting.', 'error');
      return;
    }
    setSaving(true);
    try {
      await api.post('/ratings', {
        employeeId: Number(form.employeeId),
        projectName: form.projectName,
        score: form.score,
        feedback: form.feedback
      });
      addToast('Rating submitted successfully!', 'success');
      setShowModal(false);
      setForm({ employeeId: '', projectName: '', score: 0, feedback: '' });
      fetchRatings();
    } catch (err: any) {
      const msg =
        err.response?.data?.detail ||
        err.response?.data?.error ||
        'Failed to submit rating';
      addToast(msg, 'error');
    } finally {
      setSaving(false);
    }
  };

  const avgScore =
    ratings.length > 0
      ? (ratings.reduce((sum, r) => sum + r.score, 0) / ratings.length).toFixed(1)
      : '—';

  const dist = [5, 4, 3, 2, 1].map((s) => ({
    score: s,
    count: ratings.filter((r) => r.score === s).length,
    pct: ratings.length > 0 ? (ratings.filter((r) => r.score === s).length / ratings.length) * 100 : 0
  }));

  return (
    <div className="ratings-container">
      {/* ── Header ── */}
      <div className="ratings-header-row">
        <div>
          <h1>Project Ratings</h1>
          <p className="ratings-subtitle">
            Track employee performance scores across all projects
          </p>
        </div>
        {canAdd && (
          <button
            id="add-rating-btn"
            className="btn-primary"
            onClick={() => setShowModal(true)}
          >
            <Plus size={16} />
            <span>Add Rating</span>
          </button>
        )}
      </div>

      {/* ── Stats Row ── */}
      <div className="ratings-stats-row">
        <div className="stat-card glass-panel">
          <Star size={22} className="stat-icon" fill="currentColor" />
          <div>
            <p className="stat-value">{avgScore}</p>
            <p className="stat-label">Avg Score</p>
          </div>
        </div>
        <div className="stat-card glass-panel">
          <Briefcase size={22} className="stat-icon" />
          <div>
            <p className="stat-value">{ratings.length}</p>
            <p className="stat-label">Total Ratings</p>
          </div>
        </div>
        <div className="stat-card glass-panel">
          <Shield size={22} className="stat-icon" />
          <div>
            <p className="stat-value">
              {new Set(ratings.map((r) => r.employeeId)).size}
            </p>
            <p className="stat-label">Employees Rated</p>
          </div>
        </div>

        {/* Score distribution */}
        <div className="dist-card glass-panel">
          <p className="dist-title">Score Distribution</p>
          {dist.map((d) => (
            <div key={d.score} className="dist-row">
              <span className="dist-label">{d.score}★</span>
              <div className="dist-bar-bg">
                <div
                  className="dist-bar-fill"
                  style={{
                    width: `${d.pct}%`,
                    background: scoreColor(d.score)
                  }}
                />
              </div>
              <span className="dist-count">{d.count}</span>
            </div>
          ))}
        </div>
      </div>

      {/* ── Search + Table ── */}
      <div className="ratings-table-section">
        <div className="table-header-row">
          <h2 style={{ fontSize: '1.1rem', fontWeight: 600 }}>All Ratings</h2>
          <div className="search-box">
            <Search size={16} style={{ position: 'absolute', left: '1rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)', pointerEvents: 'none' }} />
            <input
              id="ratings-search"
              type="text"
              className="form-control"
              placeholder="Search project or employee..."
              style={{ paddingLeft: '2.5rem' }}
              value={search}
              onChange={(e) => { setSearch(e.target.value); setPage(0); }}
            />
          </div>
        </div>

        <div className="table-card glass-panel">
          {loading ? (
            <div className="table-empty">Loading ratings...</div>
          ) : paginated.length === 0 ? (
            <div className="table-empty">
              {ratings.length === 0
                ? 'No ratings recorded yet.'
                : 'No results match your search.'}
            </div>
          ) : (
            <table className="employee-table">
              <thead>
                <tr>
                  <th>#</th>
                  <th>Employee ID</th>
                  <th>Project</th>
                  <th>Score</th>
                  <th>Feedback</th>
                  <th>Rated By</th>
                  <th>Date</th>
                </tr>
              </thead>
              <tbody>
                {paginated.map((r) => (
                  <tr key={r.id}>
                    <td style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>#{r.id}</td>
                    <td>
                      <span className="emp-id-badge">EMP-{r.employeeId}</span>
                    </td>
                    <td style={{ fontWeight: 500 }}>{r.projectName}</td>
                    <td>
                      <div className="score-cell">
                        <StarRating score={r.score} size={14} />
                        <span
                          className="score-pill"
                          style={{ background: `${scoreColor(r.score)}22`, color: scoreColor(r.score), borderColor: `${scoreColor(r.score)}44` }}
                        >
                          {r.score}/5
                        </span>
                      </div>
                    </td>
                    <td>
                      <span className="feedback-cell">
                        {r.feedback || <em style={{ color: 'var(--text-muted)' }}>No feedback</em>}
                      </span>
                    </td>
                    <td>
                      <div className="rated-by-cell">
                        <User size={13} />
                        <span>{r.createdBy || 'system'}</span>
                      </div>
                    </td>
                    <td style={{ fontSize: '0.82rem', color: 'var(--text-secondary)' }}>
                      {r.createdAt
                        ? new Date(r.createdAt).toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' })
                        : '—'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>

        {!loading && totalPages > 1 && (
          <div className="pagination-container">
            <span className="pagination-stats">
              Page {page + 1} of {totalPages} ({filtered.length} ratings)
            </span>
            <div className="pagination-buttons">
              <button
                className="pag-btn"
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                disabled={page === 0}
              >
                <ChevronLeft size={16} />
              </button>
              <button
                className="pag-btn"
                onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                disabled={page === totalPages - 1}
              >
                <ChevronRight size={16} />
              </button>
            </div>
          </div>
        )}
      </div>

      {/* ── Add Rating Modal ── */}
      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div
            className="modal-card glass-panel rating-modal"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="modal-header">
              <div>
                <h2>Add Project Rating</h2>
                <p style={{ color: 'var(--text-secondary)', fontSize: '0.875rem', marginTop: '0.25rem' }}>
                  Rate an employee's performance on a project
                </p>
              </div>
              <button className="modal-close-btn" onClick={() => setShowModal(false)}>
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleSubmit}>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
                <div className="form-row-2">
                  <div className="form-group">
                    <label>
                      <User size={14} style={{ marginRight: '0.4rem', verticalAlign: 'middle' }} />
                      Employee ID
                    </label>
                    <input
                      id="rating-employee-id"
                      type="number"
                      name="employeeId"
                      className="form-control"
                      placeholder="e.g. 3"
                      min="1"
                      value={form.employeeId}
                      onChange={handleFormChange}
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label>
                      <Briefcase size={14} style={{ marginRight: '0.4rem', verticalAlign: 'middle' }} />
                      Project Name
                    </label>
                    <input
                      id="rating-project-name"
                      type="text"
                      name="projectName"
                      className="form-control"
                      placeholder="e.g. Payroll Modernization"
                      value={form.projectName}
                      onChange={handleFormChange}
                      required
                    />
                  </div>
                </div>

                <div className="form-group">
                  <label>
                    <Star size={14} style={{ marginRight: '0.4rem', verticalAlign: 'middle' }} />
                    Performance Score
                  </label>
                  <InteractiveStars
                    value={form.score}
                    onChange={(v) => setForm((prev) => ({ ...prev, score: v }))}
                  />
                </div>

                <div className="form-group">
                  <label>
                    <MessageSquare size={14} style={{ marginRight: '0.4rem', verticalAlign: 'middle' }} />
                    Feedback <span style={{ color: 'var(--text-muted)', fontWeight: 400 }}>(optional)</span>
                  </label>
                  <textarea
                    id="rating-feedback"
                    name="feedback"
                    className="form-control"
                    placeholder="Share detailed feedback about the employee's performance..."
                    rows={4}
                    value={form.feedback}
                    onChange={handleFormChange}
                    style={{ resize: 'vertical', minHeight: '90px' }}
                  />
                </div>
              </div>

              <div className="modal-footer">
                <button
                  type="button"
                  className="back-btn"
                  onClick={() => setShowModal(false)}
                >
                  Cancel
                </button>
                <button
                  id="submit-rating-btn"
                  type="submit"
                  className="btn-primary"
                  disabled={saving}
                >
                  {saving ? 'Submitting...' : 'Submit Rating'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Ratings;
