import { useState, useEffect, useMemo } from 'react';
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
import { useAuth } from '../../hooks/useAuth';
import BuildPartSelector from '../../components/builds/BuildPartSelector';
import CompatibilityWarning from '../../components/builds/CompatibilityWarning';

interface SelectedPartInfo {
  partId: number;
  partName: string;
  category: string;
  manufacturer: string;
  lowestPrice: number | null;
  quantity: number;
}

const BUILD_CATEGORIES = [
  { key: 'CPU', label: 'CPU' },
  { key: 'GPU', label: '그래픽카드 (GPU)' },
  { key: 'MOTHERBOARD', label: '메인보드' },
  { key: 'RAM', label: '메모리 (RAM)' },
  { key: 'SSD', label: 'SSD' },
  { key: 'HDD', label: 'HDD' },
  { key: 'POWER_SUPPLY', label: '파워서플라이 (PSU)' },
  { key: 'CASE', label: '케이스' },
  { key: 'COOLER', label: '쿨러' },
] as const;

const REQUIRED_CATEGORIES = ['CPU', 'MOTHERBOARD', 'POWER_SUPPLY'];
const UNIQUE_CATEGORIES = ['CPU', 'MOTHERBOARD', 'CASE', 'POWER_SUPPLY'];

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

  // Calculate compatibility warnings (client-side)
  const compatibilityWarnings = useMemo(() => {
    const warnings: string[] = [];
    const activeParts = Object.values(selectedParts).filter(
      (p): p is SelectedPartInfo => p !== null,
    );

    if (activeParts.length === 0) return warnings;

    // Check required categories
    for (const category of REQUIRED_CATEGORIES) {
      if (!selectedParts[category]) {
        const label = BUILD_CATEGORIES.find((c) => c.key === category)?.label || category;
        warnings.push(`${label} 부품이 포함되어 있지 않습니다. 필수 부품입니다.`);
      }
    }

    // Check unique categories for duplicates (shouldn't happen with single-select per category,
    // but kept for future multi-select support)
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

  // Create mutation
  const createMutation = useMutation({
    mutationFn: (data: CreateBuildRequest) => createBuild(data),
    onSuccess: (result) => {
      toast.success('견적이 생성되었습니다!');
      navigate(`/builds/${result.id}`);
    },
    onError: () => {
      toast.error('견적 생성에 실패했습니다.');
    },
  });

  // Update mutation
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

  const handlePartSelect = (category: string, part: SelectedPartInfo | null) => {
    setSelectedParts((prev) => ({
      ...prev,
      [category]: part,
    }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

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

  // Redirect if not authenticated
  if (!isAuthenticated) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-20 text-center">
        <p className="text-gray-500 mb-4">로그인이 필요합니다.</p>
        <button
          onClick={() => navigate('/login')}
          className="text-sm text-blue-600 hover:text-blue-700 underline"
        >
          로그인하기
        </button>
      </div>
    );
  }

  // Loading existing build
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
    <div className="max-w-4xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-8">
        {isEditMode ? '견적 수정' : '새 견적 만들기'}
      </h1>

      <form onSubmit={handleSubmit}>
        {/* Basic Info */}
        <div className="bg-white border border-gray-200 rounded-lg p-6 mb-6">
          <h2 className="text-lg font-semibold text-gray-900 mb-4">기본 정보</h2>

          <div className="space-y-4">
            <div>
              <label htmlFor="build-name" className="block text-sm font-medium text-gray-700 mb-1">
                견적 이름 <span className="text-red-500">*</span>
              </label>
              <input
                id="build-name"
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="예: 게이밍 PC 견적"
                maxLength={100}
                className="w-full px-3 py-2.5 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
              />
              <p className="mt-1 text-xs text-gray-400">{name.length}/100</p>
            </div>

            <div>
              <label htmlFor="build-desc" className="block text-sm font-medium text-gray-700 mb-1">
                설명
              </label>
              <textarea
                id="build-desc"
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                placeholder="견적에 대한 간단한 설명을 입력해주세요"
                maxLength={500}
                rows={3}
                className="w-full px-3 py-2.5 border border-gray-300 rounded-md text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors resize-none"
              />
              <p className="mt-1 text-xs text-gray-400">{description.length}/500</p>
            </div>

            <div className="flex items-center gap-2">
              <input
                id="build-public"
                type="checkbox"
                checked={isPublic}
                onChange={(e) => setIsPublic(e.target.checked)}
                className="h-4 w-4 rounded border-gray-300 text-blue-600 focus:ring-blue-500"
              />
              <label htmlFor="build-public" className="text-sm text-gray-700">
                공개 견적으로 설정 (다른 사용자가 볼 수 있습니다)
              </label>
            </div>
          </div>
        </div>

        {/* Part Selection */}
        <div className="bg-white border border-gray-200 rounded-lg p-6 mb-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-lg font-semibold text-gray-900">부품 선택</h2>
            <span className="text-sm text-gray-500">
              선택된 부품: {selectedPartCount}개
            </span>
          </div>

          <div className="space-y-3">
            {BUILD_CATEGORIES.map((cat) => (
              <BuildPartSelector
                key={cat.key}
                category={cat.key}
                categoryLabel={cat.label}
                selectedPart={selectedParts[cat.key]}
                onSelect={(part) => handlePartSelect(cat.key, part)}
              />
            ))}
          </div>
        </div>

        {/* Compatibility Warnings */}
        {compatibilityWarnings.length > 0 && (
          <div className="mb-6">
            <CompatibilityWarning warnings={compatibilityWarnings} />
          </div>
        )}

        {/* Total Price & Submit */}
        <div className="bg-white border border-gray-200 rounded-lg p-6">
          <div className="flex items-center justify-between mb-6">
            <span className="text-lg font-semibold text-gray-900">총 예상 금액</span>
            <span className="text-2xl font-bold text-blue-600">
              {totalPrice.toLocaleString('ko-KR')}원
            </span>
          </div>

          <div className="flex items-center justify-end gap-3">
            <button
              type="button"
              onClick={() => navigate(isEditMode ? `/builds/${buildId}` : '/builds')}
              className="px-5 py-2.5 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
            >
              취소
            </button>
            <button
              type="submit"
              disabled={isSaving}
              className="px-5 py-2.5 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              {isSaving
                ? '저장 중...'
                : isEditMode
                  ? '수정 완료'
                  : '견적 저장'}
            </button>
          </div>
        </div>
      </form>
    </div>
  );
}
