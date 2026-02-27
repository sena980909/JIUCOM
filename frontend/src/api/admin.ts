import api from './client';

export interface DashboardResponse {
  totalUsers: number;
  todaySignups: number;
  totalBuilds: number;
  totalParts: number;
  totalPosts: number;
  recentUsers: {
    id: number;
    email: string;
    nickname: string;
    createdAt: string;
  }[];
}

export interface AdminUser {
  id: number;
  email: string;
  nickname: string;
  role: string;
  createdAt: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface CreatePartRequest {
  name: string;
  category: string;
  manufacturer: string;
  specs: Record<string, unknown>;
  imageUrl?: string;
}

export interface UpdatePartRequest {
  name?: string;
  category?: string;
  manufacturer?: string;
  specs?: Record<string, unknown>;
  imageUrl?: string;
}

export const getDashboard = async () => {
  const response = await api.get<DashboardResponse>('/admin/dashboard');
  return response.data;
};

export const getUsers = async (params: { page?: number; size?: number } = {}) => {
  const response = await api.get<PageResponse<AdminUser>>('/admin/users', { params });
  return response.data;
};

export const createPart = async (data: CreatePartRequest) => {
  const response = await api.post('/admin/parts', data);
  return response.data;
};

export const updatePart = async (id: number, data: UpdatePartRequest) => {
  const response = await api.put(`/admin/parts/${id}`, data);
  return response.data;
};

export const deletePart = async (id: number) => {
  const response = await api.delete(`/admin/parts/${id}`);
  return response.data;
};
