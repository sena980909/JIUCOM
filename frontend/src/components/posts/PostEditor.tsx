import { useState, useRef, type FormEvent } from 'react';
import type { CreatePostRequest, Post } from '../../api/posts';
import { uploadImage, type ImageUploadResponse } from '../../api/images';
import toast from 'react-hot-toast';

const BOARD_TYPES = [
  { value: 'FREE', label: '자유게시판' },
  { value: 'QNA', label: '질문답변' },
  { value: 'REVIEW', label: '리뷰' },
  { value: 'NOTICE', label: '공지사항' },
] as const;

interface PostEditorProps {
  initialData?: Post;
  onSubmit: (data: CreatePostRequest) => void;
}

export default function PostEditor({ initialData, onSubmit }: PostEditorProps) {
  const isEditMode = !!initialData;

  const [title, setTitle] = useState(initialData?.title ?? '');
  const [boardType, setBoardType] = useState(initialData?.boardType ?? 'FREE');
  const [content, setContent] = useState(initialData?.content ?? '');
  const [uploadedImages, setUploadedImages] = useState<ImageUploadResponse[]>([]);
  const [isUploading, setIsUploading] = useState(false);
  const [errors, setErrors] = useState<{ title?: string; content?: string }>({});
  const fileInputRef = useRef<HTMLInputElement>(null);

  function validate(): boolean {
    const newErrors: { title?: string; content?: string } = {};

    if (!title.trim()) {
      newErrors.title = '제목을 입력해주세요.';
    }
    if (!content.trim()) {
      newErrors.content = '내용을 입력해주세요.';
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  }

  function handleSubmit(e: FormEvent) {
    e.preventDefault();

    if (!validate()) return;

    // Append image URLs to content if any
    let finalContent = content;
    if (uploadedImages.length > 0) {
      const imageMarkdown = uploadedImages
        .map((img) => `\n![${img.filename}](${img.url})`)
        .join('');
      finalContent += imageMarkdown;
    }

    onSubmit({
      title: title.trim(),
      content: finalContent.trim(),
      boardType,
    });
  }

  async function handleImageUpload(e: React.ChangeEvent<HTMLInputElement>) {
    const files = e.target.files;
    if (!files || files.length === 0) return;

    setIsUploading(true);
    try {
      for (const file of Array.from(files)) {
        if (!file.type.startsWith('image/')) {
          toast.error(`${file.name}은(는) 이미지 파일이 아닙니다.`);
          continue;
        }
        if (file.size > 10 * 1024 * 1024) {
          toast.error(`${file.name}의 크기가 10MB를 초과합니다.`);
          continue;
        }
        const result = await uploadImage(file);
        // Handle ApiResponse wrapper
        const imageData = (result as { data?: ImageUploadResponse }).data ?? result;
        setUploadedImages((prev) => [...prev, imageData]);
        toast.success(`${file.name} 업로드 완료`);
      }
    } catch {
      toast.error('이미지 업로드에 실패했습니다.');
    } finally {
      setIsUploading(false);
      if (fileInputRef.current) {
        fileInputRef.current.value = '';
      }
    }
  }

  function removeImage(index: number) {
    setUploadedImages((prev) => prev.filter((_, i) => i !== index));
  }

  return (
    <form onSubmit={handleSubmit} className="space-y-6">
      {/* Board Type */}
      <div>
        <label htmlFor="boardType" className="block text-sm font-medium text-gray-700 mb-1">
          게시판
        </label>
        <select
          id="boardType"
          value={boardType}
          onChange={(e) => setBoardType(e.target.value)}
          disabled={isEditMode}
          className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent disabled:bg-gray-100 disabled:text-gray-500"
        >
          {BOARD_TYPES.map((type) => (
            <option key={type.value} value={type.value}>
              {type.label}
            </option>
          ))}
        </select>
      </div>

      {/* Title */}
      <div>
        <label htmlFor="title" className="block text-sm font-medium text-gray-700 mb-1">
          제목
        </label>
        <input
          id="title"
          type="text"
          value={title}
          onChange={(e) => {
            setTitle(e.target.value);
            if (errors.title) setErrors((prev) => ({ ...prev, title: undefined }));
          }}
          placeholder="제목을 입력해주세요"
          className={`w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent ${
            errors.title ? 'border-red-500' : 'border-gray-300'
          }`}
        />
        {errors.title && <p className="mt-1 text-sm text-red-500">{errors.title}</p>}
      </div>

      {/* Content */}
      <div>
        <label htmlFor="content" className="block text-sm font-medium text-gray-700 mb-1">
          내용
        </label>
        <textarea
          id="content"
          value={content}
          onChange={(e) => {
            setContent(e.target.value);
            if (errors.content) setErrors((prev) => ({ ...prev, content: undefined }));
          }}
          placeholder="내용을 입력해주세요"
          rows={15}
          className={`w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent resize-y ${
            errors.content ? 'border-red-500' : 'border-gray-300'
          }`}
        />
        {errors.content && <p className="mt-1 text-sm text-red-500">{errors.content}</p>}
      </div>

      {/* Image Upload */}
      <div>
        <label className="block text-sm font-medium text-gray-700 mb-1">이미지 첨부</label>
        <div className="flex items-center gap-3">
          <button
            type="button"
            onClick={() => fileInputRef.current?.click()}
            disabled={isUploading}
            className="px-4 py-2 text-sm border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {isUploading ? (
              <span className="flex items-center gap-2">
                <svg className="animate-spin w-4 h-4" fill="none" viewBox="0 0 24 24">
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
                업로드 중...
              </span>
            ) : (
              <span className="flex items-center gap-2">
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"
                  />
                </svg>
                이미지 선택
              </span>
            )}
          </button>
          <input
            ref={fileInputRef}
            type="file"
            accept="image/*"
            multiple
            onChange={handleImageUpload}
            className="hidden"
          />
          <span className="text-xs text-gray-400">최대 10MB, 여러 장 선택 가능</span>
        </div>

        {/* Uploaded image previews */}
        {uploadedImages.length > 0 && (
          <div className="mt-3 flex flex-wrap gap-3">
            {uploadedImages.map((img, index) => (
              <div key={index} className="relative group">
                <img
                  src={img.url}
                  alt={img.filename}
                  className="w-20 h-20 object-cover rounded-lg border border-gray-200"
                />
                <button
                  type="button"
                  onClick={() => removeImage(index)}
                  className="absolute -top-2 -right-2 w-5 h-5 bg-red-500 text-white rounded-full text-xs flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity"
                >
                  X
                </button>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Submit Button */}
      <div className="flex justify-end">
        <button
          type="submit"
          className="px-6 py-2.5 bg-blue-600 text-white text-sm font-medium rounded-lg hover:bg-blue-700 transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
        >
          {isEditMode ? '수정' : '작성'}
        </button>
      </div>
    </form>
  );
}
