import { useState, type FormEvent } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { useAuth } from '../../hooks/useAuth';
import { getParts } from '../../api/parts';
import {
  createPart,
  updatePart,
  deletePart,
  type CreatePartRequest,
  type UpdatePartRequest,
} from '../../api/admin';
import Sidebar from '../../components/layout/Sidebar';
import Pagination from '../../components/common/Pagination';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import Modal from '../../components/common/Modal';
import ConfirmDialog from '../../components/common/ConfirmDialog';

const CATEGORIES = [
  'CPU',
  'GPU',
  'MOTHERBOARD',
  'RAM',
  'SSD',
  'HDD',
  'PSU',
  'CASE',
  'COOLER',
];

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

interface PartFormData {
  name: string;
  category: string;
  manufacturer: string;
  specsJson: string;
}

const emptyForm: PartFormData = {
  name: '',
  category: 'CPU',
  manufacturer: '',
  specsJson: '{}',
};

export default function PartManagement() {
  const { user, isLoading: isAuthLoading } = useAuth();
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const [page, setPage] = useState(0);
  const pageSize = 15;

  // Modal state
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingPartId, setEditingPartId] = useState<number | null>(null);
  const [formData, setFormData] = useState<PartFormData>(emptyForm);

  // Delete confirm state
  const [deleteTarget, setDeleteTarget] = useState<{ id: number; name: string } | null>(null);

  const {
    data: partsPage,
    isLoading,
    isError,
  } = useQuery({
    queryKey: ['admin', 'parts', page],
    queryFn: () => getParts({ page, size: pageSize }),
    enabled: user?.role === 'ADMIN',
  });

  const createMutation = useMutation({
    mutationFn: (data: CreatePartRequest) => createPart(data),
    onSuccess: () => {
      toast.success('부품이 추가되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['admin', 'parts'] });
      closeModal();
    },
    onError: () => {
      toast.error('부품 추가에 실패했습니다.');
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: number; data: UpdatePartRequest }) => updatePart(id, data),
    onSuccess: () => {
      toast.success('부품이 수정되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['admin', 'parts'] });
      closeModal();
    },
    onError: () => {
      toast.error('부품 수정에 실패했습니다.');
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (id: number) => deletePart(id),
    onSuccess: () => {
      toast.success('부품이 삭제되었습니다.');
      queryClient.invalidateQueries({ queryKey: ['admin', 'parts'] });
      setDeleteTarget(null);
    },
    onError: () => {
      toast.error('부품 삭제에 실패했습니다.');
    },
  });

  if (isAuthLoading) {
    return <LoadingSpinner />;
  }

  if (!user || user.role !== 'ADMIN') {
    navigate('/login');
    return null;
  }

  function openCreateModal() {
    setEditingPartId(null);
    setFormData(emptyForm);
    setIsModalOpen(true);
  }

  function openEditModal(part: {
    id: number;
    name: string;
    category: string;
    manufacturer: string;
    specs: Record<string, unknown>;
  }) {
    setEditingPartId(part.id);
    setFormData({
      name: part.name,
      category: part.category,
      manufacturer: part.manufacturer,
      specsJson: JSON.stringify(part.specs, null, 2),
    });
    setIsModalOpen(true);
  }

  function closeModal() {
    setIsModalOpen(false);
    setEditingPartId(null);
    setFormData(emptyForm);
  }

  function handleSubmit(e: FormEvent) {
    e.preventDefault();

    if (!formData.name.trim() || !formData.manufacturer.trim()) {
      toast.error('이름과 제조사는 필수입니다.');
      return;
    }

    let specs: Record<string, unknown>;
    try {
      specs = JSON.parse(formData.specsJson);
    } catch {
      toast.error('스펙 JSON 형식이 올바르지 않습니다.');
      return;
    }

    const payload = {
      name: formData.name.trim(),
      category: formData.category,
      manufacturer: formData.manufacturer.trim(),
      specs,
    };

    if (editingPartId !== null) {
      updateMutation.mutate({ id: editingPartId, data: payload });
    } else {
      createMutation.mutate(payload);
    }
  }

  const isMutating = createMutation.isPending || updateMutation.isPending;

  return (
    <div className="flex min-h-[calc(100vh-4rem)]">
      <Sidebar />
      <div className="flex-1 p-6 bg-gray-50">
        <div className="mx-auto max-w-6xl">
          <div className="flex items-center justify-between mb-6">
            <h1 className="text-2xl font-bold text-gray-900">부품관리</h1>
            <button
              onClick={openCreateModal}
              className="inline-flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2.5 text-sm font-semibold text-white hover:bg-blue-700 transition-colors"
            >
              <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
              </svg>
              부품 추가
            </button>
          </div>

          {isLoading ? (
            <LoadingSpinner />
          ) : isError ? (
            <div className="rounded-lg bg-red-50 p-6 text-center text-red-600">
              부품 목록을 불러올 수 없습니다.
            </div>
          ) : (
            <>
              <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
                <div className="px-6 py-4 border-b border-gray-200">
                  <h2 className="text-lg font-semibold text-gray-900">
                    전체 부품{' '}
                    <span className="text-base font-normal text-gray-500">
                      ({partsPage?.totalElements ?? 0}개)
                    </span>
                  </h2>
                </div>

                <div className="overflow-x-auto">
                  <table className="w-full">
                    <thead>
                      <tr className="border-b border-gray-200 bg-gray-50">
                        <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                          ID
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                          이름
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                          카테고리
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                          제조사
                        </th>
                        <th className="px-6 py-3 text-right text-xs font-semibold text-gray-600 uppercase tracking-wider">
                          작업
                        </th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-200">
                      {partsPage?.content && partsPage.content.length > 0 ? (
                        partsPage.content.map((part) => (
                          <tr key={part.id} className="hover:bg-gray-50 transition-colors">
                            <td className="px-6 py-4 text-sm text-gray-500">{part.id}</td>
                            <td className="px-6 py-4 text-sm text-gray-900 font-medium max-w-xs truncate">
                              {part.name}
                            </td>
                            <td className="px-6 py-4 text-sm text-gray-600">
                              {categoryLabels[part.category] ?? part.category}
                            </td>
                            <td className="px-6 py-4 text-sm text-gray-600">
                              {part.manufacturer}
                            </td>
                            <td className="px-6 py-4 text-right">
                              <div className="flex items-center justify-end gap-2">
                                <button
                                  onClick={() => openEditModal(part)}
                                  className="inline-flex items-center rounded-md border border-gray-300 bg-white px-3 py-1.5 text-xs font-medium text-gray-700 hover:bg-gray-50 transition-colors"
                                >
                                  수정
                                </button>
                                <button
                                  onClick={() => setDeleteTarget({ id: part.id, name: part.name })}
                                  className="inline-flex items-center rounded-md border border-red-300 bg-white px-3 py-1.5 text-xs font-medium text-red-600 hover:bg-red-50 transition-colors"
                                >
                                  삭제
                                </button>
                              </div>
                            </td>
                          </tr>
                        ))
                      ) : (
                        <tr>
                          <td colSpan={5} className="px-6 py-8 text-center text-sm text-gray-500">
                            등록된 부품이 없습니다.
                          </td>
                        </tr>
                      )}
                    </tbody>
                  </table>
                </div>
              </div>

              {/* Pagination */}
              {partsPage && partsPage.totalPages > 1 && (
                <div className="mt-6">
                  <Pagination
                    currentPage={page}
                    totalPages={partsPage.totalPages}
                    onPageChange={setPage}
                  />
                </div>
              )}
            </>
          )}
        </div>
      </div>

      {/* Create / Edit Modal */}
      <Modal
        isOpen={isModalOpen}
        onClose={closeModal}
        title={editingPartId !== null ? '부품 수정' : '부품 추가'}
      >
        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label htmlFor="part-name" className="block text-sm font-medium text-gray-700 mb-1">
              이름 <span className="text-red-500">*</span>
            </label>
            <input
              id="part-name"
              type="text"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              placeholder="부품 이름"
              className="w-full rounded-lg border border-gray-300 px-4 py-2.5 text-sm focus:border-blue-500 focus:ring-1 focus:ring-blue-500 outline-none transition-colors"
              required
            />
          </div>

          <div>
            <label htmlFor="part-category" className="block text-sm font-medium text-gray-700 mb-1">
              카테고리 <span className="text-red-500">*</span>
            </label>
            <select
              id="part-category"
              value={formData.category}
              onChange={(e) => setFormData({ ...formData, category: e.target.value })}
              className="w-full rounded-lg border border-gray-300 px-4 py-2.5 text-sm focus:border-blue-500 focus:ring-1 focus:ring-blue-500 outline-none transition-colors bg-white"
            >
              {CATEGORIES.map((cat) => (
                <option key={cat} value={cat}>
                  {categoryLabels[cat] ?? cat}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label htmlFor="part-manufacturer" className="block text-sm font-medium text-gray-700 mb-1">
              제조사 <span className="text-red-500">*</span>
            </label>
            <input
              id="part-manufacturer"
              type="text"
              value={formData.manufacturer}
              onChange={(e) => setFormData({ ...formData, manufacturer: e.target.value })}
              placeholder="제조사"
              className="w-full rounded-lg border border-gray-300 px-4 py-2.5 text-sm focus:border-blue-500 focus:ring-1 focus:ring-blue-500 outline-none transition-colors"
              required
            />
          </div>

          <div>
            <label htmlFor="part-specs" className="block text-sm font-medium text-gray-700 mb-1">
              스펙 (JSON)
            </label>
            <textarea
              id="part-specs"
              value={formData.specsJson}
              onChange={(e) => setFormData({ ...formData, specsJson: e.target.value })}
              rows={5}
              placeholder='{"cores": 8, "threads": 16}'
              className="w-full rounded-lg border border-gray-300 px-4 py-2.5 text-sm font-mono focus:border-blue-500 focus:ring-1 focus:ring-blue-500 outline-none transition-colors resize-none"
            />
          </div>

          <div className="flex justify-end gap-3 pt-2">
            <button
              type="button"
              onClick={closeModal}
              className="rounded-lg border border-gray-300 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-50 transition-colors"
            >
              취소
            </button>
            <button
              type="submit"
              disabled={isMutating}
              className="rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              {isMutating
                ? '처리 중...'
                : editingPartId !== null
                  ? '수정'
                  : '추가'}
            </button>
          </div>
        </form>
      </Modal>

      {/* Delete Confirmation */}
      <ConfirmDialog
        isOpen={deleteTarget !== null}
        onConfirm={() => {
          if (deleteTarget) {
            deleteMutation.mutate(deleteTarget.id);
          }
        }}
        onCancel={() => setDeleteTarget(null)}
        title="부품 삭제"
        message={`"${deleteTarget?.name ?? ''}" 부품을 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.`}
      />
    </div>
  );
}
