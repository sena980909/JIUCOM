import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import toast from 'react-hot-toast';
import { getFavorites, removeFavorite, type Favorite } from '../../api/favorites';
import { useAuth } from '../../hooks/useAuth';
import LoadingSpinner from '../../components/common/LoadingSpinner';

function formatPrice(price: number): string {
  return price.toLocaleString('ko-KR');
}

function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  });
}

export default function MyFavorites() {
  const { isAuthenticated } = useAuth();
  const queryClient = useQueryClient();

  const { data, isLoading, isError } = useQuery({
    queryKey: ['favorites'],
    queryFn: getFavorites,
    enabled: isAuthenticated,
  });

  const favorites: Favorite[] = Array.isArray(data)
    ? data
    : (data as unknown as { data?: Favorite[] })?.data ?? [];

  const removeMutation = useMutation({
    mutationFn: removeFavorite,
    onSuccess: () => {
      toast.success('즐겨찾기가 해제되었습니다');
      queryClient.invalidateQueries({ queryKey: ['favorites'] });
    },
    onError: () => {
      toast.error('즐겨찾기 해제에 실패했습니다');
    },
  });

  const handleRemove = (partId: number, e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    removeMutation.mutate(partId);
  };

  if (!isAuthenticated) {
    return (
      <div className="py-20 text-center">
        <p className="text-gray-500">로그인이 필요합니다</p>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-5xl px-4 py-8">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-gray-900">즐겨찾기</h1>
        <p className="mt-1 text-sm text-gray-500">
          관심 부품을 한눈에 확인하세요
        </p>
      </div>

      {isLoading ? (
        <LoadingSpinner />
      ) : isError ? (
        <div className="py-20 text-center">
          <p className="text-red-500">즐겨찾기 목록을 불러오는 중 오류가 발생했습니다.</p>
        </div>
      ) : favorites.length === 0 ? (
        <div className="py-20 text-center">
          <svg className="mx-auto h-12 w-12 text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.519 4.674a1 1 0 00.95.69h4.915c.969 0 1.371 1.24.588 1.81l-3.976 2.888a1 1 0 00-.363 1.118l1.518 4.674c.3.922-.755 1.688-1.538 1.118l-3.976-2.888a1 1 0 00-1.176 0l-3.976 2.888c-.783.57-1.838-.197-1.538-1.118l1.518-4.674a1 1 0 00-.363-1.118l-3.976-2.888c-.784-.57-.38-1.81.588-1.81h4.914a1 1 0 00.951-.69l1.519-4.674z" />
          </svg>
          <p className="mt-4 text-gray-500">즐겨찾기한 부품이 없습니다</p>
          <Link
            to="/parts"
            className="mt-4 inline-flex items-center gap-1.5 rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700 transition-colors"
          >
            부품 둘러보기
          </Link>
        </div>
      ) : (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {favorites.map((fav) => (
            <div
              key={fav.id}
              className="relative rounded-lg border border-gray-200 bg-white shadow-sm hover:shadow-md transition-shadow"
            >
              <Link to={`/parts/${fav.partId}`} className="block p-5">
                <div className="mb-2">
                  <span className="inline-block rounded-full bg-blue-50 px-2.5 py-0.5 text-xs font-medium text-blue-700">
                    {fav.category}
                  </span>
                </div>
                <h3 className="text-base font-semibold text-gray-900 leading-tight mb-2 line-clamp-2">
                  {fav.partName}
                </h3>
                <div className="text-lg font-bold text-blue-600 mb-2">
                  {fav.lowestPrice > 0 ? `${formatPrice(fav.lowestPrice)}원` : '가격 미정'}
                </div>
                <div className="text-xs text-gray-400">
                  추가일: {formatDate(fav.createdAt)}
                </div>
              </Link>

              {/* Remove button */}
              <button
                onClick={(e) => handleRemove(fav.partId, e)}
                disabled={removeMutation.isPending}
                className="absolute top-3 right-3 rounded-full p-1.5 text-gray-400 hover:bg-red-50 hover:text-red-500 transition-colors"
                title="즐겨찾기 해제"
              >
                <svg className="h-5 w-5" fill="currentColor" viewBox="0 0 24 24">
                  <path d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.519 4.674a1 1 0 00.95.69h4.915c.969 0 1.371 1.24.588 1.81l-3.976 2.888a1 1 0 00-.363 1.118l1.518 4.674c.3.922-.755 1.688-1.538 1.118l-3.976-2.888a1 1 0 00-1.176 0l-3.976 2.888c-.783.57-1.838-.197-1.538-1.118l1.518-4.674a1 1 0 00-.363-1.118l-3.976-2.888c-.784-.57-.38-1.81.588-1.81h4.914a1 1 0 00.951-.69l1.519-4.674z" />
                </svg>
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
