import { useState } from 'react';
import { useParams } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { getPartDetail } from '../../api/parts';
import { comparePrices, getPriceHistory, createAlert } from '../../api/prices';
import { getReviews, createReview, deleteReview } from '../../api/reviews';
import { addFavorite, removeFavorite, getFavorites } from '../../api/favorites';
import { useAuth } from '../../hooks/useAuth';
import PriceChart from '../../components/parts/PriceChart';
import ReviewCard from '../../components/reviews/ReviewCard';

const categoryLabels: Record<string, string> = {
  CPU: 'CPU',
  GPU: '그래픽카드',
  MOTHERBOARD: '메인보드',
  RAM: '메모리',
  SSD: 'SSD',
  HDD: 'HDD',
  PSU: '파워서플라이',
  POWER_SUPPLY: '파워서플라이',
  CASE: '케이스',
  COOLER: '쿨러',
};

const specKeyLabels: Record<string, string> = {
  cores: '코어 수', threads: '스레드 수', socket: '소켓', tdp: 'TDP',
  baseClock: '기본 클럭', boostClock: '부스트 클럭', coreClock: '코어 클럭',
  vram: 'VRAM', cudaCores: 'CUDA 코어', interface: '인터페이스',
  memoryType: '메모리 타입', chipset: '칩셋', formFactor: '폼팩터',
  capacity: '용량', speed: '속도', type: '타입', readSpeed: '읽기 속도',
  writeSpeed: '쓰기 속도', wattage: '출력', efficiency: '효율 등급',
  modular: '모듈러', size: '크기', color: '색상', fanSize: '팬 크기',
};

function unwrap<T>(response: unknown): T {
  if (response && typeof response === 'object' && 'data' in response) {
    return (response as { data: T }).data;
  }
  return response as T;
}

