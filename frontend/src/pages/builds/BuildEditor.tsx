import { useState, useEffect, useMemo, useCallback } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import {
  getBuildDetail,
  createBuild,
  updateBuild,
  type CreateBuildRequest,
  type UpdateBuildRequest,
} from '../../api/builds';
import { type Part } from '../../api/parts';
import { useAuth } from '../../hooks/useAuth';
import PartSelectorModal from '../../components/builds/PartSelectorModal';
import SelectedPartCard, { type SelectedPartInfo } from '../../components/builds/SelectedPartCard';
import CompatibilityWarning from '../../components/builds/CompatibilityWarning';

const CATEGORY_COLORS: Record<string, string> = {
  CPU: 'bg-red-500',
  MOTHERBOARD: 'bg-green-500',
  RAM: 'bg-blue-500',
  GPU: 'bg-purple-500',
  SSD: 'bg-yellow-500',
  HDD: 'bg-orange-500',
  POWER_SUPPLY: 'bg-cyan-500',
  CASE: 'bg-gray-500',
  COOLER: 'bg-teal-500',
};

const BUILD_CATEGORIES = [
  { key: 'CPU', label: 'CPU' },
  { key: 'MOTHERBOARD', label: '메인보드' },
  { key: 'RAM', label: '메모리 (RAM)' },
  { key: 'GPU', label: '그래픽카드 (GPU)' },
  { key: 'SSD', label: 'SSD' },
  { key: 'HDD', label: 'HDD' },
  { key: 'POWER_SUPPLY', label: '파워서플라이' },
  { key: 'CASE', label: '케이스' },
  { key: 'COOLER', label: '쿨러' },
] as const;

const REQUIRED_CATEGORIES = ['CPU', 'MOTHERBOARD', 'POWER_SUPPLY'];
const UNIQUE_CATEGORIES = ['CPU', 'MOTHERBOARD', 'CASE', 'POWER_SUPPLY'];

const THUMB_FALLBACK = 'data:image/svg+xml,' + encodeURIComponent(
  '<svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" fill="none"><rect width="32" height="32" rx="4" fill="#f3f4f6"/><path d="M10 22l4-5 3 4 4-6 5 7H10z" fill="#d1d5db"/></svg>'
);

