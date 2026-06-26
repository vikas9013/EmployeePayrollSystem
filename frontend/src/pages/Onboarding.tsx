import React, { useState } from 'react';
import { useToast } from '../context/ToastContext';
import api from '../services/api';
import { 
  Sparkles, 
  CheckCircle2, 
  ArrowLeft, 
  Mail, 
  MessageSquare, 
  GraduationCap, 
  CreditCard, 
  Briefcase, 
  User 
} from 'lucide-react';
import './Onboarding.css';

interface OnboardRequest {
  name: string;
  designation: string;
  type: 'FULLTIME' | 'PARTTIME';
  monthlySalary: number;
  hourlyRate: number;
  hoursWorked: number;
}

interface OnboardResponse {
  employeeId: number;
  employeeName: string;
  workEmail: string;
  slackInviteSent: boolean;
  trainingAssigned: boolean;
  payrollConfigured: boolean;
  message: string;
  aiOnboardingMessage: string;
}

const Onboarding = () => {
  const { addToast } = useToast();
  const [formData, setFormData] = useState<OnboardRequest>({
    name: '',
    designation: '',
    type: 'FULLTIME',
    monthlySalary: 0,
    hourlyRate: 0,
    hoursWorked: 0
  });

  const [loading, setLoading] = useState<boolean>(false);
  const [result, setResult] = useState<OnboardResponse | null>(null);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: name === 'monthlySalary' || name === 'hourlyRate' || name === 'hoursWorked'
        ? Number(value)
        : value
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setResult(null);

    // Build payload according to employee type
    const payload = {
      name: formData.name,
      designation: formData.designation,
      type: formData.type,
      ...(formData.type === 'FULLTIME'
        ? { monthlySalary: formData.monthlySalary }
        : { hourlyRate: formData.hourlyRate, hoursWorked: formData.hoursWorked }
      )
    };

    try {
      const response = await api.post<OnboardResponse>('/employees/onboard', payload);
      const initialData = response.data;

      // Poll the backend for up to 5 seconds to wait for async pipeline to complete
      let attempts = 0;
      
      const pollPipeline = async (): Promise<OnboardResponse> => {
        try {
          const res = await api.get<any>(`/employees/${initialData.employeeId}`);
          if (res.data.aiOnboardingMessage || res.data.workEmail || res.data.slackInviteSent) {
            return {
              employeeId: res.data.id,
              employeeName: res.data.name,
              workEmail: res.data.workEmail,
              slackInviteSent: res.data.slackInviteSent,
              trainingAssigned: res.data.trainingAssigned,
              payrollConfigured: res.data.payrollConfigured,
              message: 'Onboarding completed successfully',
              aiOnboardingMessage: res.data.aiOnboardingMessage
            };
          }
        } catch (e) {
          console.error("Polling check failed", e);
        }
        
        attempts++;
        if (attempts < 6) {
          await new Promise((resolve) => setTimeout(resolve, 1000));
          return pollPipeline();
        }
        return initialData;
      };

      const finalResult = await pollPipeline();
      setResult(finalResult);
      addToast('Employee onboarded successfully!', 'success');
    } catch (error: any) {
      console.error(error);
      const errMsg = error.response?.data?.error || 'Failed to onboard employee. Try again.';
      addToast(errMsg, 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setFormData({
      name: '',
      designation: '',
      type: 'FULLTIME',
      monthlySalary: 0,
      hourlyRate: 0,
      hoursWorked: 0
    });
    setResult(null);
  };

  if (loading) {
    return (
      <div className="onboarding-container">
        <div className="onboarding-card glass-panel pipeline-loader">
          <div className="spinner"></div>
          <h2 className="pipeline-status">Executing AI Onboarding Pipeline...</h2>
          <p className="pipeline-step">Orchestrating credentials, training assignments, and generating welcome greetings via LLaMA 3.3...</p>
        </div>
      </div>
    );
  }

  if (result) {
    return (
      <div className="onboarding-container">
        <div className="results-header">
          <button className="back-btn" onClick={handleReset}>
            <ArrowLeft size={16} />
            <span>Onboard Another</span>
          </button>
          <span className="success-badge">Completed</span>
        </div>

        <h1 style={{ marginBottom: '1.5rem' }}>Onboarding Complete</h1>

        <div className="results-grid">
          {/* Employee Details Column */}
          <div className="employee-details-card glass-panel">
            <div className="detail-row">
              <span className="detail-label">Employee ID</span>
              <span className="detail-value">#{result.employeeId}</span>
            </div>
            <div className="detail-row">
              <span className="detail-label">Full Name</span>
              <span className="detail-value">{result.employeeName}</span>
            </div>
            <div className="detail-row">
              <span className="detail-label">Designation</span>
              <span className="detail-value">{formData.designation}</span>
            </div>
            <div className="detail-row">
              <span className="detail-label">Contract Type</span>
              <span className="detail-value">{formData.type === 'FULLTIME' ? 'Full-Time' : 'Part-Time'}</span>
            </div>
            {formData.type === 'FULLTIME' ? (
              <>
                <div className="detail-row">
                  <span className="detail-label">Annual Salary</span>
                  <span className="detail-value">₹{formData.monthlySalary.toLocaleString('en-IN')}</span>
                </div>
                <div className="detail-row">
                  <span className="detail-label">Monthly Salary</span>
                  <span className="detail-value" style={{ color: 'var(--accent-color)', fontWeight: 'bold' }}>
                    ₹{Math.round(formData.monthlySalary / 12).toLocaleString('en-IN')}
                  </span>
                </div>
              </>
            ) : (
              <>
                <div className="detail-row">
                  <span className="detail-label">Hourly Rate</span>
                  <span className="detail-value">₹{formData.hourlyRate}/hr</span>
                </div>
                <div className="detail-row">
                  <span className="detail-label">Hours Worked</span>
                  <span className="detail-value">{formData.hoursWorked} hrs</span>
                </div>
                <div className="detail-row">
                  <span className="detail-label">Est. Annual (LPA)</span>
                  <span className="detail-value" style={{ color: 'var(--accent-color)', fontWeight: 'bold' }}>
                    ₹{(((formData.hourlyRate * formData.hoursWorked) * 12) / 100000).toFixed(2)} LPA
                  </span>
                </div>
              </>
            )}
          </div>

          {/* Pipeline Checklist Status Column */}
          <div className="pipeline-checklist-card">
            <h3>Onboarding Pipeline Status</h3>
            <div className="pipeline-checklist" style={{ marginTop: '1rem' }}>
              <div className="checklist-item active">
                <Mail className="icon-success" size={20} />
                <div className="checklist-text">
                  <h4>Work Email Generated</h4>
                  <p>{result.workEmail}</p>
                </div>
              </div>

              <div className="checklist-item active">
                <MessageSquare className={result.slackInviteSent ? 'icon-success' : 'icon-pending'} size={20} />
                <div className="checklist-text">
                  <h4>Slack Workspace Invitation</h4>
                  <p>{result.slackInviteSent ? 'Invite sent successfully' : 'Pending/Failed'}</p>
                </div>
              </div>

              <div className="checklist-item active">
                <GraduationCap className={result.trainingAssigned ? 'icon-success' : 'icon-pending'} size={20} />
                <div className="checklist-text">
                  <h4>Training Modules Assignment</h4>
                  <p>{result.trainingAssigned ? 'Curriculum configured dynamically' : 'Pending/Failed'}</p>
                </div>
              </div>

              <div className="checklist-item active">
                <CreditCard className={result.payrollConfigured ? 'icon-success' : 'icon-pending'} size={20} />
                <div className="checklist-text">
                  <h4>Bank Payroll Setup</h4>
                  <p>{result.payrollConfigured ? 'Accounts generated successfully' : 'Pending/Failed'}</p>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* AI Greetings Block */}
        {result.aiOnboardingMessage && (
          <div className="ai-message-card glass-panel">
            <div className="ai-header">
              <Sparkles size={18} />
              <span>AI Welcome Message (LLaMA 3.3)</span>
            </div>
            <div className="ai-body">
              "{result.aiOnboardingMessage}"
            </div>
          </div>
        )}
      </div>
    );
  }

  return (
    <div className="onboarding-container">
      <h1 className="onboarding-title">Employee Onboarding</h1>
      <p className="onboarding-subtitle">Hire a new team member and initialize the automated workflow credentials</p>

      <div className="onboarding-card glass-panel">
        <form onSubmit={handleSubmit}>
          <div className="form-grid">
            {/* Full Name */}
            <div className="form-group">
              <label>Full Name</label>
              <div style={{ position: 'relative' }}>
                <input 
                  type="text" 
                  name="name"
                  className="form-control"
                  placeholder="e.g. Vikas Kumar"
                  value={formData.name}
                  onChange={handleChange}
                  required 
                />
              </div>
            </div>

            {/* Designation */}
            <div className="form-group">
              <label>Designation</label>
              <input 
                type="text" 
                name="designation"
                className="form-control"
                placeholder="e.g. Senior Java Developer"
                value={formData.designation}
                onChange={handleChange}
                required 
              />
            </div>

            {/* Employment Type */}
            <div className="form-group">
              <label>Employment Type</label>
              <select 
                name="type"
                className="form-control select-control"
                value={formData.type}
                onChange={handleChange}
              >
                <option value="FULLTIME">Full-Time Employee</option>
                <option value="PARTTIME">Part-Time Employee</option>
              </select>
            </div>

            {/* Conditional Fields based on Type */}
            {formData.type === 'FULLTIME' ? (
              <div className="form-group">
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
                  <label style={{ margin: 0 }}>Annual Salary (INR)</label>
                  {formData.monthlySalary > 0 && (
                    <span style={{ color: 'var(--accent-color)', fontSize: '0.85rem', fontWeight: '500' }}>
                      Monthly: ₹{Math.round(formData.monthlySalary / 12).toLocaleString('en-IN')}
                    </span>
                  )}
                </div>
                <input 
                  type="number" 
                  name="monthlySalary"
                  className="form-control"
                  placeholder="e.g. 720000"
                  min="0"
                  value={formData.monthlySalary || ''}
                  onChange={handleChange}
                  required 
                />
              </div>
            ) : (
              <>
                <div className="form-group">
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '0.5rem' }}>
                    <label style={{ margin: 0 }}>Hourly Rate (INR)</label>
                    {formData.hourlyRate > 0 && formData.hoursWorked > 0 && (
                      <span style={{ color: 'var(--accent-color)', fontSize: '0.85rem', fontWeight: '500' }}>
                        Monthly: ₹{(formData.hourlyRate * formData.hoursWorked).toLocaleString('en-IN')}
                      </span>
                    )}
                  </div>
                  <input 
                    type="number" 
                    name="hourlyRate"
                    className="form-control"
                    placeholder="e.g. 500"
                    min="0"
                    value={formData.hourlyRate || ''}
                    onChange={handleChange}
                    required 
                  />
                </div>
                <div className="form-group">
                  <label>Hours Worked</label>
                  <input 
                    type="number" 
                    name="hoursWorked"
                    className="form-control"
                    placeholder="e.g. 160"
                    min="0"
                    value={formData.hoursWorked || ''}
                    onChange={handleChange}
                    required 
                  />
                </div>
              </>
            )}
          </div>

          <button type="submit" className="btn-primary onboard-submit-btn">
            <Sparkles size={18} />
            <span>Onboard Employee</span>
          </button>
        </form>
      </div>
    </div>
  );
};

export default Onboarding;