export default function PartDetail() {
  const { id } = useParams<{ id: string }>();
  const partId = Number(id);
  const { user, isAuthenticated } = useAuth();
  const queryClient = useQueryClient();

  const [showAlertModal, setShowAlertModal] = useState(false);
  const [targetPrice, setTargetPrice] = useState('');
  const [showReviewForm, setShowReviewForm] = useState(false);
  const [reviewContent, setReviewContent] = useState('');
  const [reviewRating, setReviewRating] = useState(5);

  // Part detail
  const { data: partRaw, isLoading: isLoadingPart } = useQuery({
    queryKey: ['part', partId],
    queryFn: () => getPartDetail(partId),
    enabled: !!partId,
  });
  const part = partRaw ? unwrap<typeof partRaw>(partRaw) : undefined;

  // Price comparison
  const { data: priceComparisonRaw } = useQuery({
    queryKey: ['priceComparison', partId],
    queryFn: () => comparePrices(partId),
    enabled: !!partId,
  });
  const priceComparison = priceComparisonRaw ? unwrap<typeof priceComparisonRaw>(priceComparisonRaw) : undefined;

  // Price history
  const { data: priceHistoryRaw } = useQuery({
    queryKey: ['priceHistory', partId],
    queryFn: () => getPriceHistory(partId, { days: 30 }),
    enabled: !!partId,
  });
  const priceHistory = priceHistoryRaw ? unwrap<typeof priceHistoryRaw>(priceHistoryRaw) : undefined;

  // Reviews
  const { data: reviews } = useQuery({
    queryKey: ['reviews', partId],
    queryFn: () => getReviews(partId),
    enabled: !!partId,
  });

  // Favorites
  const { data: favoritesRaw } = useQuery({
    queryKey: ['favorites'],
    queryFn: getFavorites,
    enabled: isAuthenticated,
  });
  const favorites = favoritesRaw ? unwrap<typeof favoritesRaw>(favoritesRaw) : undefined;
  const isFavorite = Array.isArray(favorites) && favorites.some((f) => f.partId === partId);

  // Mutations
  const addFavMutation = useMutation({
    mutationFn: () => addFavorite(partId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['favorites'] });
      toast.success('관심 부품에 추가되었습니다.');
    },
    onError: () => toast.error('관심 부품 추가에 실패했습니다.'),
  });

  const removeFavMutation = useMutation({
    mutationFn: () => removeFavorite(partId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['favorites'] });
      toast.success('관심 부품에서 제거되었습니다.');
    },
    onError: () => toast.error('관심 부품 제거에 실패했습니다.'),
  });

  const alertMutation = useMutation({
    mutationFn: (price: number) => createAlert({ partId, targetPrice: price }),
    onSuccess: () => {
      toast.success('가격 알림이 설정되었습니다.');
      setShowAlertModal(false);
      setTargetPrice('');
    },
    onError: () => toast.error('가격 알림 설정에 실패했습니다.'),
  });

  const reviewMutation = useMutation({
    mutationFn: () => createReview({ partId, content: reviewContent, rating: reviewRating }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['reviews', partId] });
      toast.success('리뷰가 작성되었습니다.');
      setShowReviewForm(false);
      setReviewContent('');
      setReviewRating(5);
    },
    onError: () => toast.error('리뷰 작성에 실패했습니다.'),
  });

  const deleteReviewMutation = useMutation({
    mutationFn: (reviewId: number) => deleteReview(reviewId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['reviews', partId] });
      toast.success('리뷰가 삭제되었습니다.');
    },
    onError: () => toast.error('리뷰 삭제에 실패했습니다.'),
  });

  const handleToggleFavorite = () => {
    if (!isAuthenticated) {
      toast.error('로그인이 필요합니다.');
      return;
    }
    if (isFavorite) {
      removeFavMutation.mutate();
    } else {
      addFavMutation.mutate();
    }
  };

  const handleCreateAlert = () => {
    const price = Number(targetPrice);
    if (!price || price <= 0) {
      toast.error('올바른 가격을 입력해주세요.');
      return;
    }
    alertMutation.mutate(price);
  };

  const handleSubmitReview = () => {
    if (!reviewContent.trim()) {
      toast.error('리뷰 내용을 입력해주세요.');
      return;
    }
    reviewMutation.mutate();
  };

  // Convert price history to chart data
  const historyEntries = priceHistory && typeof priceHistory === 'object' && 'history' in priceHistory
    ? (priceHistory as { history: { date: string; avgPrice: number }[] }).history
    : Array.isArray(priceHistory) ? priceHistory : [];
  const chartData = historyEntries.map((entry) => ({
    date: entry.date,
    price: entry.avgPrice,
    sellerName: '평균가',
  }));

  if (isLoadingPart) {
    return (
      <div className="flex justify-center py-20">
        <div className="w-10 h-10 border-4 border-blue-500 border-t-transparent rounded-full animate-spin" />
      </div>
    );
  }

  if (!part) {
    return (
      <div className="text-center py-20">
        <p className="text-gray-500">부품 정보를 찾을 수 없습니다.</p>
      </div>
    );
  }

  const specsRaw = part.specs;
  const specs: Record<string, unknown> | undefined = (() => {
    if (!specsRaw) return undefined;
    if (typeof specsRaw === 'string') {
      try { return JSON.parse(specsRaw); } catch { return undefined; }
    }
    if (typeof specsRaw === 'object') return specsRaw as Record<string, unknown>;
    return undefined;
  })();

  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      {/* Header */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 mb-6">
        <div className="flex flex-col md:flex-row gap-6">
          <div className="w-full md:w-64 h-64 bg-gray-100 rounded-lg flex items-center justify-center shrink-0">
            {part.imageUrl ? (
              <img
                src={part.imageUrl}
                alt={part.name}
                className="w-full h-full object-contain p-4"
              />
            ) : (
              <svg className="w-20 h-20 text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2zM9 9h6v6H9V9z" />
              </svg>
            )}
          </div>

          <div className="flex-1">
            <div className="flex items-center gap-2 mb-2">
              <span className="px-3 py-1 bg-blue-100 text-blue-800 text-xs font-medium rounded-full">
                {categoryLabels[part.category] || part.category}
              </span>
              <span className="text-sm text-gray-500">{part.manufacturer}</span>
            </div>

            <h1 className="text-2xl font-bold text-gray-900 mb-4">{part.name}</h1>

            {part.lowestPrice != null && (
              <p className="text-3xl font-bold text-blue-600 mb-4">
                {part.lowestPrice.toLocaleString('ko-KR')}원
                <span className="text-sm font-normal text-gray-500 ml-2">최저가</span>
              </p>
            )}

            <div className="flex gap-3">
              <button
                onClick={handleToggleFavorite}
                className={`flex items-center gap-2 px-4 py-2 rounded-lg border text-sm font-medium transition-colors ${
                  isFavorite
                    ? 'bg-red-50 border-red-200 text-red-600 hover:bg-red-100'
                    : 'bg-white border-gray-300 text-gray-700 hover:bg-gray-50'
                }`}
              >
                <svg
                  className="w-5 h-5"
                  fill={isFavorite ? 'currentColor' : 'none'}
                  viewBox="0 0 24 24"
                  stroke="currentColor"
                  strokeWidth={2}
                >
                  <path strokeLinecap="round" strokeLinejoin="round" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                </svg>
                {isFavorite ? '관심 해제' : '관심 등록'}
              </button>

              <button
                onClick={() => {
                  if (!isAuthenticated) {
                    toast.error('로그인이 필요합니다.');
                    return;
                  }
                  setShowAlertModal(true);
                }}
                className="flex items-center gap-2 px-4 py-2 rounded-lg border border-gray-300 text-sm font-medium text-gray-700 hover:bg-gray-50 transition-colors"
              >
                <svg className="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                  <path strokeLinecap="round" strokeLinejoin="round" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
                </svg>
                가격 알림
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Specs */}
      {specs && Object.keys(specs).length > 0 && (
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 mb-6">
          <h2 className="text-lg font-bold text-gray-900 mb-4">사양</h2>
          <table className="w-full">
            <tbody>
              {Object.entries(specs).map(([key, value]) => (
                <tr key={key} className="border-b border-gray-100 last:border-0">
                  <td className="py-3 pr-4 text-sm font-medium text-gray-600 w-1/3">
                    {specKeyLabels[key] || key}
                  </td>
                  <td className="py-3 text-sm text-gray-900">
                    {typeof value === 'object' ? JSON.stringify(value) : String(value ?? '-')}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Price Comparison */}
      {priceComparison?.prices && priceComparison.prices.length > 0 && (
        <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 mb-6">
          <h2 className="text-lg font-bold text-gray-900 mb-4">가격 비교</h2>
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-gray-200">
                  <th className="text-left py-3 px-2 text-sm font-medium text-gray-600">판매처</th>
                  <th className="text-right py-3 px-2 text-sm font-medium text-gray-600">가격</th>
                  <th className="text-right py-3 px-2 text-sm font-medium text-gray-600"></th>
                </tr>
              </thead>
              <tbody>
                {priceComparison.prices.map((price, index) => (
                  <tr key={index} className="border-b border-gray-100 last:border-0">
                    <td className="py-3 px-2 text-sm text-gray-900 font-medium">
                      {price.sellerName}
                    </td>
                    <td className="py-3 px-2 text-sm text-right font-bold text-blue-600">
                      {price.price.toLocaleString('ko-KR')}원
                    </td>
                    <td className="py-3 px-2 text-right">
                      {price.productUrl && (
                        <a
                          href={price.productUrl}
                          target="_blank"
                          rel="noopener noreferrer"
                          className="inline-block px-3 py-1 bg-blue-600 text-white text-xs font-medium rounded hover:bg-blue-700 transition-colors"
                        >
                          구매하기
                        </a>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Price History Chart */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6 mb-6">
        <h2 className="text-lg font-bold text-gray-900 mb-4">가격 추이 (30일)</h2>
        <PriceChart data={chartData} />
      </div>

      {/* Reviews */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-6">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-lg font-bold text-gray-900">
            리뷰 {Array.isArray(reviews) ? `(${reviews.length})` : ''}
          </h2>
          {isAuthenticated && !showReviewForm && (
            <button
              onClick={() => setShowReviewForm(true)}
              className="px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 transition-colors"
            >
              리뷰 작성
            </button>
          )}
        </div>

        {/* Review Form */}
        {showReviewForm && (
          <div className="bg-gray-50 rounded-lg p-4 mb-4">
            <div className="mb-3">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                평점
              </label>
              <div className="flex gap-1">
                {[1, 2, 3, 4, 5].map((star) => (
                  <button
                    key={star}
                    onClick={() => setReviewRating(star)}
                    className="text-2xl focus:outline-none"
                  >
                    <span className={star <= reviewRating ? 'text-yellow-400' : 'text-gray-300'}>
                      ★
                    </span>
                  </button>
                ))}
              </div>
            </div>
            <textarea
              value={reviewContent}
              onChange={(e) => setReviewContent(e.target.value)}
              placeholder="리뷰 내용을 입력하세요..."
              rows={3}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none resize-none"
            />
            <div className="flex justify-end gap-2 mt-3">
              <button
                onClick={() => {
                  setShowReviewForm(false);
                  setReviewContent('');
                  setReviewRating(5);
                }}
                className="px-4 py-2 text-sm text-gray-600 hover:text-gray-800 transition-colors"
              >
                취소
              </button>
              <button
                onClick={handleSubmitReview}
                disabled={reviewMutation.isPending}
                className="px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 disabled:opacity-50 transition-colors"
              >
                {reviewMutation.isPending ? '작성 중...' : '등록'}
              </button>
            </div>
          </div>
        )}

        {/* Review List */}
        {Array.isArray(reviews) && reviews.length > 0 ? (
          <div className="space-y-3">
            {reviews.map((review) => (
              <ReviewCard
                key={review.id}
                review={review}
                currentUserId={user?.id}
                onDelete={(reviewId) => deleteReviewMutation.mutate(reviewId)}
              />
            ))}
          </div>
        ) : (
          <p className="text-center py-8 text-gray-500 text-sm">
            아직 리뷰가 없습니다. 첫 번째 리뷰를 작성해보세요!
          </p>
        )}
      </div>

      {/* Price Alert Modal */}
      {showAlertModal && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50 px-4">
          <div className="bg-white rounded-xl shadow-2xl p-6 w-full max-w-sm">
            <h3 className="text-lg font-bold text-gray-900 mb-4">가격 알림 설정</h3>
            <p className="text-sm text-gray-600 mb-4">
              목표 가격 이하로 내려가면 알림을 보내드립니다.
            </p>
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                목표 가격 (원)
              </label>
              <input
                type="number"
                value={targetPrice}
                onChange={(e) => setTargetPrice(e.target.value)}
                placeholder="예: 500000"
                min={0}
                className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none"
              />
            </div>
            <div className="flex justify-end gap-2">
              <button
                onClick={() => {
                  setShowAlertModal(false);
                  setTargetPrice('');
                }}
                className="px-4 py-2 text-sm text-gray-600 hover:text-gray-800 transition-colors"
              >
                취소
              </button>
              <button
                onClick={handleCreateAlert}
                disabled={alertMutation.isPending}
                className="px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 disabled:opacity-50 transition-colors"
              >
                {alertMutation.isPending ? '설정 중...' : '알림 설정'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
