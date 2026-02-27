import { useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { getPartDetail } from '../../api/parts';
import { getSpecsSummary } from '../../utils/specsHelper';

const FALLBACK_IMAGE = 'data:image/svg+xml,' + encodeURIComponent(
  '<svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" fill="none"><rect width="48" height="48" rx="6" fill="#f3f4f6"/><path d="M16 34l5-7 4 5 5-7 7 9H16z" fill="#d1d5db"/><circle cx="20" cy="20" r="2.5" fill="#d1d5db"/></svg>'
);

export interface SelectedPartInfo {
  partId: number;
  partName: string;
  category: string;
  manufacturer: string;
  lowestPrice: number | null;
  quantity: number;
  imageUrl?: string;
  specs?: Record<string, unknown>;
}

interface SelectedPartCardProps {
  part: SelectedPartInfo;
  onQuantityChange: (quantity: number) => void;
  onChange: () => void;
  onRemove: () => void;
  onUpdatePartInfo: (updates: Partial<SelectedPartInfo>) => void;
}

export default function SelectedPartCard({
  part,
  onQuantityChange,
  onChange,
  onRemove,
  onUpdatePartInfo,
}: SelectedPartCardProps) {
  const needsFetch = !part.imageUrl && !part.specs;

  const { data: detail } = useQuery({
    queryKey: ['partDetail', part.partId],
    queryFn: () => getPartDetail(part.partId),
    enabled: needsFetch,
    staleTime: 5 * 60 * 1000,
  });

  useEffect(() => {
    if (detail && needsFetch) {
      const updates: Partial<SelectedPartInfo> = {};
      if (detail.imageUrl) updates.imageUrl = detail.imageUrl;
      if (detail.specs) updates.specs = detail.specs;
      if (Object.keys(updates).length > 0) {
        onUpdatePartInfo(updates);
      }
    }
  }, [detail, needsFetch, onUpdatePartInfo]);

  const specsSummary = getSpecsSummary(part.category, part.specs);
  const totalPrice = part.lowestPrice != null ? part.lowestPrice * part.quantity : null;

  return (
    <div className="flex items-start gap-3">
      {/* Thumbnail */}
      <img
        src={part.imageUrl || FALLBACK_IMAGE}
        alt={part.partName}
        className="w-12 h-12 object-contain rounded-md bg-gray-50 shrink-0"
        onError={(e) => {
          (e.target as HTMLImageElement).src = FALLBACK_IMAGE;
        }}
      />

      {/* Info */}
      <div className="flex-1 min-w-0">
        <p className="text-sm font-medium text-gray-900 truncate">{part.partName}</p>
        <div className="flex items-center gap-1.5 mt-0.5">
          <span className="text-[11px] bg-gray-100 text-gray-600 px-1.5 py-0.5 rounded font-medium">
            {part.manufacturer}
          </span>
          {specsSummary && (
            <span className="text-[11px] text-gray-400 truncate">{specsSummary}</span>
          )}
        </div>

        {/* Price + Quantity + Actions */}
        <div className="flex items-center justify-between mt-2">
          <div className="flex items-center gap-2">
            {/* Quantity */}
            <div className="flex items-center gap-0.5">
              <button
                type="button"
                onClick={() => onQuantityChange(part.quantity - 1)}
                disabled={part.quantity <= 1}
                className="w-6 h-6 flex items-center justify-center rounded border border-gray-300 text-gray-600 hover:bg-gray-100 disabled:opacity-40 disabled:cursor-not-allowed text-xs"
              >
                -
              </button>
              <span className="w-7 text-center text-xs font-medium">{part.quantity}</span>
              <button
                type="button"
                onClick={() => onQuantityChange(part.quantity + 1)}
                className="w-6 h-6 flex items-center justify-center rounded border border-gray-300 text-gray-600 hover:bg-gray-100 text-xs"
              >
                +
              </button>
            </div>

            {/* Price */}
            <span className="text-sm font-bold text-blue-600">
              {totalPrice != null ? `${totalPrice.toLocaleString()}원` : '가격 미정'}
            </span>
            {part.quantity > 1 && part.lowestPrice != null && (
              <span className="text-[10px] text-gray-400">
                (@{part.lowestPrice.toLocaleString()})
              </span>
            )}
          </div>

          {/* Actions */}
          <div className="flex items-center gap-1">
            <button
              type="button"
              onClick={onChange}
              className="text-[11px] text-gray-500 hover:text-blue-600 font-medium px-1.5 py-0.5 rounded hover:bg-blue-50 transition-colors"
            >
              변경
            </button>
            <button
              type="button"
              onClick={onRemove}
              className="text-[11px] text-gray-500 hover:text-red-600 font-medium px-1.5 py-0.5 rounded hover:bg-red-50 transition-colors"
            >
              제거
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
