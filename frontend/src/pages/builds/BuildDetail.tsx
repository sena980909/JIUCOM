import { useParams, useNavigate, Link } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { getBuildDetail, deleteBuild } from '../../api/builds';
import { toggleLike, getLikeCount } from '../../api/likes';
import { useAuth } from '../../hooks/useAuth';
import CompatibilityWarning from '../../components/builds/CompatibilityWarning';

function formatPrice(price: number | null): string {
  if (price == null) return '-';
  return price.toLocaleString('ko-KR');
}

function formatDate(dateStr: string): string {
  const date = new Date(dateStr);
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });
}

const CATEGORY_LABELS: Record<string, string> = {
  CPU: 'CPU',
  GPU: '그래픽카드',
  RAM: '메모리',
  MOTHERBOARD: '메인보드',
  SSD: 'SSD',
  HDD: 'HDD',
  POWER_SUPPLY: '파워서플라이',
  CASE: '케이스',
  COOLER: '쿨러',
  MONITOR: '모니터',
  KEYBOARD: '키보드',
  MOUSE: '마우스',
  ETC: '기타',
};

export default function BuildDetail() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { user, isAuthenticated } = useAuth();
  const buildId = Number(id);

  const {
    data: build,
    isLoading,
    isError,
  } = useQuery({
    queryKey: ['build', buildId],
    queryFn: () => getBuildDetail(buildId),
    enabled: !isNaN(buildId),
  });

  const { data: likeData } = useQuery({
    queryKey: ['likes', 'BUILD', buildId],
    queryFn: () => getLikeCount('BUILD', buildId),
    enabled: !isNaN(buildId),
  });

  const likeMutation = useMutation({
    mutationFn: () => toggleLike('BUILD', buildId),
    onSuccess: (data) => {
      queryClient.setQueryData(['likes', 'BUILD', buildId], data);
      queryClient.invalidateQueries({ queryKey: ['build', buildId] });
      toast.success(data.liked ? '좋아요!' : '좋아요 취소');
    },
    onError: () => {
      toast.error('좋아요 처리에 실패했습니다.');
    },
  });

  const deleteMutation = useMutation({
    mutationFn: () => deleteBuild(buildId),
    onSuccess: () => {
      toast.success('견적이 삭제되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['builds'] });
      navigate('/builds');
    },
    onError: () => {
      toast.error('견적 삭제에 실패했습니다.');
    },
  });

  const handleDelete = () => {
    if (window.confirm('정말로 이 견적을 삭제하시겠습니까?')) {
      deleteMutation.mutate();
    }
  };

  const isOwner = user && build && user.id === build.ownerId;

  // Extract like data (may be wrapped in ApiResponse)
  const likeInfo = (() => {
    if (!likeData) return { liked: false, likeCount: build?.likeCount ?? 0 };
    const raw = likeData as unknown;
    if (raw && typeof raw === 'object' && 'data' in (raw as Record<string, unknown>)) {
      return (raw as { data: { liked: boolean; likeCount: number } }).data;
    }
    return likeData;
  })();

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-20">
        <div className="flex flex-col items-center gap-3">
          <div className="w-8 h-8 border-3 border-blue-500 border-t-transparent rounded-full animate-spin" />
          <span className="text-sm text-gray-500">견적 정보를 불러오는 중...</span>
        </div>
      </div>
    );
  }

  if (isError || !build) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-20 text-center">
        <p className="text-gray-500 mb-4">견적을 찾을 수 없습니다.</p>
        <Link to="/builds" className="text-sm text-blue-600 hover:text-blue-700 underline">
          견적 목록으로 돌아가기
        </Link>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      {/* Back link */}
      <Link
        to="/builds"
        className="inline-flex items-center gap-1 text-sm text-gray-500 hover:text-gray-700 mb-6 transition-colors"
      >
        <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
        </svg>
        견적 목록
      </Link>

      {/* Header */}
      <div className="bg-white border border-gray-200 rounded-lg p-6 mb-6">
        <div className="flex items-start justify-between mb-4">
          <div>
            <div className="flex items-center gap-2 mb-1">
              <h1 className="text-2xl font-bold text-gray-900">{build.name}</h1>
              {!build.isPublic && (
                <span className="inline-flex items-center rounded-md bg-gray-100 px-2 py-0.5 text-xs font-medium text-gray-600">
                  비공개
                </span>
              )}
            </div>
            <div className="flex items-center gap-3 text-sm text-gray-500">
              <span>{build.ownerNickname}</span>
              <span className="text-gray-300">|</span>
              <span>{formatDate(build.createdAt)}</span>
              {build.viewCount > 0 && (
                <>
                  <span className="text-gray-300">|</span>
                  <span>조회 {build.viewCount}</span>
                </>
              )}
            </div>
          </div>

          {isOwner && (
            <div className="flex items-center gap-2">
              <Link
                to={`/builds/${build.id}/edit`}
                className="px-3 py-1.5 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 transition-colors"
              >
                수정
              </Link>
              <button
                onClick={handleDelete}
                disabled={deleteMutation.isPending}
                className="px-3 py-1.5 text-sm font-medium text-red-600 bg-white border border-red-300 rounded-md hover:bg-red-50 transition-colors disabled:opacity-50"
              >
                {deleteMutation.isPending ? '삭제 중...' : '삭제'}
              </button>
            </div>
          )}
        </div>

        {build.description && (
          <p className="text-sm text-gray-600 leading-relaxed">{build.description}</p>
        )}

        {/* Like button */}
        <div className="mt-4 pt-4 border-t border-gray-100">
          <button
            onClick={() => {
              if (!isAuthenticated) {
                toast.error('로그인이 필요합니다.');
                return;
              }
              likeMutation.mutate();
            }}
            disabled={likeMutation.isPending}
            className={`inline-flex items-center gap-1.5 px-3 py-1.5 rounded-full text-sm font-medium transition-colors ${
              likeInfo.liked
                ? 'bg-red-50 text-red-600 border border-red-200 hover:bg-red-100'
                : 'bg-gray-50 text-gray-600 border border-gray-200 hover:bg-gray-100'
            }`}
          >
            <svg
              className="w-4 h-4"
              fill={likeInfo.liked ? 'currentColor' : 'none'}
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"
              />
            </svg>
            좋아요 {likeInfo.likeCount}
          </button>
        </div>
      </div>

      {/* Compatibility Warnings */}
      {build.compatibilityWarnings && build.compatibilityWarnings.length > 0 && (
        <div className="mb-6">
          <CompatibilityWarning warnings={build.compatibilityWarnings} />
        </div>
      )}

      {/* Parts Table */}
      <div className="bg-white border border-gray-200 rounded-lg overflow-hidden mb-6">
        <div className="px-6 py-4 border-b border-gray-200">
          <h2 className="text-lg font-semibold text-gray-900">부품 목록</h2>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="bg-gray-50 border-b border-gray-200">
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  카테고리
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  부품명
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                  제조사
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                  수량
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                  단가
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                  소계
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {build.parts.map((part, index) => (
                <tr key={index} className="hover:bg-gray-50 transition-colors">
                  <td className="px-6 py-3 text-sm text-gray-500">
                    <span className="inline-flex items-center rounded-md bg-gray-100 px-2 py-0.5 text-xs font-medium text-gray-600">
                      {CATEGORY_LABELS[part.category] || part.category}
                    </span>
                  </td>
                  <td className="px-6 py-3 text-sm font-medium text-gray-900">
                    <Link
                      to={`/parts/${part.partId}`}
                      className="hover:text-blue-600 transition-colors"
                    >
                      {part.partName}
                    </Link>
                  </td>
                  <td className="px-6 py-3 text-sm text-gray-500">{part.manufacturer}</td>
                  <td className="px-6 py-3 text-sm text-gray-700 text-right">{part.quantity}</td>
                  <td className="px-6 py-3 text-sm text-gray-700 text-right">
                    {formatPrice(part.unitPrice)}원
                  </td>
                  <td className="px-6 py-3 text-sm font-medium text-gray-900 text-right">
                    {formatPrice(part.lineTotal)}원
                  </td>
                </tr>
              ))}
            </tbody>
            <tfoot>
              <tr className="bg-gray-50 border-t-2 border-gray-200">
                <td colSpan={5} className="px-6 py-4 text-right text-base font-semibold text-gray-900">
                  총 견적 금액
                </td>
                <td className="px-6 py-4 text-right text-xl font-bold text-blue-600">
                  {formatPrice(build.totalPrice)}원
                </td>
              </tr>
            </tfoot>
          </table>
        </div>
      </div>
    </div>
  );
}
