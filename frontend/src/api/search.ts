import api from './client';

export interface SearchParams {
  keyword: string;
  type?: string;
  page?: number;
  size?: number;
}

export interface SearchResult {
  id: number;
  title: string;
  description: string;
  type: string;
  url: string;
}

export interface SearchResponse {
  content: SearchResult[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface SuggestResponse {
  suggestions: string[];
}

export const search = async (params: SearchParams) => {
  const response = await api.get<SearchResponse>('/search', { params });
  return response.data;
};

export const suggest = async (keyword: string) => {
  const response = await api.get<SuggestResponse>('/search/suggest', {
    params: { keyword },
  });
  return response.data;
};
