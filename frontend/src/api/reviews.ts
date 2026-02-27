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
  userId: number;
  nickname: string;
  createdAt: string;
  updatedAt: string;
}

export const getReviews = async (partId: number) => {
  const response = await api.get<Review[]>(`/parts/${partId}/reviews`);
  return response.data;
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
