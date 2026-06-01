import { Link, useNavigate } from 'react-router-dom'
import { useState } from 'react'
import type { ChangeEvent } from 'react'
import { Mail, Lock, User } from 'lucide-react'
import axios from 'axios'
import toast from 'react-hot-toast'
import type RegisterData from '@/models/RegisterData'
import { registerUser } from '@/services/AuthService'
import useAuth from '@/auth/Store'
import OAuth2Buttons from '@/components/OAuth2Buttons'

function Signup() {
  const [data, setData] = useState<RegisterData>({
    name: '',
    email: '',
    password: '',
  })

  const handleInputChange = (event: ChangeEvent<HTMLInputElement>) => {
    setData((value) => ({
      ...value,
      [event.target.name]: event.target.value,
    }))
  };

  const navigate = useNavigate();
  const setAuthSession = useAuth(state => state.setAuthSession);

  const handleFormSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    console.log(data);

    if (data.name.trim() === "") {
      toast.error("Name is required!!")
      return;
    }

    if (data.email.trim() === "") {
      toast.error("Email is required!!")
      return;
    }

    if (data.password.trim() === "") {
      toast.error("Password is required!!")
      return;
    }

    try {
      // Default password signup
      const result = await registerUser(data);
      console.log(result);

      if (result?.accessToken) {
        setAuthSession(result);
        toast.success("Account created and logged in successfully")
        navigate("/dashboard")
        return;
      }

      toast.success("User registered Successfully...")
      setData({
        name: '',
        email: '',
        password: '',
      });
      navigate("/login")
    } catch (error: unknown) {
      console.log(error);
      if (axios.isAxiosError(error)) {
        const data = error.response?.data as any
        const message =
          (typeof data === 'string' ? data : null) ||
          data?.message ||
          data?.error ||
          error.response?.statusText ||
          error.message ||
          'Error in registering the user'
        toast.error(message)
        if (error.response?.status === 409) {
          navigate('/login')
        }
      } else {
        toast.error('Error in registering the User')
      }
    }
  };

  const handleGoToSendOtp = () => {
    const query = data.email.trim()
      ? `?email=${encodeURIComponent(data.email)}&mode=signup`
      : '?mode=signup'

    navigate(`/send-otp${query}`)
  }

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex items-center justify-center px-4">
      <div className="w-full max-w-sm rounded-2xl border border-slate-700 bg-slate-900 p-8 shadow-2xl shadow-black/60">
        <div className="space-y-6">
          <div className="space-y-2 text-center">
            <h1 className="text-2xl font-semibold">Create Account</h1>
            <p className="text-sm text-slate-400">Sign up with your email or provider</p>
          </div>

          <form onSubmit={handleFormSubmit} className="space-y-4">
            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1.5">Email</label>
              <div className="flex items-center gap-2 rounded-lg border border-slate-700 bg-slate-800 px-3 py-2.5">
                <Mail className="h-4 w-4 text-slate-400" />
                <input
                  id="email"
                  name="email"
                  value={data.email}
                  onChange={handleInputChange}
                  type="email"
                  placeholder="you@example.com"
                  className="w-full bg-transparent text-sm text-slate-100 outline-none placeholder:text-slate-500"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1.5">Full Name</label>
              <div className="flex items-center gap-2 rounded-lg border border-slate-700 bg-slate-800 px-3 py-2.5">
                <User className="h-4 w-4 text-slate-400" />
                <input
                  id="name"
                  type="text"
                  placeholder="Your full name"
                  className="w-full bg-transparent text-sm text-slate-100 outline-none placeholder:text-slate-500"
                  name="name"
                  value={data.name}
                  onChange={handleInputChange}
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1.5">Password</label>
              <div className="flex items-center gap-2 rounded-lg border border-slate-700 bg-slate-800 px-3 py-2.5">
                <Lock className="h-4 w-4 text-slate-400" />
                <input
                  id="password"
                  name="password"
                  value={data.password}
                  onChange={handleInputChange}
                  type="password"
                  placeholder="Choose a password"
                  className="w-full bg-transparent text-sm text-slate-100 outline-none placeholder:text-slate-500"
                />
              </div>
            </div>

            <button type="submit" className="w-full rounded-lg bg-slate-700 px-4 py-2.5 text-sm font-medium text-slate-100 transition hover:bg-slate-600">
              Create Account
            </button>

            <button type="button" onClick={handleGoToSendOtp} className="w-full mt-2 rounded-lg border border-slate-700 px-4 py-2.5 text-sm font-medium text-slate-200 transition hover:border-slate-600 hover:bg-slate-800">
              Sign up with Email OTP
            </button>
          </form>

          <div className="relative">
            <div className="absolute inset-0 flex items-center">
              <div className="w-full border-t border-slate-700"></div>
            </div>
            <div className="relative flex justify-center text-xs">
              <span className="bg-slate-900 px-2 text-slate-400">or</span>
            </div>
          </div>

          <OAuth2Buttons />

          <p className="text-center text-xs text-slate-400">
            Already have an account?{' '}
            <Link to="/login" className="text-slate-300 hover:text-slate-200">
              Sign in
            </Link>
          </p>
        </div>
      </div>
    </div>
  )
}

export default Signup
