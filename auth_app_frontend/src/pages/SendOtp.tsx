import { useState, useEffect } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import toast from 'react-hot-toast'
import axios from 'axios'
import { sendOtp } from '@/services/AuthService'

function SendOtp() {
  const [searchParams] = useSearchParams()
  const emailParam = searchParams.get('email') ?? ''
  const mode = searchParams.get('mode') ?? 'login'

  const [email, setEmail] = useState(emailParam)
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  useEffect(() => {
    if (emailParam) setEmail(emailParam)
  }, [emailParam])

  const handleSend = async () => {
    if (!email.trim()) {
      toast.error('Email required')
      return
    }

    try {
      setLoading(true)
      await sendOtp({ email })
      toast.success('OTP sent — check your email')
      navigate(`/verify-otp?email=${encodeURIComponent(email)}&mode=${mode}`)
    } catch (err: unknown) {
      console.error('Error details:', err)
      if (axios.isAxiosError(err)) {
        const data = err.response?.data as any
        const status = err.response?.status

        if (mode === 'signup' && status === 409) {
          toast.error('User already exists — please login with OTP')
          navigate(`/login?email=${encodeURIComponent(email)}&mode=otp`)
          return
        }

        if (mode === 'login' && status === 404) {
          toast.error('No account found with that email — please sign up')
          navigate('/signup')
          return
        }

        const message =
          (typeof data === 'string' ? data : null) ||
          data?.message ||
          data?.error ||
          err.response?.statusText ||
          err.message ||
          'Failed to send OTP'
        toast.error(message)
      } else {
        toast.error('Failed to send OTP')
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
            <h1 className="text-2xl font-semibold">{mode === 'signup' ? 'Sign up with Email OTP' : 'Login with Email OTP'}</h1>
            <p className="text-sm text-slate-400">{mode === 'signup' ? 'Send an OTP to this email to create your account.' : 'Send an OTP to this email to login.'}</p>
          </div>
          <div className="space-y-4">
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

            <button
              onClick={handleSend}
              className="w-full rounded-2xl text-lg bg-slate-700 px-4 py-2.5 text-sm font-medium text-slate-100 transition hover:bg-slate-600"
              disabled={loading}
            >
              {loading ? 'Sending...' : 'Send OTP'}
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}

export default SendOtp
