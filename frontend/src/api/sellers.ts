import api from './client';

export interface Seller {
  id: number;
  name: string;
  url: string;
  status: string;
}

export const getSellers = async () => {
  const response = await api.get<Seller[]>('/sellers');
  return response.data;
};
