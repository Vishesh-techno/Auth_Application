import { useState, useEffect } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import toast from 'react-hot-toast'
import axios from 'axios'
import { verifyOtp } from '@/services/AuthService'
import useAuth from '@/auth/Store'

function VerifyOtp() {
  const [searchParams] = useSearchParams()
  const emailParam = searchParams.get('email') ?? ''
  const mode = searchParams.get('mode') ?? 'login'

  const [email, setEmail] = useState(emailParam)
  const [code, setCode] = useState('')
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()
  const loginWithOtp = useAuth(state => state.loginWithOtp);

  useEffect(() => {
    if (emailParam) setEmail(emailParam)
  }, [emailParam])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!email.trim()) {
      toast.error('Email required')
      return
    }
    if (!code.trim()) {
      toast.error('OTP code required')
      return
    }

    try {
      setLoading(true)
      const response = await verifyOtp({
        email,
        otp: code
      });

      console.log("OTP RESPONSE", response);

      loginWithOtp(response);
      navigate("/dashboard"); 
      toast.success('Verified — redirecting to dashboard')
    } catch (err: unknown) {
      console.error('Error details:', err)
      if (axios.isAxiosError(err)) {
        if (err.code === 'ECONNABORTED') {
          toast.error('Request timed out. Please try again.')
        } else {
          const data = err.response?.data as any
          const message =
            (typeof data === 'string' ? data : null) ||
            data?.message ||
            data?.error ||
            err.response?.statusText ||
            err.message ||
            'OTP verification failed'
          console.log('Extracted message:', message)
          toast.error(message)
        }
      } else {
        toast.error('OTP verification failed')
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex items-center justify-center px-4">
      <div className="w-full max-w-sm rounded-2xl border border-slate-700 bg-slate-900 p-8 shadow-2xl shadow-black/60">
        <div className="space-y-6">
          <div className="space-y-2 text-center">
            <h1 className="text-2xl font-semibold">Verify OTP</h1>
            <p className="text-sm text-slate-400">Enter the code sent to your email to {mode === 'signup' ? 'create your account' : 'sign in'}</p>
          </div>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1.5">Email</label>
              <div className="flex items-center gap-2 rounded-lg border border-slate-700 bg-slate-800 px-3 py-2.5">
                <input
                  id="email"
                  type="email"
                  placeholder="you@example.com"
                  className="w-full bg-transparent text-sm text-slate-100 outline-none placeholder:text-slate-500"
                  name="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1.5">OTP Code</label>
              <div className="flex items-center gap-2 rounded-lg border border-slate-700 bg-slate-800 px-3 py-2.5">
                <input
                  id="code"
                  type="text"
                  placeholder="Enter code"
                  className="w-full bg-transparent text-sm text-slate-100 outline-none placeholder:text-slate-500"
                  name="code"
                  value={code}
                  onChange={(e) => setCode(e.target.value)}
                />
              </div>
            </div>

            <button
              type="submit"
              className="w-full rounded-2xl text-lg bg-slate-700 px-4 py-2.5 text-sm font-medium text-slate-100 transition hover:bg-slate-600"
            >
              {loading ? 'Verifying...' : 'Verify OTP'}
            </button>
          </form>
        </div>
      </div>
    </div>
  )
}

export default VerifyOtp
