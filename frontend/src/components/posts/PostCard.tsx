import { Link } from 'react-router-dom';
import type { Post } from '../../api/posts';

const BOARD_TYPE_CONFIG: Record<string, { label: string; color: string }> = {
  FREE: { label: '자유', color: 'bg-blue-100 text-blue-700' },
  QNA: { label: 'Q&A', color: 'bg-green-100 text-green-700' },
  REVIEW: { label: '리뷰', color: 'bg-purple-100 text-purple-700' },
  NOTICE: { label: '공지', color: 'bg-red-100 text-red-700' },
};

interface PostCardProps {
  post: Post;
}

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

function truncateContent(content: string, maxLength: number = 150): string {
  if (content.length <= maxLength) return content;
  return content.slice(0, maxLength) + '...';
}

export default function PostCard({ post }: PostCardProps) {
  const boardConfig = BOARD_TYPE_CONFIG[post.boardType] ?? {
    label: post.boardType,
    color: 'bg-gray-100 text-gray-700',
  };

  return (
    <div className="border-b border-gray-200 py-4 hover:bg-gray-50 transition-colors px-2">
      <Link to={`/posts/${post.id}`} className="block">
        <h3 className="text-lg font-semibold text-gray-900 hover:text-blue-600 transition-colors mb-1">
          {post.title}
        </h3>
        <p className="text-sm text-gray-500 leading-relaxed mb-3">
          {truncateContent(post.content)}
        </p>
      </Link>

      <div className="flex items-center justify-between text-sm text-gray-400">
        <div className="flex items-center gap-4">
          <span
            className={`inline-block px-2 py-0.5 rounded-full text-xs font-medium ${boardConfig.color}`}
          >
            {boardConfig.label}
          </span>

          {/* View count */}
          <span className="flex items-center gap-1">
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
              />
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z"
              />
            </svg>
            {post.viewCount}
          </span>

          {/* Like count */}
          <span className="flex items-center gap-1">
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"
              />
            </svg>
            {post.likeCount}
          </span>

          {/* Comment count */}
          <span className="flex items-center gap-1">
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M8 12h.01M12 12h.01M16 12h.01M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"
              />
            </svg>
            {post.commentCount}
          </span>
        </div>

        <div className="flex items-center gap-2">
          <span className="text-gray-500">{post.nickname}</span>
          <span className="text-gray-300">|</span>
          <span>{formatDate(post.createdAt)}</span>
        </div>
      </div>
    </div>
  );
}
