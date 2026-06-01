import useAuth from '@/auth/Store'
import { Link } from 'react-router-dom'

function HomePage() {
  const user = useAuth((s) => s.user)

  return (
    <div className="space-y-6">
      
      {/* Hero Section */}
      <div className="rounded-3xl border border-slate-800 bg-slate-900/70 p-8 backdrop-blur">
        <p className="text-slate-400 text-sm">Welcome back</p>

        <h1 className="mt-2 text-3xl font-bold text-white">
          {user?.name || "User"}
        </h1>

        <p className="mt-3 max-w-2xl text-slate-400">
          This is a demo dashboard created for authentication flow testing.
          Explore profile information and navigate through protected routes.
        </p>

        <div className="mt-6 flex gap-3">
          <Link
            to="/dashboard/profile"
            className="rounded-xl bg-slate-700 px-5 py-2.5 text-sm font-medium hover:bg-slate-600 transition"
          >
            View Profile
          </Link>

          <Link
            to="/dashboard"
            className="rounded-xl border border-slate-700 px-5 py-2.5 text-sm hover:border-slate-500 transition"
          >
            Dashboard
          </Link>
        </div>
      </div>

      {/* Stats */}
      <div className="grid gap-4 md:grid-cols-3">
        <div className="rounded-2xl border border-slate-800 bg-slate-900 p-6">
          <p className="text-sm text-slate-400">Account Status</p>
          <h3 className="mt-2 text-xl font-semibold">Active</h3>
        </div>

        <div className="rounded-2xl border border-slate-800 bg-slate-900 p-6">
          <p className="text-sm text-slate-400">Authentication</p>
          <h3 className="mt-2 text-xl font-semibold">JWT Enabled</h3>
        </div>

        <div className="rounded-2xl border border-slate-800 bg-slate-900 p-6">
          <p className="text-sm text-slate-400">Provider</p>
          <h3 className="mt-2 text-xl font-semibold">
            {user?.provider || "Local"}
          </h3>
        </div>
      </div>

      {/* Activity Section */}
      <div className="rounded-3xl border border-slate-800 bg-slate-900 p-6">
        <h2 className="text-lg font-semibold">Recent Activity</h2>

        <div className="mt-5 space-y-4">
          <div className="flex items-center justify-between rounded-xl bg-slate-800/50 p-4">
            <span>Login Successful</span>
            <span className="text-xs text-slate-400">Just now</span>
          </div>

          <div className="flex items-center justify-between rounded-xl bg-slate-800/50 p-4">
            <span>Profile Available</span>
            <span className="text-xs text-slate-400">Ready</span>
          </div>

          <div className="flex items-center justify-between rounded-xl bg-slate-800/50 p-4">
            <span>Protected Routes Enabled</span>
            <span className="text-xs text-slate-400">Active</span>
          </div>
        </div>
      </div>

    </div>
  )
}

export default HomePage