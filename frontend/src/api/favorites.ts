import api from './client';

export interface Favorite {
  id: number;
  partId: number;
  partName: string;
  category: string;
  lowestPrice: number;
  createdAt: string;
}

export const getFavorites = async () => {
  const response = await api.get<Favorite[]>('/favorites');
  return response.data;
};

export const addFavorite = async (partId: number) => {
  const response = await api.post('/favorites', null, {
    params: { partId },
  });
  return response.data;
};

export const removeFavorite = async (partId: number) => {
  const response = await api.delete('/favorites', {
    params: { partId },
  });
  return response.data;
};
