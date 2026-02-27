import api from './client';

export interface UserProfile {
  id: number;
  email: string;
  nickname: string;
  role: string;
  createdAt: string;
}

export interface UpdateProfileRequest {
  nickname?: string;
}

export const getProfile = async () => {
  const response = await api.get<UserProfile>('/users/me');
  return response.data;
};

export const updateProfile = async (data: UpdateProfileRequest) => {
  const response = await api.patch<UserProfile>('/users/me', data);
  return response.data;
};
