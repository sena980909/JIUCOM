import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { getDashboard } from '../../api/admin';
import Sidebar from '../../components/layout/Sidebar';
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

export default function Dashboard() {
  const { user, isLoading: isAuthLoading } = useAuth();
  const navigate = useNavigate();

  const {
    data: dashboard,
    isLoading,
    isError,
  } = useQuery({
    queryKey: ['admin', 'dashboard'],
    queryFn: getDashboard,
    enabled: user?.role === 'ADMIN',
  });

  if (isAuthLoading) {
    return <LoadingSpinner />;
  }

  if (!user || user.role !== 'ADMIN') {
    navigate('/login');
    return null;
  }

  const stats = [
    {
      label: '총 회원수',
      value: dashboard?.totalUsers ?? 0,
      bgColor: 'bg-blue-50',
      textColor: 'text-blue-700',
      iconBg: 'bg-blue-100',
      iconColor: 'text-blue-600',
      icon: (
        <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
        </svg>
      ),
    },
    {
      label: '오늘 가입',
      value: dashboard?.todaySignups ?? 0,
      bgColor: 'bg-green-50',
      textColor: 'text-green-700',
      iconBg: 'bg-green-100',
      iconColor: 'text-green-600',
      icon: (
        <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z" />
        </svg>
      ),
    },
    {
      label: '총 견적수',
      value: dashboard?.totalBuilds ?? 0,
      bgColor: 'bg-purple-50',
      textColor: 'text-purple-700',
      iconBg: 'bg-purple-100',
      iconColor: 'text-purple-600',
      icon: (
        <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 7h6m0 10v-3m-3 3h.01M9 17h.01M9 14h.01M12 14h.01M15 11h.01M12 11h.01M9 11h.01M7 21h10a2 2 0 002-2V5a2 2 0 00-2-2H7a2 2 0 00-2 2v14a2 2 0 002 2z" />
        </svg>
      ),
    },
    {
      label: '부품 수',
      value: dashboard?.totalParts ?? 0,
      bgColor: 'bg-orange-50',
      textColor: 'text-orange-700',
      iconBg: 'bg-orange-100',
      iconColor: 'text-orange-600',
      icon: (
        <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2zM9 9h6v6H9V9z" />
        </svg>
      ),
    },
    {
      label: '게시글 수',
      value: dashboard?.totalPosts ?? 0,
      bgColor: 'bg-pink-50',
      textColor: 'text-pink-700',
      iconBg: 'bg-pink-100',
      iconColor: 'text-pink-600',
      icon: (
        <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 20H5a2 2 0 01-2-2V6a2 2 0 012-2h10a2 2 0 012 2v1m2 13a2 2 0 01-2-2V7m2 13a2 2 0 002-2V9a2 2 0 00-2-2h-2m-4-3H9M7 16h6M7 8h6v4H7V8z" />
        </svg>
      ),
    },
  ];

  return (
    <div className="flex min-h-[calc(100vh-4rem)]">
      <Sidebar />
      <div className="flex-1 p-6 bg-gray-50">
        <div className="mx-auto max-w-6xl">
          <h1 className="text-2xl font-bold text-gray-900 mb-6">대시보드</h1>

          {isLoading ? (
            <LoadingSpinner />
          ) : isError ? (
            <div className="rounded-lg bg-red-50 p-6 text-center text-red-600">
              대시보드 데이터를 불러올 수 없습니다.
            </div>
          ) : (
            <>
              {/* Stats Cards */}
              <div className="grid grid-cols-2 md:grid-cols-3 gap-4 mb-8">
                {stats.map((stat) => (
                  <div
                    key={stat.label}
                    className={`${stat.bgColor} rounded-xl p-5 shadow-sm`}
                  >
                    <div className="flex items-center gap-3">
                      <div className={`${stat.iconBg} ${stat.iconColor} rounded-lg p-2.5`}>
                        {stat.icon}
                      </div>
                      <div>
                        <p className="text-sm font-medium text-gray-600">{stat.label}</p>
                        <p className={`text-2xl font-bold ${stat.textColor}`}>
                          {stat.value.toLocaleString('ko-KR')}
                        </p>
                      </div>
                    </div>
                  </div>
                ))}
              </div>

              {/* Recent Signups */}
              <div className="bg-white rounded-xl shadow-sm border border-gray-200">
                <div className="px-6 py-4 border-b border-gray-200">
                  <h2 className="text-lg font-semibold text-gray-900">최근 가입자</h2>
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
                          가입일
                        </th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-200">
                      {dashboard?.recentUsers && dashboard.recentUsers.length > 0 ? (
                        dashboard.recentUsers.slice(0, 5).map((user) => (
                          <tr key={user.id} className="hover:bg-gray-50 transition-colors">
                            <td className="px-6 py-4 text-sm text-gray-500">{user.id}</td>
                            <td className="px-6 py-4 text-sm text-gray-900">{user.email}</td>
                            <td className="px-6 py-4 text-sm text-gray-900 font-medium">
                              {user.nickname}
                            </td>
                            <td className="px-6 py-4 text-sm text-gray-500">
                              {formatDate(user.createdAt)}
                            </td>
                          </tr>
                        ))
                      ) : (
                        <tr>
                          <td colSpan={4} className="px-6 py-8 text-center text-sm text-gray-500">
                            최근 가입자가 없습니다.
                          </td>
                        </tr>
                      )}
                    </tbody>
                  </table>
                </div>
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
