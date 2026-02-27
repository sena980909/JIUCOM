import api from './client';

export interface CreateCommentRequest {
  content: string;
  parentId?: number;
}

export interface UpdateCommentRequest {
  content: string;
}

export interface Comment {
  id: number;
  content: string;
  userId: number;
  nickname: string;
  parentId: number | null;
  children: Comment[];
  createdAt: string;
  updatedAt: string;
}

export const getComments = async (postId: number) => {
  const response = await api.get<Comment[]>(`/posts/${postId}/comments`);
  return response.data;
};

export const createComment = async (postId: number, data: CreateCommentRequest) => {
  const response = await api.post<Comment>(`/posts/${postId}/comments`, data);
  return response.data;
};

export const updateComment = async (postId: number, commentId: number, data: UpdateCommentRequest) => {
  const response = await api.put<Comment>(`/posts/${postId}/comments/${commentId}`, data);
  return response.data;
};

export const deleteComment = async (postId: number, commentId: number) => {
  const response = await api.delete(`/posts/${postId}/comments/${commentId}`);
  return response.data;
};
