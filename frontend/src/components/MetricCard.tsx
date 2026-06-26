import React from 'react';
import './MetricCard.css';

interface MetricCardProps {
  title: string;
  value: string | number;
  icon: React.ComponentType<{ size?: number; className?: string }>;
  trend?: number;
}

const MetricCard = ({ title, value, icon: Icon, trend }: MetricCardProps) => {
  return (
    <div className="metric-card glass-panel">
      <div className="metric-header">
        <h3 className="metric-title">{title}</h3>
        <div className="metric-icon-wrapper">
          <Icon size={20} className="metric-icon" />
        </div>
      </div>
      
      <div className="metric-content">
        <div className="metric-value">{value}</div>
        {trend !== undefined && (
          <div className={`metric-trend ${trend >= 0 ? 'positive' : 'negative'}`}>
            {trend >= 0 ? '+' : ''}{trend}% from last month
          </div>
        )}
      </div>
    </div>
  );
};

export default MetricCard;
