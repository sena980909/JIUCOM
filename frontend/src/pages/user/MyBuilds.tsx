import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { getMyBuilds } from '../../api/builds';
import { useAuth } from '../../hooks/useAuth';
import BuildCard from '../../components/builds/BuildCard';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import Pagination from '../../components/common/Pagination';

export default function MyBuilds() {
  const { isAuthenticated } = useAuth();
  const [page, setPage] = useState(0);

  const { data, isLoading, isError } = useQuery({
    queryKey: ['myBuilds', page],
    queryFn: () => getMyBuilds({ page, size: 12 }),
    enabled: isAuthenticated,
  });

  const builds = data?.content ?? [];
  const totalPages = data?.totalPages ?? 0;
  const currentPage = data?.number ?? 0;

  if (!isAuthenticated) {
    return (
      <div className="py-20 text-center">
        <p className="text-gray-500">로그인이 필요합니다</p>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-5xl px-4 py-8">
      <div className="mb-6 flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">내 견적</h1>
        <Link
          to="/builds/new"
          className="inline-flex items-center gap-1.5 rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700 transition-colors"
        >
          <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
          새 견적 만들기
        </Link>
      </div>

      {isLoading ? (
        <LoadingSpinner />
      ) : isError ? (
        <div className="py-20 text-center">
          <p className="text-red-500">견적 목록을 불러오는 중 오류가 발생했습니다.</p>
        </div>
      ) : builds.length === 0 ? (
        <div className="py-20 text-center">
          <svg className="mx-auto h-12 w-12 text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
          </svg>
          <p className="mt-4 text-gray-500">아직 만든 견적이 없습니다</p>
          <Link
            to="/builds/new"
            className="mt-4 inline-flex items-center gap-1.5 rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700 transition-colors"
          >
            <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
            </svg>
            첫 견적 만들기
          </Link>
        </div>
      ) : (
        <>
          <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {builds.map((build) => (
              <BuildCard
                key={build.id}
                build={build}
              />
            ))}
          </div>

          <div className="mt-8">
            <Pagination
              currentPage={currentPage}
              totalPages={totalPages}
              onPageChange={setPage}
            />
          </div>
        </>
      )}
    </div>
  );
}
