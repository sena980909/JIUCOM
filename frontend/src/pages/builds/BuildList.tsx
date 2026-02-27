import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { getBuilds } from '../../api/builds';
import BuildCard from '../../components/builds/BuildCard';

export default function BuildList() {
  const [page, setPage] = useState(0);
  const size = 12;

  const { data, isLoading, isError } = useQuery({
    queryKey: ['builds', page, size],
    queryFn: () => getBuilds({ page, size }),
  });

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      {/* Header */}
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">공개 견적</h1>
          <p className="mt-1 text-sm text-gray-500">
            다른 사용자들이 공유한 PC 견적을 확인해보세요
          </p>
        </div>
        <Link
          to="/builds/new"
          className="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2.5 text-sm font-medium text-white hover:bg-blue-700 transition-colors"
        >
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
          새 견적 만들기
        </Link>
      </div>

      {/* Loading */}
      {isLoading && (
        <div className="flex items-center justify-center py-20">
          <div className="flex flex-col items-center gap-3">
            <div className="w-8 h-8 border-3 border-blue-500 border-t-transparent rounded-full animate-spin" />
            <span className="text-sm text-gray-500">견적 목록을 불러오는 중...</span>
          </div>
        </div>
      )}

      {/* Error */}
      {isError && (
        <div className="flex items-center justify-center py-20">
          <div className="text-center">
            <p className="text-sm text-red-500 mb-2">견적 목록을 불러오는데 실패했습니다.</p>
            <button
              onClick={() => setPage(0)}
              className="text-sm text-blue-600 hover:text-blue-700 underline"
            >
              다시 시도
            </button>
          </div>
        </div>
      )}

      {/* Empty */}
      {data && data.content.length === 0 && (
        <div className="flex flex-col items-center justify-center py-20">
          <svg className="w-16 h-16 text-gray-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M9 17v-2m3 2v-4m3 4v-6m2 10H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
          </svg>
          <p className="text-gray-500 mb-4">아직 공개된 견적이 없습니다.</p>
          <Link
            to="/builds/new"
            className="text-sm text-blue-600 hover:text-blue-700 underline"
          >
            첫 번째 견적을 만들어보세요
          </Link>
        </div>
      )}

      {/* Build Grid */}
      {data && data.content.length > 0 && (
        <>
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {data.content.map((build) => (
              <BuildCard key={build.id} build={build} />
            ))}
          </div>

          {/* Pagination */}
          {data.totalPages > 1 && (
            <div className="flex items-center justify-center gap-2 mt-8">
              <button
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                disabled={page === 0}
                className="px-3 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
              >
                이전
              </button>

              {Array.from({ length: data.totalPages }, (_, i) => i)
                .filter((i) => {
                  // Show first, last, and pages around current
                  if (i === 0 || i === data.totalPages - 1) return true;
                  if (Math.abs(i - page) <= 2) return true;
                  return false;
                })
                .reduce<(number | 'ellipsis')[]>((acc, curr, idx, arr) => {
                  if (idx > 0 && curr - (arr[idx - 1] as number) > 1) {
                    acc.push('ellipsis');
                  }
                  acc.push(curr);
                  return acc;
                }, [])
                .map((item, index) =>
                  item === 'ellipsis' ? (
                    <span key={`ellipsis-${index}`} className="px-2 text-gray-400">
                      ...
                    </span>
                  ) : (
                    <button
                      key={item}
                      onClick={() => setPage(item)}
                      className={`px-3 py-2 text-sm font-medium rounded-md transition-colors ${
                        page === item
                          ? 'bg-blue-600 text-white'
                          : 'text-gray-700 bg-white border border-gray-300 hover:bg-gray-50'
                      }`}
                    >
                      {item + 1}
                    </button>
                  ),
                )}

              <button
                onClick={() => setPage((p) => Math.min(data.totalPages - 1, p + 1))}
                disabled={page >= data.totalPages - 1}
                className="px-3 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
              >
                다음
              </button>
            </div>
          )}

          {/* Page info */}
          <div className="text-center mt-3 text-xs text-gray-400">
            총 {data.totalElements}개 중 {page * size + 1}-{Math.min((page + 1) * size, data.totalElements)}
          </div>
        </>
      )}
    </div>
  );
}
