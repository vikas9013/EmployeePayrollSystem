import React, { useState, useEffect } from 'react';
import { useToast } from '../context/ToastContext';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';
import { 
  Search, 
  Eye, 
  Edit, 
  Trash2, 
  X, 
  ChevronLeft, 
  ChevronRight, 
  ArrowUpDown,
  Mail,
  MessageSquare,
  GraduationCap,
  CreditCard,
  Sparkles
} from 'lucide-react';
import './Employees.css';

interface Employee {
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

interface PageResponse {
  content: Employee[];
  totalPages: number;
  totalElements: number;
  number: number;
  size: number;
}

const Employees = () => {
  const { addToast } = useToast();
  const { user } = useAuth();
  const isAdmin = user?.roles?.includes('ROLE_ADMIN');
  const isHr = user?.roles?.includes('ROLE_HR');
  const canEdit = isAdmin || isHr;
  const canDelete = isAdmin;

  // Table State
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [page, setPage] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(0);
  const [totalElements, setTotalElements] = useState<number>(0);
  const [search, setSearch] = useState<string>('');
  
  // Sort State
  const [sortBy, setSortBy] = useState<string>('id');
  const [direction, setDirection] = useState<'asc' | 'desc'>('asc');

  // Modal State
  const [selectedEmployee, setSelectedEmployee] = useState<Employee | null>(null);
  const [editEmployee, setEditEmployee] = useState<Employee | null>(null);
  const [editForm, setEditForm] = useState({
    name: '',
    designation: '',
    type: 'FULLTIME',
    monthlySalary: 0,
    hourlyRate: 0,
    hoursWorked: 0
  });

  const [loading, setLoading] = useState<boolean>(true);
  const [saving, setSaving] = useState<boolean>(false);

  const fetchEmployees = async () => {
    setLoading(true);
    try {
      const response = await api.get<PageResponse>('/employees', {
        params: {
          page,
          size: 10,
          sortBy,
          direction
        }
      });
      setEmployees(response.data.content);
      setTotalPages(response.data.totalPages);
      setTotalElements(response.data.totalElements);
    } catch (error) {
      console.error(error);
      addToast('Failed to fetch employees list', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchEmployees();
  }, [page, sortBy, direction]);

  const handleSort = (field: string) => {
    if (sortBy === field) {
      setDirection((prev) => (prev === 'asc' ? 'desc' : 'asc'));
    } else {
      setSortBy(field);
      setDirection('asc');
    }
    setPage(0);
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Are you sure you want to remove (soft-delete) this employee?')) return;
    try {
      await api.delete(`/employees/${id}`);
      addToast('Employee successfully removed (soft-deleted).', 'success');
      fetchEmployees();
    } catch (error: any) {
      console.error(error);
      const errMsg = error.response?.data?.error || 'Failed to remove employee';
      addToast(errMsg, 'error');
    }
  };

  const openEditModal = (emp: Employee) => {
    setEditEmployee(emp);
    setEditForm({
      name: emp.name,
      designation: emp.designation,
      type: emp.type === 'PartTimeEmployee' || emp.type === 'PARTTIME' ? 'PARTTIME' : 'FULLTIME',
      monthlySalary: emp.type === 'FullTimeEmployee' || emp.type === 'FULLTIME' ? emp.salary : 0,
      hourlyRate: emp.type === 'PartTimeEmployee' || emp.type === 'PARTTIME' ? emp.salary : 0, // In entity, salary holds calculated base or rate
      hoursWorked: 0
    });
  };

  const handleEditChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setEditForm((prev) => ({
      ...prev,
      [name]: name === 'monthlySalary' || name === 'hourlyRate' || name === 'hoursWorked'
        ? Number(value)
        : value
    }));
  };

  const handleEditSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!editEmployee) return;
    setSaving(true);

    const payload = {
      name: editForm.name,
      designation: editForm.designation,
      type: editForm.type,
      ...(editForm.type === 'FULLTIME'
        ? { monthlySalary: editForm.monthlySalary }
        : { hourlyRate: editForm.hourlyRate, hoursWorked: editForm.hoursWorked }
      )
    };

