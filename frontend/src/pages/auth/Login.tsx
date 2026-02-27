import { useState, type FormEvent } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { login as loginApi, getOAuth2Urls } from '../../api/auth';
import { useAuth } from '../../hooks/useAuth';

export default function Login() {
  const navigate = useNavigate();
  const { login } = useAuth();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isLoadingOAuth, setIsLoadingOAuth] = useState(false);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();

    if (!email || !password) {
      toast.error('이메일과 비밀번호를 입력해주세요.');
      return;
    }

    setIsSubmitting(true);
    try {
      const response = await loginApi({ email, password });
      const { accessToken, refreshToken } = (response as { data?: { accessToken: string; refreshToken: string } }).data ?? response;
      await login(accessToken, refreshToken);
      toast.success('로그인 성공!');
      navigate('/');
    } catch (err: unknown) {
      const error = err as { response?: { data?: { message?: string } } };
      const message = error.response?.data?.message || '로그인에 실패했습니다.';
      toast.error(message);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleSocialLogin = async (provider: 'naver' | 'google') => {
    setIsLoadingOAuth(true);
    try {
      const response = await getOAuth2Urls();
      const urls = (response as { data?: { naver: string; google: string } }).data ?? response;
      const url = urls[provider];
      if (url) {
        window.location.href = url;
      } else {
        toast.error('소셜 로그인 URL을 가져올 수 없습니다.');
      }
    } catch {
      toast.error('소셜 로그인 정보를 불러올 수 없습니다.');
    } finally {
      setIsLoadingOAuth(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4">
      <div className="w-full max-w-md bg-white rounded-2xl shadow-lg p-8">
        <h1 className="text-2xl font-bold text-center text-gray-900 mb-8">
          로그인
        </h1>

        <form onSubmit={handleSubmit} className="space-y-5">
          <div>
            <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">
              이메일
            </label>
            <input
              id="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="example@email.com"
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-colors"
              required
            />
          </div>

          <div>
            <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-1">
              비밀번호
            </label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="비밀번호를 입력하세요"
              className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none transition-colors"
              required
            />
          </div>

          <button
            type="submit"
            disabled={isSubmitting}
            className="w-full py-3 bg-blue-600 text-white font-semibold rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
          >
            {isSubmitting ? '로그인 중...' : '로그인'}
          </button>
        </form>

        <div className="my-6 flex items-center">
          <div className="flex-1 border-t border-gray-300" />
          <span className="px-4 text-sm text-gray-500">또는</span>
          <div className="flex-1 border-t border-gray-300" />
        </div>

        <div className="space-y-3">
          <button
            onClick={() => handleSocialLogin('google')}
            disabled={isLoadingOAuth}
            className="w-full py-3 bg-white text-gray-700 font-semibold rounded-lg border border-gray-300 hover:bg-gray-50 disabled:opacity-50 transition-colors flex items-center justify-center gap-2"
          >
            <svg className="w-5 h-5" viewBox="0 0 24 24">
              <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92a5.06 5.06 0 0 1-2.2 3.32v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.1z" fill="#4285F4" />
              <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853" />
              <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" fill="#FBBC05" />
              <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335" />
            </svg>
            구글 로그인
          </button>

          <button
            onClick={() => handleSocialLogin('naver')}
            disabled={isLoadingOAuth}
            className="w-full py-3 bg-[#03C75A] text-white font-semibold rounded-lg hover:bg-[#02B350] disabled:opacity-50 transition-colors flex items-center justify-center gap-2"
          >
            <svg className="w-5 h-5" viewBox="0 0 24 24" fill="currentColor">
              <path d="M16.273 12.845L7.376 3H3v18h4.727V11.155L16.624 21H21V3h-4.727z" />
            </svg>
            네이버 로그인
          </button>
        </div>

        <p className="mt-8 text-center text-sm text-gray-600">
          계정이 없으신가요?{' '}
          <Link to="/signup" className="text-blue-600 font-medium hover:underline">
            회원가입
          </Link>
        </p>
      </div>
    </div>
  );
}
