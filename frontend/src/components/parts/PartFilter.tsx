import { useState } from 'react';

interface FilterValues {
  category?: string;
  minPrice?: number;
  maxPrice?: number;
  sort?: string;
}

interface PartFilterProps {
  onFilter: (values: FilterValues) => void;
  initialValues?: FilterValues;
}

const categories = [
  { value: '', label: '전체 카테고리' },
  { value: 'CPU', label: 'CPU' },
  { value: 'GPU', label: '그래픽카드' },
  { value: 'MOTHERBOARD', label: '메인보드' },
  { value: 'RAM', label: '메모리' },
  { value: 'SSD', label: 'SSD' },
  { value: 'HDD', label: 'HDD' },
  { value: 'PSU', label: '파워서플라이' },
  { value: 'CASE', label: '케이스' },
  { value: 'COOLER', label: '쿨러' },
];

const sortOptions = [
  { value: '', label: '기본 정렬' },
  { value: 'price_asc', label: '가격 낮은 순' },
  { value: 'price_desc', label: '가격 높은 순' },
  { value: 'name_asc', label: '이름 순' },
  { value: 'newest', label: '최신 순' },
];

export default function PartFilter({ onFilter, initialValues = {} }: PartFilterProps) {
  const [category, setCategory] = useState(initialValues.category || '');
  const [minPrice, setMinPrice] = useState<string>(
    initialValues.minPrice != null ? String(initialValues.minPrice) : '',
  );
  const [maxPrice, setMaxPrice] = useState<string>(
    initialValues.maxPrice != null ? String(initialValues.maxPrice) : '',
  );
  const [sort, setSort] = useState(initialValues.sort || '');

  const handleApply = () => {
    const values: FilterValues = {};
    if (category) values.category = category;
    if (minPrice) values.minPrice = Number(minPrice);
    if (maxPrice) values.maxPrice = Number(maxPrice);
    if (sort) values.sort = sort;
    onFilter(values);
  };

  const handleReset = () => {
    setCategory('');
    setMinPrice('');
    setMaxPrice('');
    setSort('');
    onFilter({});
  };

  return (
    <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-4 mb-6">
      <div className="flex flex-wrap items-end gap-4">
        <div className="flex-1 min-w-[160px]">
          <label className="block text-xs font-medium text-gray-600 mb-1">
            카테고리
          </label>
          <select
            value={category}
            onChange={(e) => setCategory(e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none"
          >
            {categories.map((cat) => (
              <option key={cat.value} value={cat.value}>
                {cat.label}
              </option>
            ))}
          </select>
        </div>

        <div className="flex-1 min-w-[200px]">
          <label className="block text-xs font-medium text-gray-600 mb-1">
            가격 범위
          </label>
          <div className="flex items-center gap-2">
            <input
              type="number"
              value={minPrice}
              onChange={(e) => setMinPrice(e.target.value)}
              placeholder="최소"
              min={0}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none"
            />
            <span className="text-gray-400 text-sm shrink-0">~</span>
            <input
              type="number"
              value={maxPrice}
              onChange={(e) => setMaxPrice(e.target.value)}
              placeholder="최대"
              min={0}
              className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none"
            />
          </div>
        </div>

        <div className="flex-1 min-w-[140px]">
          <label className="block text-xs font-medium text-gray-600 mb-1">
            정렬
          </label>
          <select
            value={sort}
            onChange={(e) => setSort(e.target.value)}
            className="w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:ring-2 focus:ring-blue-500 focus:border-transparent outline-none"
          >
            {sortOptions.map((opt) => (
              <option key={opt.value} value={opt.value}>
                {opt.label}
              </option>
            ))}
          </select>
        </div>

        <div className="flex gap-2">
          <button
            onClick={handleApply}
            className="px-5 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 transition-colors"
          >
            적용
          </button>
          <button
            onClick={handleReset}
            className="px-5 py-2 bg-gray-100 text-gray-700 text-sm font-medium rounded-lg hover:bg-gray-200 transition-colors"
          >
            초기화
          </button>
        </div>
      </div>
    </div>
  );
}
