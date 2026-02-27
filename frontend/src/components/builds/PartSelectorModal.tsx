import { useState, useEffect, useCallback, useRef } from 'react';
import { useQuery } from '@tanstack/react-query';
import { getParts, type Part, type PageResponse } from '../../api/parts';
import { useDebounce } from '../../hooks/useDebounce';

interface PartSelectorModalProps {
  isOpen: boolean;
  category: string;
  categoryLabel: string;
  onSelect: (part: Part) => void;
  onClose: () => void;
}

const FALLBACK_IMAGE = 'data:image/svg+xml,' + encodeURIComponent(
  '<svg xmlns="http://www.w3.org/2000/svg" width="80" height="80" fill="none"><rect width="80" height="80" rx="8" fill="#f3f4f6"/><path d="M28 52l8-10 6 7 8-10 10 13H28z" fill="#d1d5db"/><circle cx="34" cy="34" r="4" fill="#d1d5db"/></svg>'
);

export default function PartSelectorModal({
  isOpen,
  category,
  categoryLabel,
  onSelect,
  onClose,
}: PartSelectorModalProps) {
  const [keyword, setKeyword] = useState('');
  const [page, setPage] = useState(0);
  const [allParts, setAllParts] = useState<Part[]>([]);
  const [hasMore, setHasMore] = useState(true);
  const debouncedKeyword = useDebounce(keyword, 300);
  const inputRef = useRef<HTMLInputElement>(null);

  // Reset state when modal opens or search changes
  useEffect(() => {
    if (isOpen) {
      setPage(0);
      setAllParts([]);
      setHasMore(true);
    }
  }, [isOpen, debouncedKeyword]);

  // Reset keyword when modal closes
  useEffect(() => {
    if (!isOpen) {
      setKeyword('');
      setPage(0);
      setAllParts([]);
    }
  }, [isOpen]);

  // Focus search input when modal opens
  useEffect(() => {
    if (isOpen) {
      setTimeout(() => inputRef.current?.focus(), 100);
    }
  }, [isOpen]);

  // Lock body scroll when open
  useEffect(() => {
    if (isOpen) {
      document.body.style.overflow = 'hidden';
    }
    return () => {
      document.body.style.overflow = '';
    };
  }, [isOpen]);

  // Close on ESC
  useEffect(() => {
    if (!isOpen) return;
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') onClose();
    };
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, onClose]);

  const { data, isLoading, isFetching } = useQuery({
    queryKey: ['parts-modal', category, debouncedKeyword, page],
    queryFn: () =>
      getParts({
        category,
        keyword: debouncedKeyword || undefined,
        sort: 'price_asc',
        page,
        size: 20,
      }),
    enabled: isOpen,
  });

  // Extract parts from response
  const extractParts = useCallback((raw: unknown): { parts: Part[]; totalPages: number } => {
    if (!raw) return { parts: [], totalPages: 0 };
    const obj = raw as Record<string, unknown>;
    if ('content' in obj) {
      const pr = obj as unknown as PageResponse<Part>;
      return { parts: pr.content || [], totalPages: pr.totalPages || 0 };
    }
    if ('data' in obj) {
      const inner = obj.data as Record<string, unknown>;
      if (inner && 'content' in inner) {
        const pr = inner as unknown as PageResponse<Part>;
        return { parts: pr.content || [], totalPages: pr.totalPages || 0 };
      }
    }
    return { parts: [], totalPages: 0 };
  }, []);

  // Append parts when data changes
  useEffect(() => {
    if (!data) return;
    const { parts, totalPages } = extractParts(data);
    if (page === 0) {
      setAllParts(parts);
    } else {
      setAllParts((prev) => [...prev, ...parts]);
    }
    setHasMore(page + 1 < totalPages);
  }, [data, page, extractParts]);

  const handleLoadMore = () => {
    setPage((prev) => prev + 1);
  };

  const handleSearchChange = (value: string) => {
    setKeyword(value);
    setPage(0);
    setAllParts([]);
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      {/* Backdrop */}
      <div
        className="absolute inset-0 bg-black/50"
        onClick={onClose}
      />

      {/* Modal */}
      <div className="relative bg-white rounded-xl shadow-2xl w-full max-w-2xl max-h-[85vh] flex flex-col">
        {/* Header */}
        <div className="flex items-center justify-between px-5 py-4 border-b border-gray-200">
          <h2 className="text-lg font-bold text-gray-900">{categoryLabel} 선택</h2>
          <button
            type="button"
            onClick={onClose}
            className="w-8 h-8 flex items-center justify-center rounded-full hover:bg-gray-100 text-gray-500 hover:text-gray-700 transition-colors"
          >
            <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        {/* Search */}
        <div className="px-5 py-3 border-b border-gray-100">
          <div className="relative">
            <svg
              className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
            <input
              ref={inputRef}
              type="text"
              value={keyword}
              onChange={(e) => handleSearchChange(e.target.value)}
              placeholder="부품명으로 검색..."
              className="w-full pl-10 pr-4 py-2.5 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            />
          </div>
        </div>

        {/* Content */}
        <div className="flex-1 overflow-y-auto px-5 py-4">
          {!debouncedKeyword && allParts.length > 0 && (
            <p className="text-xs font-semibold text-gray-500 uppercase tracking-wider mb-3">
              추천 부품
            </p>
          )}

          {isLoading && page === 0 ? (
            <div className="flex items-center justify-center py-16">
              <div className="w-7 h-7 border-3 border-blue-500 border-t-transparent rounded-full animate-spin" />
            </div>
          ) : allParts.length === 0 && !isFetching ? (
            <div className="flex flex-col items-center justify-center py-16 text-gray-400">
              <svg className="w-12 h-12 mb-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
              </svg>
              <p className="text-sm">검색 결과가 없습니다</p>
            </div>
          ) : (
            <>
              <div className="grid grid-cols-2 sm:grid-cols-2 gap-3">
                {allParts.map((part) => (
                  <button
                    key={part.id}
                    type="button"
                    onClick={() => onSelect(part)}
                    className="text-left border border-gray-200 rounded-lg p-3 hover:border-blue-400 hover:shadow-md transition-all group"
                  >
                    <div className="flex items-start gap-3">
                      <img
                        src={part.imageUrl || FALLBACK_IMAGE}
                        alt={part.name}
                        className="w-16 h-16 sm:w-20 sm:h-20 object-contain rounded-md bg-gray-50 shrink-0"
                        onError={(e) => {
                          (e.target as HTMLImageElement).src = FALLBACK_IMAGE;
                        }}
                      />
                      <div className="flex-1 min-w-0">
                        <p className="text-sm font-medium text-gray-900 line-clamp-2 group-hover:text-blue-600 transition-colors">
                          {part.name}
                        </p>
                        <p className="text-xs text-gray-500 mt-0.5">{part.manufacturer}</p>
                        <p className="text-sm font-bold text-blue-600 mt-1.5">
                          {part.lowestPrice != null
                            ? `${part.lowestPrice.toLocaleString()}원`
                            : '가격 미정'}
                        </p>
                      </div>
                    </div>
                    <div className="mt-2 text-center">
                      <span className="inline-block w-full py-1.5 text-xs font-medium text-blue-600 bg-blue-50 rounded-md group-hover:bg-blue-600 group-hover:text-white transition-colors">
                        선택
                      </span>
                    </div>
                  </button>
                ))}
              </div>

              {/* Load More */}
              {hasMore && (
                <div className="mt-4 text-center">
                  <button
                    type="button"
                    onClick={handleLoadMore}
                    disabled={isFetching}
                    className="px-6 py-2 text-sm font-medium text-gray-600 bg-gray-100 rounded-lg hover:bg-gray-200 disabled:opacity-50 transition-colors"
                  >
                    {isFetching ? (
                      <span className="flex items-center gap-2">
                        <span className="w-4 h-4 border-2 border-gray-400 border-t-transparent rounded-full animate-spin" />
                        로딩 중...
                      </span>
                    ) : (
                      '더 보기'
                    )}
                  </button>
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
}
