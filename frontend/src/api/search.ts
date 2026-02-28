import api from './client';

export interface SearchParams {
  keyword: string;
  type?: string;
  page?: number;
  size?: number;
}

export interface SearchResult {
  id: number;
  title: string;
  description: string;
  type: string;
  url: string;
}

// Backend returns {parts: [...], posts: [...], builds: [...]} (non-paginated)
export interface SearchRawResponse {
  parts: { id: number; name: string; category: string; manufacturer: string; lowestPrice: number; imageUrl: string | null }[];
  posts: { id: number; title: string; boardType: string; authorNickname: string }[];
  builds: { id: number; name: string; description: string; totalPrice: number; ownerNickname: string }[];
}

export interface SearchResponse {
  content: SearchResult[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

export interface SuggestResponse {
  suggestions: string[];
}

export const search = async (params: SearchParams): Promise<SearchResponse> => {
  const response = await api.get<SearchRawResponse>('/search', { params });
  const raw = response.data as unknown as SearchRawResponse;

  // Transform backend flat structure to unified SearchResult[]
  const results: SearchResult[] = [
    ...(raw.parts ?? []).map((p) => ({
      id: p.id,
      title: p.name,
      description: `${p.manufacturer} · ${p.category}${p.lowestPrice ? ` · ${p.lowestPrice.toLocaleString()}원` : ''}`,
      type: 'PART',
      url: `/parts/${p.id}`,
    })),
    ...(raw.posts ?? []).map((p) => ({
      id: p.id,
      title: p.title,
      description: `${p.authorNickname} · ${p.boardType}`,
      type: 'POST',
      url: `/posts/${p.id}`,
    })),
    ...(raw.builds ?? []).map((b) => ({
      id: b.id,
      title: b.name,
      description: `${b.ownerNickname}${b.totalPrice ? ` · ${b.totalPrice.toLocaleString()}원` : ''}`,
      type: 'BUILD',
      url: `/builds/${b.id}`,
    })),
  ];

  // Client-side type filtering
  const filtered = params.type
    ? results.filter((r) => r.type === params.type)
    : results;

  // Client-side pagination
  const page = params.page ?? 0;
  const size = params.size ?? 10;
  const start = page * size;
  const paged = filtered.slice(start, start + size);

  return {
    content: paged,
    totalElements: filtered.length,
    totalPages: Math.ceil(filtered.length / size),
    size,
    number: page,
  };
};

export const suggest = async (keyword: string) => {
  const response = await api.get<SuggestResponse>('/search/suggest', {
    params: { keyword },
  });
  return response.data;
};
