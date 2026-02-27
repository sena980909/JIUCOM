import { useState, useEffect } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { getProfile, updateProfile, type UserProfile } from '../../api/users';
import { useAuth } from '../../hooks/useAuth';
import LoadingSpinner from '../../components/common/LoadingSpinner';

export default function Profile() {
  const { isAuthenticated } = useAuth();
  const queryClient = useQueryClient();

  const { data, isLoading, isError } = useQuery({
    queryKey: ['profile'],
    queryFn: getProfile,
    enabled: isAuthenticated,
  });

  const profile: UserProfile | undefined = data
    ? (data as { data?: UserProfile }).data ?? (data as UserProfile)
    : undefined;

  const [nickname, setNickname] = useState('');
  const [isEditing, setIsEditing] = useState(false);

  useEffect(() => {
    if (profile) {
      setNickname(profile.nickname);
    }
  }, [profile]);

  const updateMutation = useMutation({
    mutationFn: (newNickname: string) => updateProfile({ nickname: newNickname }),
    onSuccess: () => {
      toast.success('프로필이 업데이트되었습니다');
      queryClient.invalidateQueries({ queryKey: ['profile'] });
      setIsEditing(false);
    },
    onError: () => {
      toast.error('프로필 업데이트에 실패했습니다');
    },
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const trimmed = nickname.trim();
    if (!trimmed) {
      toast.error('닉네임을 입력해주세요');
      return;
    }
    if (trimmed === profile?.nickname) {
      setIsEditing(false);
      return;
    }
    updateMutation.mutate(trimmed);
  };

  const handleCancel = () => {
    setNickname(profile?.nickname ?? '');
    setIsEditing(false);
  };

  if (!isAuthenticated) {
    return (
      <div className="py-20 text-center">
        <p className="text-gray-500">로그인이 필요합니다</p>
      </div>
    );
  }

  if (isLoading) {
    return <LoadingSpinner />;
  }

  if (isError || !profile) {
    return (
      <div className="py-20 text-center">
        <p className="text-red-500">프로필 정보를 불러올 수 없습니다</p>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-md px-4 py-8">
      <h1 className="mb-6 text-2xl font-bold text-gray-900">내 프로필</h1>

      <div className="rounded-lg border border-gray-200 bg-white p-6 shadow-sm">
        {/* Avatar */}
        <div className="mb-6 flex justify-center">
          <div className="flex h-20 w-20 items-center justify-center rounded-full bg-blue-100 text-3xl font-bold text-blue-600">
            {profile.nickname?.charAt(0) ?? 'U'}
          </div>
        </div>

        <form onSubmit={handleSubmit}>
          {/* Email (readonly) */}
          <div className="mb-4">
            <label className="mb-1 block text-sm font-medium text-gray-700">
              이메일
            </label>
            <input
              type="email"
              value={profile.email}
              readOnly
              className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-500 cursor-not-allowed"
            />
          </div>

          {/* Nickname */}
          <div className="mb-4">
            <label className="mb-1 block text-sm font-medium text-gray-700">
              닉네임
            </label>
            {isEditing ? (
              <input
                type="text"
                value={nickname}
                onChange={(e) => setNickname(e.target.value)}
                maxLength={20}
                className="w-full rounded-lg border border-gray-300 bg-white px-3 py-2 text-sm text-gray-900 focus:border-blue-500 focus:ring-1 focus:ring-blue-500 focus:outline-none transition-colors"
                autoFocus
              />
            ) : (
              <div className="flex items-center justify-between rounded-lg border border-gray-200 bg-gray-50 px-3 py-2">
                <span className="text-sm text-gray-900">{profile.nickname}</span>
                <button
                  type="button"
                  onClick={() => setIsEditing(true)}
                  className="text-xs font-medium text-blue-600 hover:text-blue-800 transition-colors"
                >
                  수정
                </button>
              </div>
            )}
          </div>

          {/* Role */}
          <div className="mb-4">
            <label className="mb-1 block text-sm font-medium text-gray-700">
              등급
            </label>
            <input
              type="text"
              value={profile.role === 'ADMIN' ? '관리자' : '일반회원'}
              readOnly
              className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-500 cursor-not-allowed"
            />
          </div>

          {/* Join date */}
          <div className="mb-6">
            <label className="mb-1 block text-sm font-medium text-gray-700">
              가입일
            </label>
            <input
              type="text"
              value={new Date(profile.createdAt).toLocaleDateString('ko-KR', {
                year: 'numeric',
                month: 'long',
                day: 'numeric',
              })}
              readOnly
              className="w-full rounded-lg border border-gray-200 bg-gray-50 px-3 py-2 text-sm text-gray-500 cursor-not-allowed"
            />
          </div>

          {/* Action buttons */}
          {isEditing && (
            <div className="flex gap-3">
              <button
                type="submit"
                disabled={updateMutation.isPending}
                className="flex-1 rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700 disabled:bg-blue-300 disabled:cursor-not-allowed transition-colors"
              >
                {updateMutation.isPending ? '저장 중...' : '저장'}
              </button>
              <button
                type="button"
                onClick={handleCancel}
                disabled={updateMutation.isPending}
                className="flex-1 rounded-lg border border-gray-300 bg-white px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50 disabled:cursor-not-allowed transition-colors"
              >
                취소
              </button>
            </div>
          )}
        </form>
      </div>
    </div>
  );
}
