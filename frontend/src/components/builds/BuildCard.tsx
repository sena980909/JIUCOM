import { Link } from 'react-router-dom';

interface BuildCardProps {
  build: {
    id: number;
    name: string;
    description: string;
    totalPrice: number;
    partCount: number;
    ownerNickname: string;
    createdAt: string;
    isPublic: boolean;
    viewCount?: number;
    likeCount?: number;
  };
}

function formatPrice(price: number): string {
  return price.toLocaleString('ko-KR');
}

function formatDate(dateStr: string): string {
  const date = new Date(dateStr);
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  });
}

function truncate(text: string, maxLength: number): string {
  if (!text) return '';
  if (text.length <= maxLength) return text;
  return text.slice(0, maxLength) + '...';
}

export default function BuildCard({ build }: BuildCardProps) {
  return (
    <Link
      to={`/builds/${build.id}`}
      className="block bg-white border border-gray-200 rounded-lg shadow-sm hover:shadow-md transition-shadow duration-200"
    >
      <div className="p-5">
        <div className="flex items-start justify-between mb-2">
          <h3 className="text-lg font-semibold text-gray-900 leading-tight">
            {build.name}
          </h3>
          {!build.isPublic && (
            <span className="ml-2 shrink-0 inline-flex items-center rounded-md bg-gray-100 px-2 py-0.5 text-xs font-medium text-gray-600">
              비공개
            </span>
          )}
        </div>

        {build.description && (
          <p className="text-sm text-gray-500 mb-3 leading-relaxed">
            {truncate(build.description, 80)}
          </p>
        )}

        <div className="text-xl font-bold text-blue-600 mb-3">
          {formatPrice(build.totalPrice)}원
        </div>

        <div className="flex items-center justify-between text-sm text-gray-500">
          <div className="flex items-center gap-3">
            <span className="inline-flex items-center rounded-full bg-blue-50 px-2.5 py-0.5 text-xs font-medium text-blue-700">
              부품 {build.partCount}개
            </span>
            <span>{build.ownerNickname}</span>
          </div>
          <span>{formatDate(build.createdAt)}</span>
        </div>

        {(build.viewCount !== undefined || build.likeCount !== undefined) && (
          <div className="flex items-center gap-3 mt-2 text-xs text-gray-400">
            {build.viewCount !== undefined && (
              <span className="flex items-center gap-1">
                <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                </svg>
                {build.viewCount}
              </span>
            )}
            {build.likeCount !== undefined && (
              <span className="flex items-center gap-1">
                <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                </svg>
                {build.likeCount}
              </span>
            )}
          </div>
        )}
      </div>
    </Link>
  );
}
