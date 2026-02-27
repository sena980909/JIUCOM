import { useParams, useNavigate } from 'react-router-dom';
import { useQuery, useMutation } from '@tanstack/react-query';
import toast from 'react-hot-toast';
import {
  getPostDetail,
  createPost,
  updatePost,
  type Post,
  type CreatePostRequest,
} from '../../api/posts';
import PostEditorForm from '../../components/posts/PostEditor';
import { useAuth } from '../../hooks/useAuth';

export default function PostEditorPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const isEditMode = !!id;
  const postId = Number(id);

  // Fetch existing post for edit mode
  const { data: existingPost, isLoading } = useQuery<Post>({
    queryKey: ['post', postId],
    queryFn: async () => {
      const result = await getPostDetail(postId);
      return (result as { data?: Post }).data ?? result;
    },
    enabled: isEditMode,
  });

  // Create post mutation
  const createMutation = useMutation({
    mutationFn: (data: CreatePostRequest) => createPost(data),
    onSuccess: (result) => {
      const post = (result as { data?: Post }).data ?? (result as Post);
      toast.success('게시글이 작성되었습니다.');
      navigate(`/posts/${post.id}`);
    },
    onError: () => {
      toast.error('게시글 작성에 실패했습니다.');
    },
  });

  // Update post mutation
  const updateMutation = useMutation({
    mutationFn: (data: CreatePostRequest) =>
      updatePost(postId, { title: data.title, content: data.content }),
    onSuccess: () => {
      toast.success('게시글이 수정되었습니다.');
      navigate(`/posts/${postId}`);
    },
    onError: () => {
      toast.error('게시글 수정에 실패했습니다.');
    },
  });

  function handleSubmit(data: CreatePostRequest) {
    if (isEditMode) {
      updateMutation.mutate(data);
    } else {
      createMutation.mutate(data);
    }
  }

  // Auth guard
  if (!isAuthenticated) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-16 text-center">
        <p className="text-sm text-gray-500">로그인이 필요합니다.</p>
      </div>
    );
  }

  // Loading for edit mode
  if (isEditMode && isLoading) {
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

  return (
    <div className="max-w-4xl mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">
        {isEditMode ? '게시글 수정' : '새 게시글 작성'}
      </h1>

      <PostEditorForm
        initialData={existingPost}
        onSubmit={handleSubmit}
      />

      {(createMutation.isPending || updateMutation.isPending) && (
        <div className="fixed inset-0 bg-black/20 flex items-center justify-center z-50">
          <div className="bg-white rounded-lg px-6 py-4 shadow-lg flex items-center gap-3">
            <svg
              className="animate-spin w-5 h-5 text-blue-600"
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
            <span className="text-sm text-gray-700">
              {isEditMode ? '게시글을 수정하는 중...' : '게시글을 작성하는 중...'}
            </span>
          </div>
        </div>
      )}
    </div>
  );
}
