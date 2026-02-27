import api from './client';

export interface Notification {
  id: number;
  type: string;
  message: string;
  referenceId: number;
  read: boolean;
  createdAt: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface UnreadCountResponse {
  count: number;
}

export const getNotifications = async (params: { page?: number; size?: number } = {}) => {
  const response = await api.get<PageResponse<Notification>>('/notifications', { params });
  return response.data;
};

export const getUnreadCount = async () => {
  const response = await api.get<UnreadCountResponse>('/notifications/unread-count');
  return response.data;
};

export const markAsRead = async (id: number) => {
  const response = await api.patch(`/notifications/${id}/read`);
  return response.data;
};

export const markAllAsRead = async () => {
  const response = await api.patch('/notifications/read');
  return response.data;
};
