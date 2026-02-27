import api from './client';

export interface PriceComparison {
  partId: number;
  partName: string;
  prices: {
    sellerId: number;
    sellerName: string;
    price: number;
    url: string;
    updatedAt: string;
  }[];
}

export interface PriceHistoryEntry {
  date: string;
  minPrice: number;
  maxPrice: number;
  avgPrice: number;
}

export interface PriceAlert {
  id: number;
  partId: number;
  partName: string;
  targetPrice: number;
  active: boolean;
  createdAt: string;
}

export interface CreateAlertRequest {
  partId: number;
  targetPrice: number;
}

export const comparePrices = async (partId: number) => {
  const response = await api.get<PriceComparison>('/prices/compare', {
    params: { partId },
  });
  return response.data;
};

export const getPriceHistory = async (partId: number, params: { days?: number } = {}) => {
  const response = await api.get<PriceHistoryEntry[]>('/prices/history', {
    params: { partId, ...params },
  });
  return response.data;
};

export const getAlerts = async () => {
  const response = await api.get<PriceAlert[]>('/prices/alerts');
  return response.data;
};

export const createAlert = async (data: CreateAlertRequest) => {
  const response = await api.post<PriceAlert>('/prices/alerts', data);
  return response.data;
};

export const deleteAlert = async (id: number) => {
  const response = await api.delete(`/prices/alerts/${id}`);
  return response.data;
};
