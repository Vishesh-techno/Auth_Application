import { Link, useSearchParams } from 'react-router-dom'

function OAuth2Failure() {
  const [searchParams] = useSearchParams()
  const error = searchParams.get('error') ?? 'OAuth authentication failed.'

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex items-center justify-center px-4">
      <div className="w-full max-w-md rounded-2xl border border-slate-700 bg-slate-900 p-8 shadow-2xl shadow-black/60 text-center">
        <h1 className="text-2xl font-semibold text-rose-400">OAuth Login Failed</h1>
        <p className="mt-4 text-slate-400">{error}</p>
        <div className="mt-6">
          <Link to="/login" className="rounded-lg border border-slate-700 px-4 py-2 text-sm text-slate-100 transition hover:border-slate-500">
            Back to login
          </Link>
        </div>
      </div>
    </div>
  )
}

export default OAuth2Failure
