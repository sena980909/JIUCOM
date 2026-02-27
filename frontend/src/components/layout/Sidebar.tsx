import { NavLink } from 'react-router-dom';

const adminLinks = [
  { to: '/admin', label: '대시보드', end: true },
  { to: '/admin/users', label: '회원관리', end: false },
  { to: '/admin/parts', label: '부품관리', end: false },
];

export default function Sidebar() {
  return (
    <aside className="w-64 shrink-0 border-r border-gray-200 bg-white">
      <div className="px-4 py-6">
        <h2 className="mb-4 text-lg font-semibold text-gray-900">관리자</h2>
        <nav className="flex flex-col gap-1">
          {adminLinks.map((link) => (
            <NavLink
              key={link.to}
              to={link.to}
              end={link.end}
              className={({ isActive }) =>
                `rounded-lg px-3 py-2 text-sm font-medium transition-colors ${
                  isActive
                    ? 'bg-blue-50 text-blue-700'
                    : 'text-gray-700 hover:bg-gray-100'
                }`
              }
            >
              {link.label}
            </NavLink>
          ))}
        </nav>
      </div>
    </aside>
  );
}
