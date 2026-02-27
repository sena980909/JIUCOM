import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts';

interface PriceDataPoint {
  date: string;
  price: number;
  sellerName: string;
}

interface PriceChartProps {
  data: PriceDataPoint[];
}

function formatPrice(value: number): string {
  if (value >= 10000) {
    return (value / 10000).toFixed(1) + '만원';
  }
  return value.toLocaleString('ko-KR') + '원';
}

const sellerColors = [
  '#3B82F6', // blue
  '#EF4444', // red
  '#10B981', // green
  '#F59E0B', // amber
  '#8B5CF6', // purple
  '#EC4899', // pink
  '#06B6D4', // cyan
  '#F97316', // orange
];

// eslint-disable-next-line @typescript-eslint/no-explicit-any
function CustomTooltip({ active, payload, label }: any) {
  if (!active || !payload || !payload.length) return null;

  return (
    <div className="bg-white shadow-lg rounded-lg border border-gray-200 p-3">
      <p className="text-sm font-medium text-gray-900 mb-2">{label}</p>
      {payload.map((entry: any, index: number) => (
        <div key={index} className="flex items-center gap-2 text-sm">
          <div
            className="w-3 h-3 rounded-full"
            style={{ backgroundColor: entry.color }}
          />
          <span className="text-gray-600">{entry.name}:</span>
          <span className="font-medium text-gray-900">
            {entry.value != null ? Number(entry.value).toLocaleString('ko-KR') + '원' : '-'}
          </span>
        </div>
      ))}
    </div>
  );
}

export default function PriceChart({ data }: PriceChartProps) {
  if (!data || data.length === 0) {
    return (
      <div className="flex items-center justify-center h-64 bg-gray-50 rounded-lg border border-gray-200">
        <p className="text-gray-500 text-sm">가격 데이터가 없습니다.</p>
      </div>
    );
  }

  // Group data by seller and restructure for Recharts
  const sellerNames = [...new Set(data.map((d) => d.sellerName))];
  const dateMap = new Map<string, Record<string, number>>();

  data.forEach((point) => {
    if (!dateMap.has(point.date)) {
      dateMap.set(point.date, {});
    }
    const entry = dateMap.get(point.date)!;
    entry[point.sellerName] = point.price;
  });

  const chartData = Array.from(dateMap.entries())
    .map(([date, sellers]) => ({
      date,
      ...sellers,
    }))
    .sort((a, b) => a.date.localeCompare(b.date));

  return (
    <ResponsiveContainer width="100%" height={320}>
      <LineChart data={chartData} margin={{ top: 5, right: 20, left: 10, bottom: 5 }}>
        <CartesianGrid strokeDasharray="3 3" stroke="#E5E7EB" />
        <XAxis
          dataKey="date"
          tick={{ fontSize: 12, fill: '#6B7280' }}
          tickLine={false}
        />
        <YAxis
          tickFormatter={formatPrice}
          tick={{ fontSize: 12, fill: '#6B7280' }}
          tickLine={false}
          axisLine={false}
          width={70}
        />
        <Tooltip content={<CustomTooltip />} />
        <Legend
          wrapperStyle={{ fontSize: 12, paddingTop: 8 }}
        />
        {sellerNames.map((seller, index) => (
          <Line
            key={seller}
            type="monotone"
            dataKey={seller}
            name={seller}
            stroke={sellerColors[index % sellerColors.length]}
            strokeWidth={2}
            dot={{ r: 3 }}
            activeDot={{ r: 5 }}
            connectNulls
          />
        ))}
      </LineChart>
    </ResponsiveContainer>
  );
}
