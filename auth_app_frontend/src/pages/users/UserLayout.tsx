import useAuth from "@/auth/Store"
import { Navigate, Outlet } from "react-router"

function UserLayout() {

  const checkLogin = useAuth((state) => state.checkLogin)
  const user = useAuth((state) => state.user)

  const authStatus = useAuth(state => state.authStatus)

  if (!authStatus) {
    return <Navigate to="/login" />
  }
  if (checkLogin()) {
    return (
      <div className="min-h-screen bg-slate-950 text-slate-100 px-4 py-8">
        <div className="max-w-5xl mx-auto">
          <header className="mb-8 flex items-center justify-between rounded-2xl border border-slate-800 bg-slate-900 px-6 py-4">
            <div>
              <h1 className="text-xl font-semibold">Dashboard</h1>
              <p className="text-sm text-slate-400">
                Authentication Demo Application
              </p>
            </div>

            {user && (
              <div className="text-right">
                <p className="font-medium">{user.name ?? user.email}</p>
                <p className="text-xs text-slate-400">{user.email}</p>
              </div>
            )}
          </header>

          <main>
            <Outlet />
          </main>
        </div>
      </div>
    )
  } else {
    return <Navigate to={"/login"} />
  }
}

export default UserLayout
