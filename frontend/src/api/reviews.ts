import api from './client';

export interface CreateReviewRequest {
  partId: number;
  content: string;
  rating: number;
}

export interface UpdateReviewRequest {
  content?: string;
  rating?: number;
}

export interface Review {
  id: number;
  partId: number;
  partName: string;
  content: string;
  rating: number;
  authorId: number;
  authorNickname: string;
  likeCount: number;
  createdAt: string;
  updatedAt: string;
}

interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
}

export const getReviews = async (partId: number): Promise<Review[]> => {
  const response = await api.get<PageResponse<Review>>(`/parts/${partId}/reviews`);
  const data = response.data as unknown as PageResponse<Review>;
  return data.content ?? [];
};

export const createReview = async (data: CreateReviewRequest) => {
  const response = await api.post<Review>('/reviews', data);
  return response.data;
};

export const updateReview = async (id: number, data: UpdateReviewRequest) => {
  const response = await api.put<Review>(`/reviews/${id}`, data);
  return response.data;
};

export const deleteReview = async (id: number) => {
  const response = await api.delete(`/reviews/${id}`);
  return response.data;
};