export default function BuildEditor() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const isEditMode = !!id;
  const buildId = Number(id);

  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [isPublic, setIsPublic] = useState(false);
  const [selectedParts, setSelectedParts] = useState<Record<string, SelectedPartInfo | null>>(() => {
    const initial: Record<string, SelectedPartInfo | null> = {};
    BUILD_CATEGORIES.forEach((cat) => {
      initial[cat.key] = null;
    });
    return initial;
  });

  // Modal state
  const [modalCategory, setModalCategory] = useState<{ key: string; label: string } | null>(null);

  // Fetch existing build for edit mode
  const { data: existingBuild, isLoading: isBuildLoading } = useQuery({
    queryKey: ['build', buildId],
    queryFn: () => getBuildDetail(buildId),
    enabled: isEditMode && !isNaN(buildId),
  });

  // Pre-fill form when editing
  useEffect(() => {
    if (existingBuild) {
      setName(existingBuild.name || '');
      setDescription(existingBuild.description || '');
      setIsPublic(existingBuild.isPublic);

      const partsMap: Record<string, SelectedPartInfo | null> = {};
      BUILD_CATEGORIES.forEach((cat) => {
        partsMap[cat.key] = null;
      });

      existingBuild.parts.forEach((part) => {
        partsMap[part.category] = {
          partId: part.partId,
          partName: part.partName,
          category: part.category,
          manufacturer: part.manufacturer,
          lowestPrice: part.unitPrice,
          quantity: part.quantity,
        };
      });

      setSelectedParts(partsMap);
    }
  }, [existingBuild]);

  // Calculate total price
  const totalPrice = useMemo(() => {
    return Object.values(selectedParts).reduce((sum, part) => {
      if (part && part.lowestPrice != null) {
        return sum + part.lowestPrice * part.quantity;
      }
      return sum;
    }, 0);
  }, [selectedParts]);

  // Calculate compatibility warnings
  const compatibilityWarnings = useMemo(() => {
    const warnings: string[] = [];
    const activeParts = Object.values(selectedParts).filter(
      (p): p is SelectedPartInfo => p !== null,
    );

    if (activeParts.length === 0) return warnings;

    for (const category of REQUIRED_CATEGORIES) {
      if (!selectedParts[category]) {
        const label = BUILD_CATEGORIES.find((c) => c.key === category)?.label || category;
        warnings.push(`${label} 부품이 포함되어 있지 않습니다. 필수 부품입니다.`);
      }
    }

    for (const category of UNIQUE_CATEGORIES) {
      const part = selectedParts[category];
      if (part && part.quantity > 1) {
        const label = BUILD_CATEGORIES.find((c) => c.key === category)?.label || category;
        warnings.push(
          `${label} 부품이 ${part.quantity}개 포함되어 있습니다. 일반적으로 1개만 필요합니다.`,
        );
      }
    }

    return warnings;
  }, [selectedParts]);

  const selectedPartCount = Object.values(selectedParts).filter((p) => p !== null).length;

  // Mutations
  const createMutation = useMutation({
    mutationFn: (data: CreateBuildRequest) => createBuild(data),
    onSuccess: (result) => {
      toast.success('견적이 저장되었습니다!');
      navigate(`/builds/${result.id}`);
    },
    onError: () => {
      toast.error('견적 저장에 실패했습니다.');
    },
  });

  const updateMutation = useMutation({
    mutationFn: (data: UpdateBuildRequest) => updateBuild(buildId, data),
    onSuccess: (result) => {
      toast.success('견적이 수정되었습니다!');
      navigate(`/builds/${result.id}`);
    },
    onError: () => {
      toast.error('견적 수정에 실패했습니다.');
    },
  });

  const handlePartSelectFromModal = useCallback((part: Part) => {
    if (!modalCategory) return;
    setSelectedParts((prev) => ({
      ...prev,
      [modalCategory.key]: {
        partId: part.id,
        partName: part.name,
        category: part.category,
        manufacturer: part.manufacturer,
        lowestPrice: part.lowestPrice ?? null,
        quantity: 1,
        imageUrl: part.imageUrl,
        specs: part.specs,
      },
    }));
    setModalCategory(null);
  }, [modalCategory]);

  const handleQuantityChange = useCallback((category: string, quantity: number) => {
    if (quantity < 1) return;
    setSelectedParts((prev) => {
      const part = prev[category];
      if (!part) return prev;
      return { ...prev, [category]: { ...part, quantity } };
    });
  }, []);

  const handleRemovePart = useCallback((category: string) => {
    setSelectedParts((prev) => ({ ...prev, [category]: null }));
  }, []);

  const handleUpdatePartInfo = useCallback((category: string, updates: Partial<SelectedPartInfo>) => {
    setSelectedParts((prev) => {
      const part = prev[category];
      if (!part) return prev;
      return { ...prev, [category]: { ...part, ...updates } };
    });
  }, []);

  const handleClearAll = () => {
    const cleared: Record<string, SelectedPartInfo | null> = {};
    BUILD_CATEGORIES.forEach((cat) => {
      cleared[cat.key] = null;
    });
    setSelectedParts(cleared);
    toast.success('모든 부품이 초기화되었습니다.');
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (!isAuthenticated) {
      toast('로그인 후 견적을 저장할 수 있습니다.', { icon: '\uD83D\uDD12' });
      navigate('/login');
      return;
    }

    if (!name.trim()) {
      toast.error('견적 이름을 입력해주세요.');
      return;
    }

    const activeParts = Object.values(selectedParts).filter(
      (p): p is SelectedPartInfo => p !== null,
    );

    if (activeParts.length === 0) {
      toast.error('최소 1개 이상의 부품을 선택해주세요.');
      return;
    }

    const partsPayload = activeParts.map((p) => ({
      partId: p.partId,
      quantity: p.quantity,
    }));

    if (isEditMode) {
      updateMutation.mutate({
        name: name.trim(),
        description: description.trim() || undefined,
        isPublic,
        parts: partsPayload,
      });
    } else {
      createMutation.mutate({
        name: name.trim(),
        description: description.trim() || undefined,
        isPublic,
        parts: partsPayload,
      });
    }
  };

  const isSaving = createMutation.isPending || updateMutation.isPending;

  if (isEditMode && isBuildLoading) {
    return (
      <div className="flex items-center justify-center py-20">
        <div className="flex flex-col items-center gap-3">
          <div className="w-8 h-8 border-3 border-blue-500 border-t-transparent rounded-full animate-spin" />
          <span className="text-sm text-gray-500">견적 정보를 불러오는 중...</span>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-5xl mx-auto px-4 py-8">
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">
            {isEditMode ? '견적 수정' : 'PC 견적 만들기'}
          </h1>
          <p className="text-sm text-gray-500 mt-1">
            카테고리별로 부품을 선택하고 나만의 PC 견적을 만들어보세요
          </p>
        </div>
        {selectedPartCount > 0 && (
          <button
            type="button"
            onClick={handleClearAll}
            className="text-sm text-red-500 hover:text-red-600 font-medium"
          >
            전체 초기화
          </button>
        )}
      </div>

      <form onSubmit={handleSubmit}>
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          {/* Left: Part Selection (2/3 width) */}
          <div className="lg:col-span-2 space-y-3">
            {BUILD_CATEGORIES.map((cat) => {
              const part = selectedParts[cat.key];
              const isRequired = REQUIRED_CATEGORIES.includes(cat.key);
              const dotColor = CATEGORY_COLORS[cat.key] || 'bg-gray-400';

              return (
                <div
                  key={cat.key}
                  className={`bg-white border rounded-lg overflow-hidden transition-colors ${
                    part
                      ? 'border-blue-200'
                      : isRequired
                        ? 'border-dashed border-orange-300'
                        : 'border-dashed border-gray-300'
                  }`}
                >
                  {/* Category Header */}
                  <div className="flex items-center gap-2 px-4 py-2.5 bg-gray-50/80 border-b border-gray-100">
                    <span className={`w-2.5 h-2.5 rounded-full ${dotColor}`} />
                    <span className="text-sm font-semibold text-gray-800">{cat.label}</span>
                    {isRequired && !part && (
                      <span className="text-[10px] bg-orange-100 text-orange-600 px-1.5 py-0.5 rounded font-medium">
                        필수
                      </span>
                    )}
                    {part && (
                      <span className="text-[10px] bg-blue-100 text-blue-600 px-1.5 py-0.5 rounded font-medium">
                        선택됨
                      </span>
                    )}
                  </div>

                  {/* Content */}
                  <div className="p-3">
                    {part ? (
                      <SelectedPartCard
                        part={part}
                        onQuantityChange={(qty) => handleQuantityChange(cat.key, qty)}
                        onChange={() => setModalCategory(cat)}
                        onRemove={() => handleRemovePart(cat.key)}
                        onUpdatePartInfo={(updates) => handleUpdatePartInfo(cat.key, updates)}
                      />
                    ) : (
                      <button
                        type="button"
                        onClick={() => setModalCategory(cat)}
                        className="w-full flex items-center justify-center gap-2 py-4 text-sm text-gray-400 hover:text-blue-500 hover:bg-blue-50/50 rounded-md transition-colors"
                      >
                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                        </svg>
                        부품을 선택하세요
                      </button>
                    )}
                  </div>
                </div>
              );
            })}
          </div>

          {/* Right: Summary Sidebar (1/3 width, sticky) */}
          <div className="lg:col-span-1">
            <div className="sticky top-20 space-y-4">
              {/* Price Summary Card */}
              <div className="bg-white border border-gray-200 rounded-lg p-5">
                <h2 className="text-lg font-bold text-gray-900 mb-4">견적 요약</h2>

                <div className="space-y-2 mb-4">
                  {BUILD_CATEGORIES.map((cat) => {
                    const part = selectedParts[cat.key];
                    if (!part) return null;
                    return (
                      <div key={cat.key} className="flex items-center gap-2 text-sm">
                        <img
                          src={part.imageUrl || THUMB_FALLBACK}
                          alt=""
                          className="w-6 h-6 object-contain rounded bg-gray-50 shrink-0"
                          onError={(e) => {
                            (e.target as HTMLImageElement).src = THUMB_FALLBACK;
                          }}
                        />
                        <div className="flex-1 min-w-0 mr-1">
                          <span className="text-gray-500 text-[10px]">{cat.label}</span>
                          <p className="text-gray-800 truncate text-xs">{part.partName}</p>
                        </div>
                        <span className="text-gray-900 font-medium text-xs whitespace-nowrap">
                          {part.lowestPrice != null
                            ? `${(part.lowestPrice * part.quantity).toLocaleString()}원`
                            : '-'}
                          {part.quantity > 1 && (
                            <span className="text-gray-400 ml-0.5">x{part.quantity}</span>
                          )}
                        </span>
                      </div>
                    );
                  })}
                  {selectedPartCount === 0 && (
                    <p className="text-sm text-gray-400 text-center py-4">
                      부품을 선택해주세요
                    </p>
                  )}
                </div>

                {selectedPartCount > 0 && <div className="border-t border-gray-200 my-3" />}

                <div className="flex items-center justify-between">
                  <span className="text-base font-semibold text-gray-900">총 예상 금액</span>
                  <span className="text-xl font-bold text-blue-600 whitespace-nowrap">
                    {totalPrice.toLocaleString('ko-KR')}원
                  </span>
                </div>
                <p className="text-xs text-gray-400 mt-1">
                  {selectedPartCount}개 부품 선택됨
                </p>
              </div>

              {/* Compatibility Warnings */}
              {compatibilityWarnings.length > 0 && selectedPartCount > 0 && (
                <CompatibilityWarning warnings={compatibilityWarnings} />
              )}

              {/* Save Section */}
              <div className="bg-white border border-gray-200 rounded-lg p-5">
                {isAuthenticated ? (
                  <>
                    <div className="space-y-3 mb-4">
                      <div>
                        <label htmlFor="build-name" className="block text-xs font-medium text-gray-600 mb-1">
                          견적 이름 <span className="text-red-500">*</span>
                        </label>
                        <input
                          id="build-name"
                          type="text"
                          value={name}
                          onChange={(e) => setName(e.target.value)}
                          placeholder="예: 게이밍 PC 견적"
                          maxLength={100}
                          className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
                        />
                      </div>

                      <div>
                        <label htmlFor="build-desc" className="block text-xs font-medium text-gray-600 mb-1">
                          설명 (선택)
                        </label>
                        <textarea
                          id="build-desc"
                          value={description}
                          onChange={(e) => setDescription(e.target.value)}
                          placeholder="간단한 설명"
                          maxLength={500}
                          rows={2}
                          className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 resize-none"
                        />
                      </div>

                      <label className="flex items-center gap-2 cursor-pointer">
                        <input
                          type="checkbox"
                          checked={isPublic}
                          onChange={(e) => setIsPublic(e.target.checked)}
                          className="h-4 w-4 rounded border-gray-300 text-blue-600 focus:ring-blue-500"
                        />
                        <span className="text-xs text-gray-600">공개 견적</span>
                      </label>
                    </div>

                    <button
                      type="submit"
                      disabled={isSaving || selectedPartCount === 0}
                      className="w-full py-2.5 text-sm font-semibold text-white bg-blue-600 rounded-lg hover:bg-blue-700 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
                    >
                      {isSaving
                        ? '저장 중...'
                        : isEditMode
                          ? '수정 완료'
                          : '견적 저장하기'}
                    </button>
                  </>
                ) : (
                  <>
                    <p className="text-sm text-gray-600 mb-3 text-center">
                      견적을 저장하려면 로그인이 필요합니다
                    </p>
                    <button
                      type="button"
                      onClick={() => navigate('/login')}
                      className="w-full py-2.5 text-sm font-semibold text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition-colors"
                    >
                      로그인하고 저장하기
                    </button>
                    <button
                      type="button"
                      onClick={() => navigate('/signup')}
                      className="w-full mt-2 py-2.5 text-sm font-medium text-blue-600 bg-blue-50 rounded-lg hover:bg-blue-100 transition-colors"
                    >
                      회원가입
                    </button>
                  </>
                )}

                {isEditMode && (
                  <button
                    type="button"
                    onClick={() => navigate(`/builds/${buildId}`)}
                    className="w-full mt-2 py-2.5 text-sm font-medium text-gray-600 bg-gray-100 rounded-lg hover:bg-gray-200 transition-colors"
                  >
                    취소
                  </button>
                )}
              </div>
            </div>
          </div>
        </div>
      </form>

      {/* Part Selector Modal */}
      <PartSelectorModal
        isOpen={!!modalCategory}
        category={modalCategory?.key || ''}
        categoryLabel={modalCategory?.label || ''}
        onSelect={handlePartSelectFromModal}
        onClose={() => setModalCategory(null)}
      />
    </div>
  );
}
