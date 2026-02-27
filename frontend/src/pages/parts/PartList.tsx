import { useSearchParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { getParts, type PartsParams } from '../../api/parts';
import PartCard from '../../components/parts/PartCard';
import PartFilter from '../../components/parts/PartFilter';

export default function PartList() {
  const [searchParams, setSearchParams] = useSearchParams();

  const currentPage = Number(searchParams.get('page') || '0');
  const category = searchParams.get('category') || undefined;
  const minPrice = searchParams.get('minPrice') ? Number(searchParams.get('minPrice')) : undefined;
  const maxPrice = searchParams.get('maxPrice') ? Number(searchParams.get('maxPrice')) : undefined;
  const sort = searchParams.get('sort') || undefined;
  const keyword = searchParams.get('keyword') || undefined;

  const params: PartsParams = {
    page: currentPage,
    size: 12,
    category,
    minPrice,
    maxPrice,
    sort,
    keyword,
  };

  const { data, isLoading, isError } = useQuery({
    queryKey: ['parts', params],
    queryFn: () => getParts(params),
  });

  const handleFilter = (values: {
    category?: string;
    minPrice?: number;
    maxPrice?: number;
    sort?: string;
  }) => {
    const newParams = new URLSearchParams();
    if (values.category) newParams.set('category', values.category);
    if (values.minPrice != null) newParams.set('minPrice', String(values.minPrice));
    if (values.maxPrice != null) newParams.set('maxPrice', String(values.maxPrice));
    if (values.sort) newParams.set('sort', values.sort);
    if (keyword) newParams.set('keyword', keyword);
    newParams.set('page', '0');
    setSearchParams(newParams);
  };

  const handlePageChange = (page: number) => {
    const newParams = new URLSearchParams(searchParams);
    newParams.set('page', String(page));
    setSearchParams(newParams);
  };

  // Unwrap ApiResponse wrapper if present
  const pageData = data && 'data' in data && (data as { data?: typeof data }).data
    ? (data as unknown as { data: typeof data }).data
    : data;

  const content = pageData?.content || [];
  const totalPages = pageData?.totalPages || 0;

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">부품 목록</h1>

      <PartFilter
        onFilter={handleFilter}
        initialValues={{ category, minPrice, maxPrice, sort }}
      />

      {isLoading && (
        <div className="flex justify-center py-20">
          <div className="w-10 h-10 border-4 border-blue-500 border-t-transparent rounded-full animate-spin" />
        </div>
      )}

      {isError && (
        <div className="text-center py-20">
          <p className="text-gray-500">부품 목록을 불러올 수 없습니다.</p>
        </div>
      )}

      {!isLoading && !isError && content.length === 0 && (
        <div className="text-center py-20">
          <svg className="w-16 h-16 mx-auto text-gray-300 mb-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <p className="text-gray-500">검색 결과가 없습니다.</p>
        </div>
      )}

      {!isLoading && !isError && content.length > 0 && (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">
            {content.map((part) => (
              <PartCard key={part.id} part={part} />
            ))}
          </div>

          {totalPages > 1 && (
            <div className="flex justify-center items-center gap-2 mt-10">
              <button
                onClick={() => handlePageChange(currentPage - 1)}
                disabled={currentPage === 0}
                className="px-3 py-2 text-sm border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
              >
                이전
              </button>

              {Array.from({ length: totalPages }, (_, i) => i)
                .filter((page) => {
                  const distance = Math.abs(page - currentPage);
                  return distance <= 2 || page === 0 || page === totalPages - 1;
                })
                .reduce<(number | 'ellipsis')[]>((acc, page, idx, arr) => {
                  if (idx > 0 && page - (arr[idx - 1] as number) > 1) {
                    acc.push('ellipsis');
                  }
                  acc.push(page);
                  return acc;
                }, [])
                .map((item, idx) =>
                  item === 'ellipsis' ? (
                    <span key={`ellipsis-${idx}`} className="px-2 text-gray-400">
                      ...
                    </span>
                  ) : (
                    <button
                      key={item}
                      onClick={() => handlePageChange(item)}
                      className={`w-10 h-10 text-sm rounded-lg transition-colors ${
                        item === currentPage
                          ? 'bg-blue-600 text-white font-semibold'
                          : 'border border-gray-300 hover:bg-gray-50 text-gray-700'
                      }`}
                    >
                      {item + 1}
                    </button>
                  ),
                )}

              <button
                onClick={() => handlePageChange(currentPage + 1)}
                disabled={currentPage >= totalPages - 1}
                className="px-3 py-2 text-sm border border-gray-300 rounded-lg hover:bg-gray-50 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
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
