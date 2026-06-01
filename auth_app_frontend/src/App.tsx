import './App.css'
import { Link } from 'react-router-dom'

function App() {
  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex items-center justify-center px-4">
      <div className="w-full max-w-sm rounded-2xl border border-slate-700 bg-slate-900 p-8 shadow-2xl shadow-black/60">
        <div className="space-y-6 text-center">
          <div className="space-y-2">
            <h1 className="text-2xl font-semibold">Welcome</h1>
            <p className="text-sm text-slate-400">Choose an option to continue</p>
          </div>
          
          <div className="space-y-3">
            <Link
              to="/login"
              className="block w-full rounded-lg bg-slate-700 px-4 py-3 text-center text-sm font-medium text-slate-100 transition hover:bg-slate-600"
            >
              Sign In
            </Link>
            <Link
              to="/signup"
              className="block w-full rounded-lg border border-slate-600 px-4 py-3 text-center text-sm font-medium text-slate-100 transition hover:border-slate-500 hover:bg-slate-800"
            >
              Create Account
            </Link>
          </div>
        </div>
      </div>
    </div>
  )
}

export default App
