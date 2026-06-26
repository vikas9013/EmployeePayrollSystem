import axios, { InternalAxiosRequestConfig, AxiosResponse, AxiosError } from 'axios';

interface CustomAxiosRequestConfig extends InternalAxiosRequestConfig {
  _retry?: boolean;
}

const getBaseUrl = () => {
  const envUrl = import.meta.env.VITE_API_BASE_URL;
  if (envUrl) {
    // Suffix with /api if not already present
    return envUrl.endsWith('/api') ? envUrl : `${envUrl}/api`;
  }
  return 'http://localhost:8080/api';
};

const api = axios.create({
  baseURL: getBaseUrl(),
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor: Attach JWT Token
api.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = localStorage.getItem('token');
  if (token && config.headers) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response Interceptor: Handle 401 & Silent Refresh
api.interceptors.response.use(
  (response: AxiosResponse) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as CustomAxiosRequestConfig | undefined;
    
    // If error is 401 and we haven't retried yet
    if (error.response?.status === 401 && originalRequest && !originalRequest._retry) {
      originalRequest._retry = true;
      const refreshToken = localStorage.getItem('refreshToken');
      
      if (refreshToken) {
        try {
          // Attempt to refresh the token
          const refreshUrl = `${getBaseUrl()}/auth/refresh`;
          const { data } = await axios.post<{ token: string }>(refreshUrl, {
            refreshToken,
          });
          
          localStorage.setItem('token', data.token);
          // Set new token and retry the original request
          if (originalRequest.headers) {
            originalRequest.headers.Authorization = `Bearer ${data.token}`;
          }
          return api(originalRequest);
        } catch (refreshError) {
          // Refresh failed, force logout
          localStorage.removeItem('token');
          localStorage.removeItem('refreshToken');
          window.location.href = '/login';
        }
      } else {
        // No refresh token, force logout
        localStorage.removeItem('token');
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export default api;
