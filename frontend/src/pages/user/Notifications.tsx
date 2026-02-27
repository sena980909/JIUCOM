import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import {
  getNotifications,
  markAsRead,
  markAllAsRead,
  type Notification,
} from '../../api/notifications';
import { useAuth } from '../../hooks/useAuth';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import Pagination from '../../components/common/Pagination';
import { useState } from 'react';

const TYPE_CONFIG: Record<string, { label: string; icon: string; color: string }> = {
  COMMENT_REPLY: {
    label: '댓글',
    icon: 'M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z',
    color: 'bg-blue-100 text-blue-600',
  },
  PRICE_ALERT: {
    label: '가격알림',
    icon: 'M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z',
    color: 'bg-green-100 text-green-600',
  },
  LIKE: {
    label: '좋아요',
    icon: 'M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z',
    color: 'bg-red-100 text-red-600',
  },
  SYSTEM: {
    label: '시스템',
    icon: 'M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z',
    color: 'bg-gray-100 text-gray-600',
  },
};

function formatDate(dateString: string): string {
  const date = new Date(dateString);
  const now = new Date();
  const diff = now.getTime() - date.getTime();
  const diffMinutes = Math.floor(diff / (1000 * 60));
  const diffHours = Math.floor(diff / (1000 * 60 * 60));
  const diffDays = Math.floor(diff / (1000 * 60 * 60 * 24));

  if (diffMinutes < 1) return '방금 전';
  if (diffMinutes < 60) return `${diffMinutes}분 전`;
  if (diffHours < 24) return `${diffHours}시간 전`;
  if (diffDays < 7) return `${diffDays}일 전`;

  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  });
}

export default function Notifications() {
  const { isAuthenticated } = useAuth();
  const queryClient = useQueryClient();
  const [page, setPage] = useState(0);

  const { data, isLoading, isError } = useQuery({
    queryKey: ['notifications', page],
    queryFn: () => getNotifications({ page, size: 20 }),
    enabled: isAuthenticated,
  });

  const notifications: Notification[] = data?.content ?? [];
  const totalPages = data?.totalPages ?? 0;
  const currentPage = data?.number ?? 0;

  const markReadMutation = useMutation({
    mutationFn: markAsRead,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['notifications'] });
    },
  });

  const markAllReadMutation = useMutation({
    mutationFn: markAllAsRead,
    onSuccess: () => {
      toast.success('모든 알림을 읽음 처리했습니다');
      queryClient.invalidateQueries({ queryKey: ['notifications'] });
    },
    onError: () => {
      toast.error('읽음 처리에 실패했습니다');
    },
  });

  const handleNotificationClick = (notification: Notification) => {
    if (!notification.read) {
      markReadMutation.mutate(notification.id);
    }
  };

  const hasUnread = notifications.some((n) => !n.read);

  if (!isAuthenticated) {
    return (
      <div className="py-20 text-center">
        <p className="text-gray-500">로그인이 필요합니다</p>
      </div>
    );
  }

  return (
    <div className="mx-auto max-w-2xl px-4 py-8">
      <div className="mb-6 flex items-center justify-between">
        <h1 className="text-2xl font-bold text-gray-900">알림</h1>
        {hasUnread && (
          <button
            onClick={() => markAllReadMutation.mutate()}
            disabled={markAllReadMutation.isPending}
            className="rounded-lg border border-gray-300 bg-white px-3 py-1.5 text-sm font-medium text-gray-700 hover:bg-gray-50 disabled:cursor-not-allowed disabled:text-gray-400 transition-colors"
          >
            {markAllReadMutation.isPending ? '처리 중...' : '모두 읽음'}
          </button>
        )}
      </div>

      {isLoading ? (
        <LoadingSpinner />
      ) : isError ? (
        <div className="py-20 text-center">
          <p className="text-red-500">알림 목록을 불러오는 중 오류가 발생했습니다.</p>
        </div>
      ) : notifications.length === 0 ? (
        <div className="py-20 text-center">
          <svg className="mx-auto h-12 w-12 text-gray-300" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
          </svg>
          <p className="mt-4 text-gray-500">알림이 없습니다</p>
        </div>
      ) : (
        <>
          <div className="overflow-hidden rounded-lg border border-gray-200 bg-white shadow-sm">
            {notifications.map((notification, index) => {
              const typeConfig = TYPE_CONFIG[notification.type] ?? TYPE_CONFIG.SYSTEM;

              return (
                <button
                  key={notification.id}
                  onClick={() => handleNotificationClick(notification)}
                  className={`flex w-full items-start gap-3 px-4 py-4 text-left transition-colors hover:bg-gray-50 ${
                    !notification.read ? 'bg-blue-50' : 'bg-white'
                  } ${index > 0 ? 'border-t border-gray-100' : ''}`}
                >
                  {/* Type icon */}
                  <div className={`mt-0.5 shrink-0 rounded-full p-2 ${typeConfig.color}`}>
                    <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d={typeConfig.icon} />
                    </svg>
                  </div>

                  {/* Content */}
                  <div className="min-w-0 flex-1">
                    <div className="flex items-center gap-2 mb-0.5">
                      <span className="text-xs font-medium text-gray-500">
                        {typeConfig.label}
                      </span>
                      {!notification.read && (
                        <span className="h-2 w-2 rounded-full bg-blue-500" />
                      )}
                    </div>
                    <p className={`text-sm leading-relaxed ${!notification.read ? 'font-medium text-gray-900' : 'text-gray-700'}`}>
                      {notification.message}
                    </p>
                    <p className="mt-1 text-xs text-gray-400">
                      {formatDate(notification.createdAt)}
                    </p>
                  </div>
                </button>
              );
            })}
          </div>

          <div className="mt-8">
            <Pagination
              currentPage={currentPage}
              totalPages={totalPages}
              onPageChange={setPage}
            />
          </div>
        </>
      )}
    </div>
  );
}
