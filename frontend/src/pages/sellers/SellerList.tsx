import { useQuery } from '@tanstack/react-query';
import { getSellers, type Seller } from '../../api/sellers';
import LoadingSpinner from '../../components/common/LoadingSpinner';

const STATUS_CONFIG: Record<string, { label: string; dotColor: string; bgColor: string }> = {
  ACTIVE: { label: '운영중', dotColor: 'bg-green-500', bgColor: 'bg-green-50 text-green-700' },
  INACTIVE: { label: '비활성', dotColor: 'bg-gray-400', bgColor: 'bg-gray-100 text-gray-600' },
  SUSPENDED: { label: '정지', dotColor: 'bg-red-500', bgColor: 'bg-red-50 text-red-700' },
};

export default function SellerList() {
  const { data, isLoading, isError } = useQuery({
    queryKey: ['sellers'],
    queryFn: getSellers,
  });

  const sellers: Seller[] = Array.isArray(data)
    ? data
    : (data as unknown as { data?: Seller[] })?.data ?? [];

  return (
    <div className="mx-auto max-w-4xl px-4 py-8">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-gray-900">판매처 목록</h1>
        <p className="mt-1 text-sm text-gray-500">
          등록된 컴퓨터 부품 판매처 정보입니다
        </p>
      </div>

      {isLoading ? (
        <LoadingSpinner />
      ) : isError ? (
        <div className="py-20 text-center">
          <p className="text-red-500">판매처 목록을 불러오는 중 오류가 발생했습니다.</p>
        </div>
      ) : sellers.length === 0 ? (
        <div className="py-20 text-center">
          <svg className="mx-auto h-12 w-12 text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 100 4 2 2 0 000-4z" />
          </svg>
          <p className="mt-4 text-gray-500">등록된 판매처가 없습니다</p>
        </div>
      ) : (
        <>
          {/* Desktop table */}
          <div className="hidden sm:block overflow-hidden rounded-lg border border-gray-200 bg-white shadow-sm">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-semibold uppercase tracking-wider text-gray-500">
                    판매처명
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold uppercase tracking-wider text-gray-500">
                    URL
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-semibold uppercase tracking-wider text-gray-500">
                    상태
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200 bg-white">
                {sellers.map((seller) => {
                  const status = STATUS_CONFIG[seller.status] ?? {
                    label: seller.status,
                    dotColor: 'bg-gray-400',
                    bgColor: 'bg-gray-100 text-gray-600',
                  };

                  return (
                    <tr key={seller.id} className="hover:bg-gray-50 transition-colors">
                      <td className="whitespace-nowrap px-6 py-4 text-sm font-medium text-gray-900">
                        {seller.name}
                      </td>
                      <td className="px-6 py-4 text-sm text-gray-500">
                        {seller.url ? (
                          <a
                            href={seller.url}
                            target="_blank"
                            rel="noopener noreferrer"
                            className="text-blue-600 hover:text-blue-800 hover:underline transition-colors"
                          >
                            {seller.url}
                          </a>
                        ) : (
                          <span className="text-gray-400">-</span>
                        )}
                      </td>
                      <td className="whitespace-nowrap px-6 py-4">
                        <span className={`inline-flex items-center gap-1.5 rounded-full px-2.5 py-0.5 text-xs font-medium ${status.bgColor}`}>
                          <span className={`h-1.5 w-1.5 rounded-full ${status.dotColor}`} />
                          {status.label}
                        </span>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>

          {/* Mobile card grid */}
          <div className="sm:hidden space-y-3">
            {sellers.map((seller) => {
              const status = STATUS_CONFIG[seller.status] ?? {
                label: seller.status,
                dotColor: 'bg-gray-400',
                bgColor: 'bg-gray-100 text-gray-600',
              };

              return (
                <div
                  key={seller.id}
                  className="rounded-lg border border-gray-200 bg-white p-4 shadow-sm"
                >
                  <div className="flex items-center justify-between mb-2">
                    <h3 className="text-sm font-semibold text-gray-900">{seller.name}</h3>
                    <span className={`inline-flex items-center gap-1.5 rounded-full px-2.5 py-0.5 text-xs font-medium ${status.bgColor}`}>
                      <span className={`h-1.5 w-1.5 rounded-full ${status.dotColor}`} />
                      {status.label}
                    </span>
                  </div>
                  {seller.url ? (
                    <a
                      href={seller.url}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="text-sm text-blue-600 hover:text-blue-800 hover:underline break-all transition-colors"
                    >
                      {seller.url}
                    </a>
                  ) : (
                    <span className="text-sm text-gray-400">URL 없음</span>
                  )}
                </div>
              );
            })}
          </div>

          <div className="mt-4 text-right text-sm text-gray-400">
            총 {sellers.length}개 판매처
          </div>
        </>
      )}
    </div>
  );
}
