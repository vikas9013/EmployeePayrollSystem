interface SkeletonLoaderProps {
  count?: number;
  height?: string;
  width?: string;
  borderRadius?: string;
}

const SkeletonLoader = ({ count = 1, height = '40px', width = '100%', borderRadius = '8px' }: SkeletonLoaderProps) => {
  return (
    <div className="skeleton-container" style={{ gap: '1rem', display: 'flex', flexDirection: 'column' }}>
      {Array.from({ length: count }).map((_, index) => (
        <div 
          key={index} 
          className="skeleton" 
          style={{ height, width, borderRadius }}
        />
      ))}
    </div>
  );
};

export default SkeletonLoader;
