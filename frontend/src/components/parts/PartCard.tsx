import { Link } from 'react-router-dom';

interface PartCardProps {
  part: {
    id: number;
    name: string;
    category: string;
    manufacturer: string;
    lowestPrice?: number;
    imageUrl?: string;
  };
}

const categoryLabels: Record<string, string> = {
  CPU: 'CPU',
  GPU: '그래픽카드',
  MOTHERBOARD: '메인보드',
  RAM: '메모리',
  SSD: 'SSD',
  HDD: 'HDD',
  PSU: '파워서플라이',
  CASE: '케이스',
  COOLER: '쿨러',
};

const categoryColors: Record<string, string> = {
  CPU: 'bg-blue-100 text-blue-800',
  GPU: 'bg-purple-100 text-purple-800',
  MOTHERBOARD: 'bg-green-100 text-green-800',
  RAM: 'bg-yellow-100 text-yellow-800',
  SSD: 'bg-orange-100 text-orange-800',
  HDD: 'bg-red-100 text-red-800',
  PSU: 'bg-indigo-100 text-indigo-800',
  CASE: 'bg-pink-100 text-pink-800',
  COOLER: 'bg-cyan-100 text-cyan-800',
};

function formatPrice(price: number): string {
  return price.toLocaleString('ko-KR') + '원';
}

export default function PartCard({ part }: PartCardProps) {
  return (
    <Link
      to={`/parts/${part.id}`}
      className="block bg-white rounded-xl shadow-sm border border-gray-200 hover:shadow-md hover:border-gray-300 transition-all duration-200 overflow-hidden"
    >
      <div className="aspect-square bg-gray-100 flex items-center justify-center">
        {part.imageUrl ? (
          <img
            src={part.imageUrl}
            alt={part.name}
            className="w-full h-full object-contain p-4"
          />
        ) : (
          <div className="text-gray-400 flex flex-col items-center gap-2">
            <svg className="w-16 h-16" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2zM9 9h6v6H9V9z" />
            </svg>
            <span className="text-xs">이미지 없음</span>
          </div>
        )}
      </div>

      <div className="p-4">
        <div className="flex items-center gap-2 mb-2">
          <span
            className={`inline-block px-2 py-0.5 text-xs font-medium rounded-full ${
              categoryColors[part.category] || 'bg-gray-100 text-gray-800'
            }`}
          >
            {categoryLabels[part.category] || part.category}
          </span>
          <span className="text-xs text-gray-500">{part.manufacturer}</span>
        </div>

        <h3 className="text-sm font-semibold text-gray-900 line-clamp-2 mb-3 min-h-[2.5rem]">
          {part.name}
        </h3>

        <div className="text-right">
          {part.lowestPrice != null ? (
            <p className="text-lg font-bold text-blue-600">
              {formatPrice(part.lowestPrice)}
            </p>
          ) : (
            <p className="text-sm text-gray-400">가격 정보 없음</p>
          )}
        </div>
      </div>
    </Link>
  );
}
