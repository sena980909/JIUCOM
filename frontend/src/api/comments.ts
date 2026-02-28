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
  authorId: number;
  authorNickname: string;
  parentId: number | null;
  likeCount: number;
  replies: Comment[];
  createdAt: string;
  updatedAt: string;
}

interface CommentsResponse {
  comments: Comment[];
  totalPages: number;
  totalElements: number;
  currentPage: number;
}

export const getComments = async (postId: number): Promise<Comment[]> => {
  const response = await api.get<CommentsResponse>(`/posts/${postId}/comments`);
  const data = response.data as unknown as CommentsResponse;
  return data.comments ?? [];
};

export const createComment = async (postId: number, data: CreateCommentRequest) => {
  const response = await api.post<Comment>(`/posts/${postId}/comments`, data);
  return response.data;
};

export const updateComment = async (_postId: number, commentId: number, data: UpdateCommentRequest) => {
  const response = await api.put<Comment>(`/comments/${commentId}`, data);
  return response.data;
};

export const deleteComment = async (_postId: number, commentId: number) => {
  const response = await api.delete(`/comments/${commentId}`);
  return response.data;
};
