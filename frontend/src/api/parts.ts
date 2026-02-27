import api from './client';

export interface PartsParams {
  category?: string;
  keyword?: string;
  minPrice?: number;
  maxPrice?: number;
  page?: number;
  size?: number;
  sort?: string;
}

export interface Part {
  id: number;
  name: string;
  category: string;
  manufacturer: string;
  specs: Record<string, unknown>;
  lowestPrice?: number;
  imageUrl?: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export const getParts = async (params: PartsParams = {}) => {
  const response = await api.get<PageResponse<Part>>('/parts', { params });
  return response.data;
};

export const getPartDetail = async (id: number) => {
  const response = await api.get<Part>(`/parts/${id}`);
  return response.data;
};

export const getCategories = async () => {
  const response = await api.get<string[]>('/parts/categories');
  return response.data;
};
