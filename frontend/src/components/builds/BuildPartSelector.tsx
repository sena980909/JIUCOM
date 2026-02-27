import { useState, useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { getParts, type Part } from '../../api/parts';
import { useDebounce } from '../../hooks/useDebounce';

interface SelectedPartInfo {
  partId: number;
  partName: string;
  category: string;
  manufacturer: string;
  lowestPrice: number | null;
  quantity: number;
}

interface BuildPartSelectorProps {
  category: string;
  categoryLabel: string;
  selectedPart: SelectedPartInfo | null;
  onSelect: (part: SelectedPartInfo | null) => void;
}

export default function BuildPartSelector({
  category,
  categoryLabel,
  selectedPart,
  onSelect,
}: BuildPartSelectorProps) {
  const [searchKeyword, setSearchKeyword] = useState('');
  const [isOpen, setIsOpen] = useState(false);
  const debouncedKeyword = useDebounce(searchKeyword, 300);

  const { data: partsData, isLoading } = useQuery({
    queryKey: ['parts', category, debouncedKeyword],
    queryFn: () =>
      getParts({
        category,
        keyword: debouncedKeyword || undefined,
        size: 20,
      }),
    enabled: isOpen,
  });

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      const target = e.target as HTMLElement;
      if (!target.closest(`[data-selector="${category}"]`)) {
        setIsOpen(false);
      }
    };
    if (isOpen) {
      document.addEventListener('mousedown', handleClickOutside);
    }
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [isOpen, category]);

  const handleSelectPart = (part: Part) => {
    onSelect({
      partId: part.id,
      partName: part.name,
      category: part.category,
      manufacturer: part.manufacturer,
      lowestPrice: part.lowestPrice ?? null,
      quantity: 1,
    });
    setIsOpen(false);
    setSearchKeyword('');
  };

  const handleRemove = () => {
    onSelect(null);
  };

  const handleQuantityChange = (newQuantity: number) => {
    if (selectedPart && newQuantity >= 1) {
      onSelect({ ...selectedPart, quantity: newQuantity });
    }
  };

  // Extract parts from the API response (may be wrapped in ApiResponse)
  const parts: Part[] = (() => {
    if (!partsData) return [];
    const raw = partsData as unknown;
    if (raw && typeof raw === 'object' && 'content' in (raw as Record<string, unknown>)) {
      return (raw as { content: Part[] }).content;
    }
    if (raw && typeof raw === 'object' && 'data' in (raw as Record<string, unknown>)) {
      const inner = (raw as { data: unknown }).data;
      if (inner && typeof inner === 'object' && 'content' in (inner as Record<string, unknown>)) {
        return (inner as { content: Part[] }).content;
      }
    }
    return [];
  })();

  return (
    <div
      data-selector={category}
      className="border border-gray-200 rounded-lg p-4"
    >
      <div className="flex items-center justify-between mb-2">
        <label className="text-sm font-semibold text-gray-700">
          {categoryLabel}
        </label>
        {selectedPart && (
          <button
            type="button"
            onClick={handleRemove}
            className="text-xs text-red-500 hover:text-red-700 transition-colors"
          >
            제거
          </button>
        )}
      </div>

      {selectedPart ? (
        <div className="flex items-center justify-between bg-blue-50 rounded-md p-3">
          <div className="flex-1 min-w-0">
            <p className="text-sm font-medium text-gray-900 truncate">
              {selectedPart.partName}
            </p>
            <p className="text-xs text-gray-500">{selectedPart.manufacturer}</p>
          </div>
          <div className="flex items-center gap-3 ml-3">
            <div className="flex items-center gap-1">
              <button
                type="button"
                onClick={() => handleQuantityChange(selectedPart.quantity - 1)}
                disabled={selectedPart.quantity <= 1}
                className="w-6 h-6 flex items-center justify-center rounded border border-gray-300 text-gray-600 hover:bg-gray-100 disabled:opacity-40 disabled:cursor-not-allowed text-xs"
              >
                -
              </button>
              <span className="w-8 text-center text-sm font-medium">
                {selectedPart.quantity}
              </span>
              <button
                type="button"
                onClick={() => handleQuantityChange(selectedPart.quantity + 1)}
                className="w-6 h-6 flex items-center justify-center rounded border border-gray-300 text-gray-600 hover:bg-gray-100 text-xs"
              >
                +
              </button>
            </div>
            {selectedPart.lowestPrice != null && (
              <span className="text-sm font-semibold text-blue-600 whitespace-nowrap">
                {(selectedPart.lowestPrice * selectedPart.quantity).toLocaleString('ko-KR')}원
              </span>
            )}
          </div>
        </div>
      ) : (
        <div className="relative">
          <button
            type="button"
            onClick={() => setIsOpen(!isOpen)}
            className="w-full text-left px-3 py-2.5 border border-gray-300 rounded-md text-sm text-gray-500 hover:border-blue-400 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
          >
            부품을 선택하세요
          </button>

          {isOpen && (
            <div className="absolute z-10 mt-1 w-full bg-white border border-gray-200 rounded-md shadow-lg max-h-64 overflow-hidden">
              <div className="p-2 border-b border-gray-100">
                <input
                  type="text"
                  value={searchKeyword}
                  onChange={(e) => setSearchKeyword(e.target.value)}
                  placeholder="부품명으로 검색..."
                  className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                  autoFocus
                />
              </div>
              <div className="max-h-48 overflow-y-auto">
                {isLoading ? (
                  <div className="flex items-center justify-center py-4">
                    <div className="w-5 h-5 border-2 border-blue-500 border-t-transparent rounded-full animate-spin" />
                  </div>
                ) : parts.length === 0 ? (
                  <div className="px-3 py-4 text-sm text-gray-400 text-center">
                    검색 결과가 없습니다
                  </div>
                ) : (
                  parts.map((part) => (
                    <button
                      key={part.id}
                      type="button"
                      onClick={() => handleSelectPart(part)}
                      className="w-full text-left px-3 py-2.5 hover:bg-blue-50 transition-colors border-b border-gray-50 last:border-b-0"
                    >
                      <div className="flex items-center justify-between">
                        <div className="min-w-0 flex-1">
                          <p className="text-sm font-medium text-gray-900 truncate">
                            {part.name}
                          </p>
                          <p className="text-xs text-gray-500">{part.manufacturer}</p>
                        </div>
                        {part.lowestPrice != null && (
                          <span className="ml-2 text-sm font-medium text-blue-600 whitespace-nowrap">
                            {part.lowestPrice.toLocaleString('ko-KR')}원
                          </span>
                        )}
                      </div>
                    </button>
                  ))
                )}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
