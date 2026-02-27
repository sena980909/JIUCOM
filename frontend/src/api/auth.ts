import api from './client';

export interface SignupRequest {
  email: string;
  password: string;
  nickname: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthTokenResponse {
  accessToken: string;
  refreshToken: string;
}

export interface OAuth2UrlsResponse {
  google: string;
  kakao: string;
  naver: string;
}

export const signup = async (data: SignupRequest) => {
  const response = await api.post<AuthTokenResponse>('/auth/signup', data);
  return response.data;
};

export const login = async (data: LoginRequest) => {
  const response = await api.post<AuthTokenResponse>('/auth/login', data);
  return response.data;
};

export const refresh = async (refreshToken: string) => {
  const response = await api.post<AuthTokenResponse>('/auth/refresh', { refreshToken });
  return response.data;
};

export const logout = async (refreshToken: string) => {
  const response = await api.post('/auth/logout', { refreshToken });
  return response.data;
};

export const getOAuth2Urls = async () => {
  const response = await api.get<OAuth2UrlsResponse>('/auth/oauth2/urls');
  return response.data;
};
