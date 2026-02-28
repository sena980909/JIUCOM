interface ReviewCardProps {
  review: {
    id: number;
    content: string;
    rating: number;
    authorNickname: string;
    authorId: number;
    createdAt: string;
  };
  currentUserId?: number;
  onEdit?: (reviewId: number) => void;
  onDelete?: (reviewId: number) => void;
}

function formatDate(dateStr: string): string {
  const date = new Date(dateStr);
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });
}

function StarRating({ rating }: { rating: number }) {
  return (
    <div className="flex gap-0.5">
      {[1, 2, 3, 4, 5].map((star) => (
        <span
          key={star}
          className={`text-sm ${star <= rating ? 'text-yellow-400' : 'text-gray-300'}`}
        >
          ★
        </span>
      ))}
    </div>
  );
}

export default function ReviewCard({
  review,
  currentUserId,
  onEdit,
  onDelete,
}: ReviewCardProps) {
  const isOwner = currentUserId != null && currentUserId === review.authorId;

  return (
    <div className="bg-white border border-gray-200 rounded-lg p-4">
      <div className="flex items-start justify-between mb-2">
        <div>
          <div className="flex items-center gap-2 mb-1">
            <span className="text-sm font-semibold text-gray-900">
              {review.authorNickname}
            </span>
            <StarRating rating={review.rating} />
          </div>
          <p className="text-xs text-gray-500">{formatDate(review.createdAt)}</p>
        </div>

        {isOwner && (
          <div className="flex gap-1 shrink-0">
            {onEdit && (
              <button
                onClick={() => onEdit(review.id)}
                className="px-2 py-1 text-xs text-gray-600 hover:text-blue-600 hover:bg-blue-50 rounded transition-colors"
              >
                수정
              </button>
            )}
            {onDelete && (
              <button
                onClick={() => {
                  if (window.confirm('리뷰를 삭제하시겠습니까?')) {
                    onDelete(review.id);
                  }
                }}
                className="px-2 py-1 text-xs text-gray-600 hover:text-red-600 hover:bg-red-50 rounded transition-colors"
              >
                삭제
              </button>
            )}
          </div>
        )}
      </div>

      <p className="text-sm text-gray-700 leading-relaxed whitespace-pre-wrap">
        {review.content}
      </p>
    </div>
  );
}