    try {
      await api.put(`/employees/${editEmployee.id}`, payload);
      addToast('Employee updated successfully!', 'success');
      setEditEmployee(null);
      fetchEmployees();
    } catch (error: any) {
      console.error(error);
      const errMsg = error.response?.data?.error || 'Failed to update employee';
      addToast(errMsg, 'error');
    } finally {
      setSaving(false);
    }
  };

  // Local filtering on name search
  const filteredEmployees = employees.filter((emp) => 
    emp.name.toLowerCase().includes(search.toLowerCase()) ||
    emp.designation.toLowerCase().includes(search.toLowerCase()) ||
    emp.workEmail.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div className="employees-container">
      <div className="table-header-row">
        <div>
          <h1>Employee Directory</h1>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
            Browse, manage, and update employee details and onboarding workflows.
          </p>
        </div>

        {/* Search */}
        <div className="search-box">
          <input 
            type="text"
            className="form-control"
            placeholder="Search directory..."
            style={{ paddingLeft: '2.5rem' }}
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
          <Search size={16} style={{ position: 'absolute', left: '1rem', top: '1rem', color: 'var(--text-muted)' }} />
        </div>
      </div>

      <div className="table-card glass-panel">
        {loading ? (
          <div style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-secondary)' }}>
            Retrieving database directory records...
          </div>
        ) : filteredEmployees.length === 0 ? (
          <div style={{ padding: '3rem', textAlign: 'center', color: 'var(--text-secondary)' }}>
            No employee records found in the directory.
          </div>
        ) : (
          <table className="employee-table">
            <thead>
              <tr>
                <th onClick={() => handleSort('id')}>ID <ArrowUpDown size={12} /></th>
                <th onClick={() => handleSort('name')}>Name <ArrowUpDown size={12} /></th>
                <th onClick={() => handleSort('designation')}>Designation <ArrowUpDown size={12} /></th>
                <th>Email</th>
                <th>Type</th>
                <th>Salary</th>
                <th style={{ textAlign: 'right' }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filteredEmployees.map((emp) => {
                const isPartTime = emp.type === 'PartTimeEmployee' || emp.type === 'PARTTIME';
                return (
                  <tr key={emp.id}>
                    <td>#{emp.id}</td>
                    <td style={{ fontWeight: '500' }}>{emp.name}</td>
                    <td>{emp.designation}</td>
                    <td>{emp.workEmail || 'N/A'}</td>
                    <td>
                      <span className={`type-badge ${isPartTime ? 'parttime' : 'fulltime'}`}>
                        {isPartTime ? 'Part-Time' : 'Full-Time'}
                      </span>
                    </td>
                    <td>
                      ₹{Math.round(isPartTime ? emp.salary : (emp.salary / 12)).toLocaleString('en-IN')}
                    </td>
                    <td>
                      <div className="action-buttons" style={{ justifyContent: 'flex-end' }}>
                        <button className="action-btn" onClick={() => setSelectedEmployee(emp)}>
                          <Eye size={14} />
                          <span>View</span>
                        </button>
                        {canEdit && (
                          <button className="action-btn edit" onClick={() => openEditModal(emp)}>
                            <Edit size={14} />
                            <span>Edit</span>
                          </button>
                        )}
                        {canDelete && (
                          <button className="action-btn delete" onClick={() => handleDelete(emp.id)}>
                            <Trash2 size={14} />
                            <span>Delete</span>
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        )}
      </div>

      {/* Pagination */}
      {!loading && totalPages > 1 && (
        <div className="pagination-container">
          <span className="pagination-stats">
            Showing Page {page + 1} of {totalPages} ({totalElements} total records)
          </span>
          <div className="pagination-buttons">
            <button 
              className="pag-btn" 
              onClick={() => setPage((prev) => Math.max(0, prev - 1))}
              disabled={page === 0}
            >
              <ChevronLeft size={16} />
            </button>
            <button 
              className="pag-btn" 
              onClick={() => setPage((prev) => Math.min(totalPages - 1, prev + 1))}
              disabled={page === totalPages - 1}
            >
              <ChevronRight size={16} />
            </button>
          </div>
        </div>
      )}

      {/* 1. View Details Modal */}
      {selectedEmployee && (
        <div className="modal-overlay" onClick={() => setSelectedEmployee(null)}>
          <div className="modal-card glass-panel" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Employee Details</h2>
              <button className="modal-close-btn" onClick={() => setSelectedEmployee(null)}>
                <X size={20} />
              </button>
            </div>
            
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem', marginBottom: '2rem' }}>
              <div className="detail-row">
                <span className="detail-label">Employee ID</span>
                <span className="detail-value">#{selectedEmployee.id}</span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Name</span>
                <span className="detail-value">{selectedEmployee.name}</span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Designation</span>
                <span className="detail-value">{selectedEmployee.designation}</span>
              </div>
              <div className="detail-row">
                <span className="detail-label">Employment Type</span>
                <span className="detail-value">
                  {selectedEmployee.type === 'PartTimeEmployee' || selectedEmployee.type === 'PARTTIME' ? 'Part-Time' : 'Full-Time'}
                </span>
              </div>
              <div className="detail-row" style={{ gridColumn: 'span 2' }}>
                <span className="detail-label">Work Email</span>
                <span className="detail-value">{selectedEmployee.workEmail || 'N/A'}</span>
              </div>
            </div>

            {/* Checklists */}
            <h3 style={{ marginBottom: '1rem', fontSize: '1.1rem' }}>Onboarding Pipeline Verification</h3>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem', marginBottom: '2rem' }}>
              <div className="checklist-item" style={{ borderColor: 'transparent' }}>
                <Mail className="icon-success" size={18} />
                <div className="checklist-text">
                  <h4>Email Account</h4>
                  <p>{selectedEmployee.workEmail ? 'Active' : 'Unconfigured'}</p>
                </div>
              </div>
              <div className="checklist-item" style={{ borderColor: 'transparent' }}>
                <MessageSquare className={selectedEmployee.slackInviteSent ? 'icon-success' : 'icon-pending'} size={18} />
                <div className="checklist-text">
                  <h4>Slack Invite</h4>
                  <p>{selectedEmployee.slackInviteSent ? 'Sent' : 'Pending'}</p>
                </div>
              </div>
              <div className="checklist-item" style={{ borderColor: 'transparent' }}>
                <GraduationCap className={selectedEmployee.trainingAssigned ? 'icon-success' : 'icon-pending'} size={18} />
                <div className="checklist-text">
                  <h4>Training Track</h4>
                  <p>{selectedEmployee.trainingAssigned ? 'Assigned' : 'Pending'}</p>
                </div>
              </div>
              <div className="checklist-item" style={{ borderColor: 'transparent' }}>
                <CreditCard className={selectedEmployee.payrollConfigured ? 'icon-success' : 'icon-pending'} size={18} />
                <div className="checklist-text">
                  <h4>Bank Payroll</h4>
                  <p>{selectedEmployee.payrollConfigured ? 'Configured' : 'Pending'}</p>
                </div>
              </div>
            </div>

            {/* AI Welcome Message */}
            {selectedEmployee.aiOnboardingMessage && (
              <div className="ai-message-card glass-panel" style={{ padding: '1.2rem', marginBottom: '0.5rem' }}>
                <div className="ai-header" style={{ fontSize: '0.95rem' }}>
                  <Sparkles size={16} />
                  <span>AI Welcome Message</span>
                </div>
                <div className="ai-body" style={{ fontSize: '0.85rem' }}>
                  "{selectedEmployee.aiOnboardingMessage}"
                </div>
              </div>
            )}
          </div>
        </div>
      )}

      {/* 2. Edit Employee Modal */}
      {editEmployee && (
        <div className="modal-overlay" onClick={() => setEditEmployee(null)}>
          <div className="modal-card glass-panel" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Edit Employee Profile</h2>
              <button className="modal-close-btn" onClick={() => setEditEmployee(null)}>
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleEditSubmit}>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '1.2rem' }}>
                <div className="form-group">
                  <label>Full Name</label>
                  <input 
                    type="text"
                    name="name"
                    className="form-control"
                    value={editForm.name}
                    onChange={handleEditChange}
                    required
                  />
                </div>

                <div className="form-group">
                  <label>Designation</label>
                  <input 
                    type="text"
                    name="designation"
                    className="form-control"
                    value={editForm.designation}
                    onChange={handleEditChange}
                    required
                  />
                </div>

                <div className="form-group">
                  <label>Employment Type</label>
                  <select 
                    name="type"
                    className="form-control select-control"
                    value={editForm.type}
                    onChange={handleEditChange}
                  >
                    <option value="FULLTIME">Full-Time Employee</option>
                    <option value="PARTTIME">Part-Time Employee</option>
                  </select>
                </div>

                {editForm.type === 'FULLTIME' ? (
                  <div className="form-group">
                    <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
                      <label style={{ margin: 0 }}>Annual Salary (INR)</label>
                      {editForm.monthlySalary > 0 && (
                        <span style={{ color: 'var(--accent-color)', fontSize: '0.85rem', fontWeight: '500' }}>
                          Monthly: ₹{Math.round(editForm.monthlySalary / 12).toLocaleString('en-IN')}
                        </span>
                      )}
                    </div>
                    <input 
                      type="number"
                      name="monthlySalary"
                      className="form-control"
                      value={editForm.monthlySalary}
                      onChange={handleEditChange}
                      required
                    />
                  </div>
                ) : (
                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                    <div className="form-group">
                      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
                        <label style={{ margin: 0 }}>Hourly Rate (INR)</label>
                        {editForm.hourlyRate > 0 && editForm.hoursWorked > 0 && (
                          <span style={{ color: 'var(--accent-color)', fontSize: '0.85rem', fontWeight: '500' }}>
                            Monthly: ₹{(editForm.hourlyRate * editForm.hoursWorked).toLocaleString('en-IN')}
                          </span>
                        )}
                      </div>
                      <input 
                        type="number"
                        name="hourlyRate"
                        className="form-control"
                        value={editForm.hourlyRate}
                        onChange={handleEditChange}
                        required
                      />
                    </div>
                    <div className="form-group">
                      <label>Hours Worked</label>
                      <input 
                        type="number"
                        name="hoursWorked"
                        className="form-control"
                        value={editForm.hoursWorked}
                        onChange={handleEditChange}
                        required
                      />
                    </div>
                  </div>
                )}
              </div>

              <div className="modal-footer">
                <button type="button" className="back-btn" onClick={() => setEditEmployee(null)}>
                  Cancel
                </button>
                <button type="submit" className="btn-primary" disabled={saving}>
                  {saving ? 'Saving Changes...' : 'Save Changes'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Employees;
