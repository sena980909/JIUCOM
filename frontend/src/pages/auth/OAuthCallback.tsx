import { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import toast from 'react-hot-toast';
import { useAuth } from '../../hooks/useAuth';

export default function OAuthCallback() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { login } = useAuth();
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const processCallback = async () => {
      const accessToken = searchParams.get('accessToken');
      const refreshToken = searchParams.get('refreshToken');
      const errorParam = searchParams.get('error');

      if (errorParam) {
        setError('소셜 로그인에 실패했습니다.');
        toast.error('소셜 로그인에 실패했습니다.');
        setTimeout(() => navigate('/login'), 2000);
        return;
      }

      if (!accessToken || !refreshToken) {
        setError('인증 정보가 올바르지 않습니다.');
        toast.error('인증 정보가 올바르지 않습니다.');
        setTimeout(() => navigate('/login'), 2000);
        return;
      }

      try {
        await login(accessToken, refreshToken);
        toast.success('로그인 성공!');
        navigate('/');
      } catch {
        setError('로그인 처리 중 오류가 발생했습니다.');
        toast.error('로그인 처리 중 오류가 발생했습니다.');
        setTimeout(() => navigate('/login'), 2000);
      }
    };

    processCallback();
  }, [searchParams, login, navigate]);

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <div className="w-16 h-16 mx-auto mb-4 flex items-center justify-center rounded-full bg-red-100">
            <svg className="w-8 h-8 text-red-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            </svg>
          </div>
          <p className="text-lg text-gray-700 mb-2">{error}</p>
          <p className="text-sm text-gray-500">잠시 후 로그인 페이지로 이동합니다...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="text-center">
        <div className="w-12 h-12 mx-auto mb-4 border-4 border-blue-500 border-t-transparent rounded-full animate-spin" />
        <p className="text-lg text-gray-700">로그인 처리 중...</p>
        <p className="text-sm text-gray-500 mt-1">잠시만 기다려주세요.</p>
      </div>
    </div>
  );
}
