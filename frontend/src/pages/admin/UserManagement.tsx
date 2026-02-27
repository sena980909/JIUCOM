import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { getUsers } from '../../api/admin';
import Sidebar from '../../components/layout/Sidebar';
import Pagination from '../../components/common/Pagination';
import LoadingSpinner from '../../components/common/LoadingSpinner';

function formatDate(dateStr: string): string {
  const date = new Date(dateStr);
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  });
}

const ROLE_BADGE: Record<string, string> = {
  ADMIN: 'bg-red-100 text-red-700',
  USER: 'bg-blue-100 text-blue-700',
};

export default function UserManagement() {
  const { user, isLoading: isAuthLoading } = useAuth();
  const navigate = useNavigate();
  const [page, setPage] = useState(0);
  const pageSize = 15;

  const {
    data: usersPage,
    isLoading,
    isError,
  } = useQuery({
    queryKey: ['admin', 'users', page],
    queryFn: () => getUsers({ page, size: pageSize }),
    enabled: user?.role === 'ADMIN',
  });

  if (isAuthLoading) {
    return <LoadingSpinner />;
  }

  if (!user || user.role !== 'ADMIN') {
    navigate('/login');
    return null;
  }

  return (
    <div className="flex min-h-[calc(100vh-4rem)]">
      <Sidebar />
      <div className="flex-1 p-6 bg-gray-50">
        <div className="mx-auto max-w-6xl">
          <h1 className="text-2xl font-bold text-gray-900 mb-6">회원관리</h1>

          {isLoading ? (
            <LoadingSpinner />
          ) : isError ? (
            <div className="rounded-lg bg-red-50 p-6 text-center text-red-600">
              회원 목록을 불러올 수 없습니다.
            </div>
          ) : (
            <>
              <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
                <div className="px-6 py-4 border-b border-gray-200 flex items-center justify-between">
                  <h2 className="text-lg font-semibold text-gray-900">
                    전체 회원{' '}
                    <span className="text-base font-normal text-gray-500">
                      ({usersPage?.totalElements ?? 0}명)
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
                          이메일
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                          닉네임
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                          역할
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                          가입일
                        </th>
                        <th className="px-6 py-3 text-left text-xs font-semibold text-gray-600 uppercase tracking-wider">
                          상태
                        </th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-200">
                      {usersPage?.content && usersPage.content.length > 0 ? (
                        usersPage.content.map((u, index) => (
                          <tr
                            key={u.id}
                            className={`${
                              index % 2 === 1 ? 'bg-gray-50/50' : 'bg-white'
                            } hover:bg-gray-100 transition-colors`}
                          >
                            <td className="px-6 py-4 text-sm text-gray-500">{u.id}</td>
                            <td className="px-6 py-4 text-sm text-gray-900">{u.email}</td>
                            <td className="px-6 py-4 text-sm text-gray-900 font-medium">
                              {u.nickname}
                            </td>
                            <td className="px-6 py-4">
                              <span
                                className={`inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-medium ${
                                  ROLE_BADGE[u.role] ?? 'bg-gray-100 text-gray-700'
                                }`}
                              >
                                {u.role}
                              </span>
                            </td>
                            <td className="px-6 py-4 text-sm text-gray-500">
                              {formatDate(u.createdAt)}
                            </td>
                            <td className="px-6 py-4">
                              <span className="inline-flex items-center rounded-full bg-green-100 px-2.5 py-0.5 text-xs font-medium text-green-700">
                                활성
                              </span>
                            </td>
                          </tr>
                        ))
                      ) : (
                        <tr>
                          <td colSpan={6} className="px-6 py-8 text-center text-sm text-gray-500">
                            등록된 회원이 없습니다.
                          </td>
                        </tr>
                      )}
                    </tbody>
                  </table>
                </div>
              </div>

              {/* Pagination */}
              {usersPage && usersPage.totalPages > 1 && (
                <div className="mt-6">
                  <Pagination
                    currentPage={page}
                    totalPages={usersPage.totalPages}
                    onPageChange={setPage}
                  />
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
}
