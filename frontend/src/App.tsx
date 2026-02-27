import { Routes, Route } from 'react-router-dom'
import Layout from './components/layout/Layout'
import ProtectedRoute from './components/common/ProtectedRoute'

import Home from './pages/Home'
import Login from './pages/auth/Login'
import Signup from './pages/auth/Signup'
import OAuthCallback from './pages/auth/OAuthCallback'
import PartList from './pages/parts/PartList'
import PartDetail from './pages/parts/PartDetail'
import BuildList from './pages/builds/BuildList'
import BuildDetail from './pages/builds/BuildDetail'
import BuildEditor from './pages/builds/BuildEditor'
import PostList from './pages/posts/PostList'
import PostDetail from './pages/posts/PostDetail'
import PostEditor from './pages/posts/PostEditor'
import SearchResults from './pages/search/SearchResults'
import SellerList from './pages/sellers/SellerList'
import Profile from './pages/user/Profile'
import MyBuilds from './pages/user/MyBuilds'
import MyFavorites from './pages/user/MyFavorites'
import Notifications from './pages/user/Notifications'
import Dashboard from './pages/admin/Dashboard'
import UserManagement from './pages/admin/UserManagement'
import PartManagement from './pages/admin/PartManagement'

function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        {/* Public routes */}
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/oauth/callback" element={<OAuthCallback />} />
        <Route path="/parts" element={<PartList />} />
        <Route path="/parts/:id" element={<PartDetail />} />
        <Route path="/builds" element={<BuildList />} />
        <Route path="/builds/:id" element={<BuildDetail />} />
        <Route path="/posts" element={<PostList />} />
        <Route path="/posts/:id" element={<PostDetail />} />
        <Route path="/search" element={<SearchResults />} />
        <Route path="/sellers" element={<SellerList />} />

        {/* Authenticated routes */}
        <Route element={<ProtectedRoute />}>
          <Route path="/builds/new" element={<BuildEditor />} />
          <Route path="/builds/:id/edit" element={<BuildEditor />} />
          <Route path="/posts/new" element={<PostEditor />} />
          <Route path="/posts/:id/edit" element={<PostEditor />} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/my/builds" element={<MyBuilds />} />
          <Route path="/my/favorites" element={<MyFavorites />} />
          <Route path="/notifications" element={<Notifications />} />
        </Route>

        {/* Admin routes */}
        <Route element={<ProtectedRoute requiredRole="ADMIN" />}>
          <Route path="/admin" element={<Dashboard />} />
          <Route path="/admin/users" element={<UserManagement />} />
          <Route path="/admin/parts" element={<PartManagement />} />
        </Route>
      </Route>
    </Routes>
  )
}

export default App
