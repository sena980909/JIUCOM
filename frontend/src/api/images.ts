import api from './client';

export interface ImageUploadResponse {
  url: string;
  filename: string;
}

export const uploadImage = async (file: File) => {
  const formData = new FormData();
  formData.append('file', file);

  const response = await api.post<ImageUploadResponse>('/images/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data',
    },
  });
  return response.data;
};
