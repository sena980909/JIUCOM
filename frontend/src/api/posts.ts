import api from './client';

export interface PostsParams {
  boardType?: string;
  keyword?: string;
  page?: number;
  size?: number;
}

export interface CreatePostRequest {
  title: string;
  content: string;
  boardType: string;
}

export interface UpdatePostRequest {
  title?: string;
  content?: string;
}

export interface Post {
  id: number;
  title: string;
  content: string;
  boardType: string;
  viewCount: number;
  likeCount: number;
  commentCount: number;
  userId: number;
  nickname: string;
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

export const getPosts = async (params: PostsParams = {}) => {
  const response = await api.get<PageResponse<Post>>('/posts', { params });
  return response.data;
};

export const getPostDetail = async (id: number) => {
  const response = await api.get<Post>(`/posts/${id}`);
  return response.data;
};

export const createPost = async (data: CreatePostRequest) => {
  const response = await api.post<Post>('/posts', data);
  return response.data;
};

export const updatePost = async (id: number, data: UpdatePostRequest) => {
  const response = await api.put<Post>(`/posts/${id}`, data);
  return response.data;
};

export const deletePost = async (id: number) => {
  const response = await api.delete(`/posts/${id}`);
  return response.data;
};
