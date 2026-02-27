import { useState, useEffect, useRef } from 'react';
import { useSearchParams, Link } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { search, suggest, type SearchParams, type SearchResult } from '../../api/search';
import { useDebounce } from '../../hooks/useDebounce';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import Pagination from '../../components/common/Pagination';

const TABS = [
  { key: '', label: '전체' },
  { key: 'PART', label: '부품' },
  { key: 'POST', label: '게시글' },
  { key: 'BUILD', label: '견적' },
] as const;

const TYPE_CONFIG: Record<string, { label: string; color: string; icon: string }> = {
  PART: { label: '부품', color: 'bg-blue-100 text-blue-700', icon: 'M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2z' },
  POST: { label: '게시글', color: 'bg-green-100 text-green-700', icon: 'M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z' },
  BUILD: { label: '견적', color: 'bg-purple-100 text-purple-700', icon: 'M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10' },
};

function getResultLink(result: SearchResult): string {
  return result.url || '#';
}

export default function SearchResults() {
  const [searchParams, setSearchParams] = useSearchParams();
  const keyword = searchParams.get('keyword') || '';
  const typeFilter = searchParams.get('type') || '';
  const pageParam = parseInt(searchParams.get('page') || '0', 10);

  const [inputValue, setInputValue] = useState(keyword);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const debouncedInput = useDebounce(inputValue, 300);
  const suggestRef = useRef<HTMLDivElement>(null);

  // Close suggestions dropdown on outside click
  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (suggestRef.current && !suggestRef.current.contains(event.target as Node)) {
        setShowSuggestions(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  // Sync input value with URL keyword
  useEffect(() => {
    setInputValue(keyword);
  }, [keyword]);

  const searchQuery: SearchParams = {
    keyword,
    type: typeFilter || undefined,
    page: pageParam,
    size: 10,
  };

  const { data, isLoading, isError } = useQuery({
    queryKey: ['search', keyword, typeFilter, pageParam],
    queryFn: () => search(searchQuery),
    enabled: !!keyword,
  });

  const { data: suggestData } = useQuery({
    queryKey: ['suggest', debouncedInput],
    queryFn: () => suggest(debouncedInput),
    enabled: debouncedInput.trim().length >= 2 && showSuggestions,
  });

  const handleSearch = (value: string) => {
    const trimmed = value.trim();
    if (!trimmed) return;
    setShowSuggestions(false);
    setSearchParams({ keyword: trimmed, ...(typeFilter ? { type: typeFilter } : {}) });
  };

  const handleTabChange = (type: string) => {
    const params: Record<string, string> = { keyword };
    if (type) params.type = type;
    setSearchParams(params);
  };

  const handlePageChange = (page: number) => {
    const params: Record<string, string> = { keyword, page: String(page) };
    if (typeFilter) params.type = typeFilter;
    setSearchParams(params);
  };

  const handleSuggestionClick = (suggestion: string) => {
    setInputValue(suggestion);
    setShowSuggestions(false);
    setSearchParams({ keyword: suggestion, ...(typeFilter ? { type: typeFilter } : {}) });
  };

  const results = data?.content ?? [];
  const totalPages = data?.totalPages ?? 0;
  const totalElements = data?.totalElements ?? 0;
  const currentPage = data?.number ?? 0;

  return (
    <div className="mx-auto max-w-4xl px-4 py-8">
      {/* Search Bar */}
      <div className="relative mb-6" ref={suggestRef}>
        <div className="relative">
          <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-4">
            <svg className="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
          </div>
          <input
            type="text"
            value={inputValue}
            onChange={(e) => {
              setInputValue(e.target.value);
              setShowSuggestions(true);
            }}
            onFocus={() => setShowSuggestions(true)}
            onKeyDown={(e) => {
              if (e.key === 'Enter') handleSearch(inputValue);
            }}
            placeholder="검색어를 입력하세요..."
            className="w-full rounded-lg border border-gray-300 bg-white py-3 pl-12 pr-24 text-sm text-gray-900 placeholder-gray-500 focus:border-blue-500 focus:ring-1 focus:ring-blue-500 focus:outline-none transition-colors"
          />
          <button
            onClick={() => handleSearch(inputValue)}
            className="absolute right-2 top-1/2 -translate-y-1/2 rounded-md bg-blue-600 px-4 py-1.5 text-sm font-medium text-white hover:bg-blue-700 transition-colors"
          >
            검색
          </button>
        </div>

        {/* Auto-suggest dropdown */}
        {showSuggestions && suggestData?.suggestions && suggestData.suggestions.length > 0 && (
          <div className="absolute z-10 mt-1 w-full rounded-lg border border-gray-200 bg-white shadow-lg">
            {suggestData.suggestions.map((suggestion, index) => (
              <button
                key={index}
                onClick={() => handleSuggestionClick(suggestion)}
                className="flex w-full items-center gap-2 px-4 py-2.5 text-left text-sm text-gray-700 hover:bg-gray-50 transition-colors first:rounded-t-lg last:rounded-b-lg"
              >
                <svg className="h-4 w-4 shrink-0 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
                {suggestion}
              </button>
            ))}
          </div>
        )}
      </div>

      {/* Results header */}
      {keyword && (
        <div className="mb-4">
          <h1 className="text-xl font-bold text-gray-900">
            &apos;{keyword}&apos; 검색 결과
          </h1>
          {data && (
            <p className="mt-1 text-sm text-gray-500">
              총 {totalElements.toLocaleString()}건
            </p>
          )}
        </div>
      )}

      {/* Tabs */}
      <div className="mb-6 border-b border-gray-200">
        <nav className="flex gap-0">
          {TABS.map((tab) => (
            <button
              key={tab.key}
              onClick={() => handleTabChange(tab.key)}
              className={`border-b-2 px-4 py-2.5 text-sm font-medium transition-colors ${
                typeFilter === tab.key
                  ? 'border-blue-600 text-blue-600'
                  : 'border-transparent text-gray-500 hover:border-gray-300 hover:text-gray-700'
              }`}
            >
              {tab.label}
            </button>
          ))}
        </nav>
      </div>

      {/* Content */}
      {!keyword ? (
        <div className="py-20 text-center">
          <svg className="mx-auto h-12 w-12 text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
          </svg>
          <p className="mt-4 text-gray-500">검색어를 입력해주세요</p>
        </div>
      ) : isLoading ? (
        <LoadingSpinner />
      ) : isError ? (
        <div className="py-20 text-center">
          <p className="text-red-500">검색 중 오류가 발생했습니다. 다시 시도해주세요.</p>
        </div>
      ) : results.length === 0 ? (
        <div className="py-20 text-center">
          <svg className="mx-auto h-12 w-12 text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <p className="mt-4 text-gray-500">검색 결과가 없습니다</p>
          <p className="mt-1 text-sm text-gray-400">다른 검색어를 시도해보세요</p>
        </div>
      ) : (
        <>
          <div className="space-y-3">
            {results.map((result) => (
              <SearchResultCard key={`${result.type}-${result.id}`} result={result} />
            ))}
          </div>

          <div className="mt-8">
            <Pagination
              currentPage={currentPage}
              totalPages={totalPages}
              onPageChange={handlePageChange}
            />
          </div>
        </>
      )}
    </div>
  );
}

function SearchResultCard({ result }: { result: SearchResult }) {
  const config = TYPE_CONFIG[result.type] ?? {
    label: result.type,
    color: 'bg-gray-100 text-gray-700',
    icon: 'M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z',
  };

  return (
    <Link
      to={getResultLink(result)}
      className="block rounded-lg border border-gray-200 bg-white p-4 hover:shadow-md transition-shadow"
    >
      <div className="flex items-start gap-3">
        <div className="mt-0.5 shrink-0">
          <svg className="h-5 w-5 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d={config.icon} />
          </svg>
        </div>
        <div className="min-w-0 flex-1">
          <div className="flex items-center gap-2 mb-1">
            <span className={`inline-block rounded-full px-2 py-0.5 text-xs font-medium ${config.color}`}>
              {config.label}
            </span>
          </div>
          <h3 className="text-base font-semibold text-gray-900 truncate">
            {result.title}
          </h3>
          {result.description && (
            <p className="mt-1 text-sm text-gray-500 line-clamp-2 leading-relaxed">
              {result.description}
            </p>
          )}
        </div>
      </div>
    </Link>
  );
}
