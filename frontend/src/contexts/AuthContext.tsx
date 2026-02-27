import { createContext, useState, useEffect, useCallback, type ReactNode } from 'react';
import { logout as apiLogout } from '../api/auth';
import { getProfile, type UserProfile } from '../api/users';

interface AuthUser {
  id: number;
  nickname: string;
  email: string;
  role: string;
}

interface AuthContextType {
  user: AuthUser | null;
  accessToken: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (accessToken: string, refreshToken: string) => Promise<void>;
  logout: () => Promise<void>;
}

export const AuthContext = createContext<AuthContextType | null>(null);

interface AuthProviderProps {
  children: ReactNode;
}

export function AuthProvider({ children }: AuthProviderProps) {
  const [user, setUser] = useState<AuthUser | null>(null);
  const [accessToken, setAccessToken] = useState<string | null>(
    localStorage.getItem('accessToken'),
  );
  const [refreshToken, setRefreshToken] = useState<string | null>(
    localStorage.getItem('refreshToken'),
  );
  const [isLoading, setIsLoading] = useState(true);

  const isAuthenticated = !!user && !!accessToken;

  const fetchUser = useCallback(async () => {
    try {
      const response = await getProfile();
      const profile: UserProfile = (response as { data?: UserProfile }).data ?? (response as UserProfile);
      setUser({
        id: profile.id,
        nickname: profile.nickname,
        email: profile.email,
        role: profile.role,
      });
    } catch {
      localStorage.removeItem('accessToken');
      localStorage.removeItem('refreshToken');
      setAccessToken(null);
      setRefreshToken(null);
      setUser(null);
    }
  }, []);

  useEffect(() => {
    const init = async () => {
      if (accessToken) {
        await fetchUser();
      }
      setIsLoading(false);
    };
    init();
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  const login = useCallback(
    async (newAccessToken: string, newRefreshToken: string) => {
      localStorage.setItem('accessToken', newAccessToken);
      localStorage.setItem('refreshToken', newRefreshToken);
      setAccessToken(newAccessToken);
      setRefreshToken(newRefreshToken);

      try {
        const response = await getProfile();
        const profile: UserProfile = (response as { data?: UserProfile }).data ?? (response as UserProfile);
        setUser({
          id: profile.id,
          nickname: profile.nickname,
          email: profile.email,
          role: profile.role,
        });
      } catch {
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        setAccessToken(null);
        setRefreshToken(null);
        setUser(null);
      }
    },
    [],
  );

  const logout = useCallback(async () => {
    const currentRefreshToken = localStorage.getItem('refreshToken');
    if (currentRefreshToken) {
      try {
        await apiLogout(currentRefreshToken);
      } catch {
        // Ignore logout API errors - clear local state regardless
      }
    }
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    setAccessToken(null);
    setRefreshToken(null);
    setUser(null);
  }, []);

  return (
    <AuthContext.Provider
      value={{
        user,
        accessToken,
        refreshToken,
        isAuthenticated,
        isLoading,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}
