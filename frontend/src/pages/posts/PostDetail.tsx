import { useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import { getPostDetail, deletePost, type Post } from '../../api/posts';
import { getComments, createComment, type Comment } from '../../api/comments';
import { toggleLike, getLikeCount, type LikeResponse } from '../../api/likes';
import CommentTree from '../../components/posts/CommentTree';
import { useAuth } from '../../hooks/useAuth';

const BOARD_TYPE_CONFIG: Record<string, { label: string; color: string }> = {
  FREE: { label: '자유', color: 'bg-blue-100 text-blue-700' },
  QNA: { label: 'Q&A', color: 'bg-green-100 text-green-700' },
  REVIEW: { label: '리뷰', color: 'bg-purple-100 text-purple-700' },
  NOTICE: { label: '공지', color: 'bg-red-100 text-red-700' },
};

function formatDate(dateString: string): string {
  const date = new Date(dateString);
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });
}

export default function PostDetail() {
  const { id } = useParams<{ id: string }>();
  const postId = Number(id);
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { user, isAuthenticated } = useAuth();

  const [newComment, setNewComment] = useState('');

  // Fetch post detail
  const {
    data: post,
    isLoading: isPostLoading,
    isError: isPostError,
  } = useQuery<Post>({
    queryKey: ['post', postId],
    queryFn: async () => {
      const result = await getPostDetail(postId);
      return (result as { data?: Post }).data ?? result;
    },
    enabled: !!postId,
  });

  // Fetch comments
  const { data: comments = [] } = useQuery<Comment[]>({
    queryKey: ['comments', postId],
    queryFn: async () => {
      const result = await getComments(postId);
      return (result as { data?: Comment[] }).data ?? result;
    },
    enabled: !!postId,
  });

  // Fetch like status
  const { data: likeData } = useQuery<LikeResponse>({
    queryKey: ['like', 'POST', postId],
    queryFn: async () => {
      const result = await getLikeCount('POST', postId);
      return (result as { data?: LikeResponse }).data ?? result;
    },
    enabled: !!postId && isAuthenticated,
  });

  // Delete post mutation
  const deleteMutation = useMutation({
    mutationFn: () => deletePost(postId),
    onSuccess: () => {
      toast.success('게시글이 삭제되었습니다.');
      navigate('/posts');
    },
    onError: () => {
      toast.error('게시글 삭제에 실패했습니다.');
    },
  });

  // Like mutation
  const likeMutation = useMutation({
    mutationFn: () => toggleLike('POST', postId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['like', 'POST', postId] });
      queryClient.invalidateQueries({ queryKey: ['post', postId] });
    },
    onError: () => {
      toast.error('좋아요 처리에 실패했습니다.');
    },
  });

  // Create comment mutation
  const createCommentMutation = useMutation({
    mutationFn: (content: string) => createComment(postId, { content }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['comments', postId] });
      queryClient.invalidateQueries({ queryKey: ['post', postId] });
      setNewComment('');
      toast.success('댓글이 등록되었습니다.');
    },
    onError: () => {
      toast.error('댓글 등록에 실패했습니다.');
    },
  });

  function handleDelete() {
    if (window.confirm('게시글을 삭제하시겠습니까? 이 작업은 되돌릴 수 없습니다.')) {
      deleteMutation.mutate();
    }
  }

  function handleCommentSubmit() {
    if (!newComment.trim()) {
      toast.error('댓글 내용을 입력해주세요.');
      return;
    }
    createCommentMutation.mutate(newComment.trim());
  }

  const isOwner = user?.id === post?.userId;
  const boardConfig = post
    ? BOARD_TYPE_CONFIG[post.boardType] ?? {
        label: post.boardType,
        color: 'bg-gray-100 text-gray-700',
      }
    : null;

  // Loading state
  if (isPostLoading) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-16 text-center">
        <svg
          className="animate-spin w-8 h-8 text-blue-600 mx-auto mb-3"
          fill="none"
          viewBox="0 0 24 24"
        >
          <circle
            className="opacity-25"
            cx="12"
            cy="12"
            r="10"
            stroke="currentColor"
            strokeWidth="4"
          />
          <path
            className="opacity-75"
            fill="currentColor"
            d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
          />
        </svg>
        <p className="text-sm text-gray-500">게시글을 불러오는 중...</p>
      </div>
    );
  }

  // Error state
  if (isPostError || !post) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-16 text-center">
        <p className="text-sm text-red-500 mb-4">게시글을 찾을 수 없습니다.</p>
        <Link
          to="/posts"
          className="text-sm text-blue-600 hover:text-blue-700 transition-colors"
        >
          목록으로 돌아가기
        </Link>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      {/* Back link */}
      <Link
        to="/posts"
        className="inline-flex items-center gap-1 text-sm text-gray-500 hover:text-gray-700 transition-colors mb-6"
      >
        <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={2}
            d="M15 19l-7-7 7-7"
          />
        </svg>
        목록으로
      </Link>

      {/* Post Header */}
      <div className="border-b border-gray-200 pb-4 mb-6">
        <div className="flex items-center gap-2 mb-2">
          {boardConfig && (
            <span
              className={`inline-block px-2 py-0.5 rounded-full text-xs font-medium ${boardConfig.color}`}
            >
              {boardConfig.label}
            </span>
          )}
        </div>
        <h1 className="text-2xl font-bold text-gray-900 mb-3">{post.title}</h1>
        <div className="flex items-center justify-between text-sm text-gray-500">
          <div className="flex items-center gap-3">
            <span className="font-medium text-gray-700">{post.nickname}</span>
            <span>{formatDate(post.createdAt)}</span>
          </div>
          <div className="flex items-center gap-3">
            {/* View count */}
            <span className="flex items-center gap-1">
              <svg
                className="w-4 h-4"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
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
          </div>
        </div>
      </div>

      {/* Post Content */}
      <div className="prose prose-sm max-w-none mb-8 text-gray-800 leading-relaxed whitespace-pre-wrap">
        {post.content}
      </div>

      {/* Like & Actions */}
      <div className="flex items-center justify-between border-t border-b border-gray-200 py-4 mb-8">
        <button
          onClick={() => likeMutation.mutate()}
          disabled={!isAuthenticated || likeMutation.isPending}
          className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm font-medium transition-colors ${
            likeData?.liked
              ? 'bg-red-50 text-red-600 border border-red-200'
              : 'bg-gray-50 text-gray-600 border border-gray-200 hover:bg-gray-100'
          } disabled:opacity-50 disabled:cursor-not-allowed`}
        >
          <svg
            className="w-5 h-5"
            fill={likeData?.liked ? 'currentColor' : 'none'}
            stroke="currentColor"
            viewBox="0 0 24 24"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z"
            />
          </svg>
          좋아요 {likeData?.likeCount ?? post.likeCount}
        </button>

        {isOwner && (
          <div className="flex items-center gap-2">
            <Link
              to={`/posts/${post.id}/edit`}
              className="px-4 py-2 text-sm border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors"
            >
              수정
            </Link>
            <button
              onClick={handleDelete}
              disabled={deleteMutation.isPending}
              className="px-4 py-2 text-sm border border-red-300 text-red-600 rounded-lg hover:bg-red-50 transition-colors disabled:opacity-50"
            >
              {deleteMutation.isPending ? '삭제 중...' : '삭제'}
            </button>
          </div>
        )}
      </div>

      {/* Comments Section */}
      <div>
        <h2 className="text-lg font-semibold text-gray-900 mb-4">
          댓글 {post.commentCount > 0 && `(${post.commentCount})`}
        </h2>

        {/* Comment Tree */}
        <CommentTree comments={comments} postId={postId} />

        {/* New Comment Form */}
        {isAuthenticated ? (
          <div className="mt-6 border-t border-gray-200 pt-6">
            <textarea
              value={newComment}
              onChange={(e) => setNewComment(e.target.value)}
              placeholder="댓글을 입력해주세요"
              rows={3}
              className="w-full border border-gray-300 rounded-lg px-4 py-3 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-y"
            />
            <div className="flex justify-end mt-2">
              <button
                onClick={handleCommentSubmit}
                disabled={createCommentMutation.isPending}
                className="px-4 py-2 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 transition-colors disabled:opacity-50"
              >
                {createCommentMutation.isPending ? '등록 중...' : '댓글 등록'}
              </button>
            </div>
          </div>
        ) : (
          <div className="mt-6 border-t border-gray-200 pt-6 text-center">
            <p className="text-sm text-gray-400">
              댓글을 작성하려면{' '}
              <Link to="/login" className="text-blue-600 hover:text-blue-700">
                로그인
              </Link>
              이 필요합니다.
            </p>
          </div>
        )}
      </div>
    </div>
  );
}
