import api from './client';

export type TargetType = 'POST' | 'COMMENT' | 'REVIEW' | 'BUILD';

export interface LikeResponse {
  liked: boolean;
  likeCount: number;
}

export const toggleLike = async (targetType: TargetType, targetId: number) => {
  const response = await api.post<LikeResponse>(`/likes/${targetType}/${targetId}`);
  return response.data;
};

export const getLikeCount = async (targetType: TargetType, targetId: number) => {
  const response = await api.get<LikeResponse>(`/likes/${targetType}/${targetId}`);
  return response.data;
};
