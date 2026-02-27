import { useState } from 'react';
import { Link } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { getPosts, type PageResponse, type Post } from '../../api/posts';
import PostCard from '../../components/posts/PostCard';
import { useAuth } from '../../hooks/useAuth';
import { useDebounce } from '../../hooks/useDebounce';

const BOARD_TABS = [
  { value: '', label: '전체' },
  { value: 'FREE', label: '자유게시판' },
  { value: 'QNA', label: '질문답변' },
  { value: 'REVIEW', label: '리뷰' },
  { value: 'NOTICE', label: '공지사항' },
] as const;

export default function PostList() {
  const { isAuthenticated } = useAuth();
  const [boardType, setBoardType] = useState('');
  const [keyword, setKeyword] = useState('');
  const [page, setPage] = useState(0);
  const pageSize = 10;

  const debouncedKeyword = useDebounce(keyword, 300);

  const { data, isLoading, isError } = useQuery<PageResponse<Post>>({
    queryKey: ['posts', boardType, debouncedKeyword, page],
    queryFn: async () => {
      const result = await getPosts({
        boardType: boardType || undefined,
        keyword: debouncedKeyword || undefined,
        page,
        size: pageSize,
      });
      // Handle ApiResponse wrapper
      return (result as { data?: PageResponse<Post> }).data ?? result;
    },
  });

  function handleTabChange(newBoardType: string) {
    setBoardType(newBoardType);
    setPage(0);
  }

  function handleSearch(e: React.ChangeEvent<HTMLInputElement>) {
    setKeyword(e.target.value);
    setPage(0);
  }

  const totalPages = data?.totalPages ?? 0;

  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-900">커뮤니티</h1>
        {isAuthenticated && (
          <Link
            to="/posts/new"
            className="px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 transition-colors"
          >
            글쓰기
          </Link>
        )}
      </div>

      {/* Board Type Tabs */}
      <div className="flex gap-1 border-b border-gray-200 mb-4">
        {BOARD_TABS.map((tab) => (
          <button
            key={tab.value}
            onClick={() => handleTabChange(tab.value)}
            className={`px-4 py-2.5 text-sm font-medium transition-colors border-b-2 -mb-px ${
              boardType === tab.value
                ? 'border-blue-600 text-blue-600'
                : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
            }`}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* Search */}
      <div className="mb-6">
        <div className="relative">
          <svg
            className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400"
            fill="none"
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
            />
          </svg>
          <input
            type="text"
            value={keyword}
            onChange={handleSearch}
            placeholder="게시글 검색..."
            className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
          />
        </div>
      </div>

      {/* Loading state */}
      {isLoading && (
        <div className="py-16 text-center">
          <svg
            className="animate-spin w-8 h-8 text-blue-600 mx-auto mb-3"
            fill="none"
            viewBox="0 0 24 24"
          >
            <circle
              className="opacity-25"
              cx="12"
              cy="12"
              r="10"
              stroke="currentColor"
              strokeWidth="4"
            />
            <path
              className="opacity-75"
              fill="currentColor"
              d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
            />
          </svg>
          <p className="text-sm text-gray-500">게시글을 불러오는 중...</p>
        </div>
      )}

      {/* Error state */}
      {isError && (
        <div className="py-16 text-center">
          <p className="text-sm text-red-500">게시글을 불러오는데 실패했습니다.</p>
        </div>
      )}

      {/* Post List */}
      {!isLoading && !isError && data && (
        <>
          {data.content.length === 0 ? (
            <div className="py-16 text-center">
              <p className="text-sm text-gray-400">게시글이 없습니다.</p>
            </div>
          ) : (
            <div>
              {data.content.map((post) => (
                <PostCard key={post.id} post={post} />
              ))}
            </div>
          )}

          {/* Pagination */}
          {totalPages > 1 && (
            <div className="flex items-center justify-center gap-1 mt-8">
              <button
                onClick={() => setPage((p) => Math.max(0, p - 1))}
                disabled={page === 0}
                className="px-3 py-1.5 text-sm border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                이전
              </button>

              {Array.from({ length: totalPages }, (_, i) => i)
                .filter((i) => {
                  // Show first, last, current, and neighbors
                  if (i === 0 || i === totalPages - 1) return true;
                  if (Math.abs(i - page) <= 2) return true;
                  return false;
                })
                .reduce<(number | 'ellipsis')[]>((acc, curr, idx, arr) => {
                  if (idx > 0) {
                    const prev = arr[idx - 1];
                    if (curr - prev > 1) {
                      acc.push('ellipsis');
                    }
                  }
                  acc.push(curr);
                  return acc;
                }, [])
                .map((item, idx) =>
                  item === 'ellipsis' ? (
                    <span key={`ellipsis-${idx}`} className="px-2 py-1.5 text-sm text-gray-400">
                      ...
                    </span>
                  ) : (
                    <button
                      key={item}
                      onClick={() => setPage(item)}
                      className={`px-3 py-1.5 text-sm rounded-lg transition-colors ${
                        page === item
                          ? 'bg-blue-600 text-white'
                          : 'border border-gray-300 hover:bg-gray-50'
                      }`}
                    >
                      {item + 1}
                    </button>
                  ),
                )}

              <button
                onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
                disabled={page >= totalPages - 1}
                className="px-3 py-1.5 text-sm border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                다음
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
}
