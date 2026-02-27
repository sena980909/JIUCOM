import api from './client';

export interface BuildPartRequest {
  partId: number;
  quantity: number;
}

export interface CreateBuildRequest {
  name: string;
  description?: string;
  isPublic?: boolean;
  parts: BuildPartRequest[];
}

export interface UpdateBuildRequest {
  name?: string;
  description?: string;
  isPublic?: boolean;
  parts?: BuildPartRequest[];
}

export interface BuildPartResponse {
  partId: number;
  partName: string;
  category: string;
  manufacturer: string;
  quantity: number;
  unitPrice: number | null;
  lineTotal: number | null;
}

export interface BuildListItem {
  id: number;
  name: string;
  description: string;
  totalPrice: number;
  partCount: number;
  isPublic: boolean;
  viewCount: number;
  likeCount: number;
  ownerNickname: string;
  createdAt: string;
}

export interface BuildDetail {
  id: number;
  name: string;
  description: string;
  totalPrice: number;
  isPublic: boolean;
  viewCount: number;
  likeCount: number;
  ownerNickname: string;
  ownerId: number;
  parts: BuildPartResponse[];
  compatibilityWarnings: string[];
  createdAt: string;
  updatedAt: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

interface ApiResponseWrapper<T> {
  success: boolean;
  message: string;
  data: T;
}

export const getBuilds = async (params: { page?: number; size?: number } = {}) => {
  const response = await api.get<ApiResponseWrapper<PageResponse<BuildListItem>>>('/builds', { params });
  return response.data.data;
};

export const getMyBuilds = async (params: { page?: number; size?: number } = {}) => {
  const response = await api.get<ApiResponseWrapper<PageResponse<BuildListItem>>>('/builds/my', { params });
  return response.data.data;
};

export const getBuildDetail = async (id: number) => {
  const response = await api.get<ApiResponseWrapper<BuildDetail>>(`/builds/${id}`);
  return response.data.data;
};

export const createBuild = async (data: CreateBuildRequest) => {
  const response = await api.post<ApiResponseWrapper<BuildDetail>>('/builds', data);
  return response.data.data;
};

export const updateBuild = async (id: number, data: UpdateBuildRequest) => {
  const response = await api.put<ApiResponseWrapper<BuildDetail>>(`/builds/${id}`, data);
  return response.data.data;
};

export const deleteBuild = async (id: number) => {
  const response = await api.delete<ApiResponseWrapper<void>>(`/builds/${id}`);
  return response.data;
};
