import { useState, useEffect } from 'react';
import MetricCard from '../components/MetricCard';
import SkeletonLoader from '../components/SkeletonLoader';
import { Users, Banknote, Star } from 'lucide-react';
import api from '../services/api';

interface Employee {
  salary?: number;       // EmployeeResponseDTO field
  type?: string;
}

interface PageResponse {
  content: Employee[];
  totalElements: number;
}

interface Rating {
  score: number;
}

interface DashboardMetrics {
  totalEmployees: number;
  totalPayroll: string;
  averageRating: number | string;
}

const Dashboard = () => {
  const [loading, setLoading] = useState<boolean>(true);
  const [metrics, setMetrics] = useState<DashboardMetrics>({
    totalEmployees: 0,
    totalPayroll: '₹0.00',
    averageRating: 0
  });

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        // allSettled so one failed request doesn't wipe out the others
        const [empResult, ratingResult] = await Promise.allSettled([
          api.get<PageResponse>('/employees?page=0&size=200'),
          api.get<Rating[]>('/ratings')
        ]);

        let totalEmployees = 0;
        let totalPayrollVal = 0;

        if (empResult.status === 'fulfilled') {
          const pageData = empResult.value.data;
          const employees: Employee[] = pageData.content || [];
          totalEmployees = pageData.totalElements ?? employees.length;
          
          totalPayrollVal = employees.reduce((acc, emp) => {
            const isPart = emp.type === 'PartTimeEmployee' || emp.type === 'PARTTIME';
            const monthlyCont = isPart ? (emp.salary || 0) : ((emp.salary || 0) / 12);
            return acc + monthlyCont;
          }, 0);
        }

        let avgRatingVal: string | number = '—';
        if (ratingResult.status === 'fulfilled') {
          const ratings: Rating[] = ratingResult.value.data || [];
          avgRatingVal = ratings.length > 0
            ? (ratings.reduce((acc, r) => acc + r.score, 0) / ratings.length).toFixed(1)
            : '—';
        }

        setMetrics({
          totalEmployees,
          totalPayroll: `₹${Math.round(totalPayrollVal).toLocaleString('en-IN')}`,
          averageRating: avgRatingVal
        });
      } catch (error) {
        console.error('Failed to load dashboard data', error);
      } finally {
        setLoading(false);
      }
    };

    fetchDashboardData();
  }, []);



  if (loading) {
    return (
      <div className="dashboard-content">
        <h1 style={{ marginBottom: '2rem' }}>Dashboard Overview</h1>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '1.5rem' }}>
          <SkeletonLoader height="120px" />
          <SkeletonLoader height="120px" />
          <SkeletonLoader height="120px" />
        </div>
      </div>
    );
  }

  return (
    <div className="dashboard-content">
      <h1 style={{ marginBottom: '2rem' }}>Dashboard Overview</h1>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '1.5rem' }}>
        <MetricCard 
          title="Total Employees" 
          value={metrics.totalEmployees} 
          icon={Users} 
          trend={5} 
        />
        <MetricCard 
          title="Monthly Payroll" 
          value={metrics.totalPayroll} 
          icon={Banknote} 
          trend={12} 
        />
        <MetricCard 
          title="Average Performance" 
          value={`${metrics.averageRating} / 5.0`} 
          icon={Star} 
        />
      </div>
    </div>
  );
};

export default Dashboard;
