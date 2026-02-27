import { Link } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { getParts } from '../api/parts';
import { getPosts } from '../api/posts';
import { getBuilds } from '../api/builds';
import PartCard from '../components/parts/PartCard';
import PostCard from '../components/posts/PostCard';
import BuildCard from '../components/builds/BuildCard';
import LoadingSpinner from '../components/common/LoadingSpinner';

export default function Home() {
  const {
    data: cpuData,
    isLoading: isCpuLoading,
  } = useQuery({
    queryKey: ['home', 'parts', 'CPU'],
    queryFn: () => getParts({ category: 'CPU', page: 0, size: 3 }),
  });

  const {
    data: gpuData,
    isLoading: isGpuLoading,
  } = useQuery({
    queryKey: ['home', 'parts', 'GPU'],
    queryFn: () => getParts({ category: 'GPU', page: 0, size: 3 }),
  });

  const partsData = (cpuData && gpuData) ? {
    content: [...(cpuData.content || []), ...(gpuData.content || [])],
  } : undefined;
  const isPartsLoading = isCpuLoading || isGpuLoading;

  const {
    data: postsData,
    isLoading: isPostsLoading,
  } = useQuery({
    queryKey: ['home', 'posts'],
    queryFn: () => getPosts({ page: 0, size: 5 }),
  });

  const {
    data: buildsData,
    isLoading: isBuildsLoading,
  } = useQuery({
    queryKey: ['home', 'builds'],
    queryFn: () => getBuilds({ page: 0, size: 4 }),
  });

  return (
    <div>
      {/* Hero Section */}
      <section className="bg-gradient-to-r from-blue-600 to-blue-800 text-white">
        <div className="mx-auto max-w-7xl px-4 py-20 sm:px-6 lg:px-8">
          <div className="text-center">
            <h1 className="text-4xl font-extrabold tracking-tight sm:text-5xl md:text-6xl">
              지우컴
            </h1>
            <p className="mx-auto mt-4 max-w-2xl text-lg text-blue-100 sm:text-xl">
              컴퓨터 부품 가격비교 & 견적 플랫폼
            </p>
            <p className="mx-auto mt-2 max-w-xl text-sm text-blue-200">
              다양한 판매처의 가격을 한눈에 비교하고, 나만의 PC 견적을 만들어보세요.
            </p>
            <div className="mt-8 flex items-center justify-center gap-4">
              <Link
                to="/parts"
                className="inline-flex items-center gap-2 rounded-lg bg-white px-6 py-3 text-base font-semibold text-blue-700 shadow-lg hover:bg-blue-50 transition-colors"
              >
                <svg className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                </svg>
                부품 검색
              </Link>
              <Link
                to="/builds"
                className="inline-flex items-center gap-2 rounded-lg border-2 border-white/30 px-6 py-3 text-base font-semibold text-white hover:bg-white/10 transition-colors"
              >
                견적 보기
              </Link>
            </div>
          </div>
        </div>
      </section>

      {/* Popular Parts Section */}
      <section className="py-12 bg-white">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold text-gray-900">인기 부품</h2>
            <Link
              to="/parts"
              className="text-sm font-medium text-blue-600 hover:text-blue-700 transition-colors flex items-center gap-1"
            >
              더보기
              <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
              </svg>
            </Link>
          </div>

          {isPartsLoading ? (
            <LoadingSpinner />
          ) : partsData?.content && partsData.content.length > 0 ? (
            <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-4">
              {partsData.content.map((part) => (
                <PartCard key={part.id} part={part} />
              ))}
            </div>
          ) : (
            <div className="rounded-lg bg-gray-50 py-12 text-center text-gray-500">
              등록된 부품이 없습니다.
            </div>
          )}
        </div>
      </section>

      {/* Latest Posts Section */}
      <section className="py-12 bg-gray-50">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold text-gray-900">최신 게시글</h2>
            <Link
              to="/posts"
              className="text-sm font-medium text-blue-600 hover:text-blue-700 transition-colors flex items-center gap-1"
            >
              더보기
              <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
              </svg>
            </Link>
          </div>

          {isPostsLoading ? (
            <LoadingSpinner />
          ) : postsData?.content && postsData.content.length > 0 ? (
            <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
              {postsData.content.map((post) => (
                <PostCard key={post.id} post={post} />
              ))}
            </div>
          ) : (
            <div className="rounded-lg bg-white py-12 text-center text-gray-500">
              게시글이 없습니다.
            </div>
          )}
        </div>
      </section>

      {/* Popular Builds Section */}
      <section className="py-12 bg-white">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-bold text-gray-900">인기 견적</h2>
            <Link
              to="/builds"
              className="text-sm font-medium text-blue-600 hover:text-blue-700 transition-colors flex items-center gap-1"
            >
              더보기
              <svg className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
              </svg>
            </Link>
          </div>

          {isBuildsLoading ? (
            <LoadingSpinner />
          ) : buildsData?.content && buildsData.content.length > 0 ? (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
              {buildsData.content.map((build) => (
                <BuildCard key={build.id} build={build} />
              ))}
            </div>
          ) : (
            <div className="rounded-lg bg-gray-50 py-12 text-center text-gray-500">
              공개된 견적이 없습니다.
            </div>
          )}
        </div>
      </section>
    </div>
  );
}
