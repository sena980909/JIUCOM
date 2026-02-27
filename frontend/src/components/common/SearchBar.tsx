import { useState, type KeyboardEvent } from 'react';
import { useDebounce } from '../../hooks/useDebounce';
import { useEffect } from 'react';

interface SearchBarProps {
  onSearch: (keyword: string) => void;
  placeholder?: string;
  initialValue?: string;
  debounceDelay?: number;
}

export default function SearchBar({
  onSearch,
  placeholder = '검색...',
  initialValue = '',
  debounceDelay = 300,
}: SearchBarProps) {
  const [value, setValue] = useState(initialValue);
  const debouncedValue = useDebounce(value, debounceDelay);

  useEffect(() => {
    if (debouncedValue !== initialValue && debouncedValue.trim()) {
      onSearch(debouncedValue);
    }
  }, [debouncedValue]); // eslint-disable-line react-hooks/exhaustive-deps

  const handleKeyDown = (e: KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter') {
      onSearch(value);
    }
  };

  return (
    <div className="relative">
      <div className="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
        <svg
          className="h-4 w-4 text-gray-400"
          fill="none"
          viewBox="0 0 24 24"
          stroke="currentColor"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={2}
            d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
          />
        </svg>
      </div>
      <input
        type="text"
        value={value}
        onChange={(e) => setValue(e.target.value)}
        onKeyDown={handleKeyDown}
        placeholder={placeholder}
        className="w-full rounded-lg border border-gray-300 bg-white py-2 pl-10 pr-4 text-sm text-gray-900 placeholder-gray-500 focus:border-blue-500 focus:ring-1 focus:ring-blue-500 focus:outline-none transition-colors"
      />
    </div>
  );
}
