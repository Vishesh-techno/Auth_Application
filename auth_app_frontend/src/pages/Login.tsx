import React, { useEffect, useState } from 'react'
import axios, { type AxiosError } from 'axios'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import { Mail, Lock, CheckCircle2Icon } from 'lucide-react'
import type LoginData from '@/models/LoginData'
import toast from 'react-hot-toast'
import { Alert, AlertTitle } from '@/components/ui/alert'
import { Spinner } from '@/components/ui/spinner'
import useAuth from '@/auth/Store'
import OAuth2Buttons from '@/components/OAuth2Buttons'

function Login() {
  const [searchParams] = useSearchParams()
  const emailParam = searchParams.get('email') ?? ''
  

  const [loginData, setLoginData] = useState<LoginData>({
    email: emailParam,
    password: '',
    code: ''
  });
  const [error, setError] = useState<AxiosError | null>(null);
  const [loading, setLoading] = useState<boolean>(false);

  const login = useAuth((state) => state.login);
  // OTP navigation uses the VerifyOtp page; no local otpSent flag needed

  useEffect(() => {
    if (emailParam) {
      setLoginData((current) => ({
        ...current,
        email: emailParam,
      }))
    }
  }, [emailParam])

  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setLoginData({
      ...loginData,
      [event.target.name]: event.target.value
    })
  };

  const navigate = useNavigate();

  const handleFormSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    // console.log(event.target)
    // console.log(loginData);

    if (loginData.email.trim() === "") {
      toast.error("Email is required !!");
      return;
    }

    if ((loginData.password ?? '').trim() === "") {
      toast.error("Password is required !!");
      return;
    }

    try {
      setLoading(true);

      const response = await login(loginData);

      if (response) {
        toast.success("Login Success");
        navigate("/dashboard");
      }
    }
    catch (err: unknown) {
      console.log(err);
      toast.error("Invalid Email or Password");
      if (axios.isAxiosError(err)) {
        setError(err)
      } else {
        setError(null)
      }
    }
    finally {
      setLoading(false);
    }
  }

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 flex items-center justify-center px-4">
      <div className="w-full max-w-sm rounded-2xl border border-slate-700 bg-slate-900 p-8 shadow-2xl shadow-black/60">
        <div className="space-y-6">
          <div className="space-y-2 text-center">
            <h1 className="text-2xl font-semibold">Sign In</h1>
            <p className="text-sm text-slate-400">Enter your credentials to continue</p>
          </div>

          {
            error && <div>
              <Alert variant={"destructive"}>
                <CheckCircle2Icon />
                <AlertTitle>{(error?.response?.data as { message?: string } | undefined)?.message}</AlertTitle>
              </Alert>
            </div>
          }

          <div className="text-xs text-slate-400">Sign in with your password, or use Email OTP via the button below.</div>

          <form onSubmit={handleFormSubmit} className="space-y-4">
            <div>
              <label className="block text-xs font-medium text-slate-300 mb-1.5">Email</label>
              <div className="flex items-center gap-2 rounded-lg border border-slate-700 bg-slate-800/50 px-3 py-2.5">
                <Mail className="h-4 w-4 text-slate-400" />
                <input
                  id="email"
                  type="email"
                  placeholder="you@example.com"
                  className="w-full bg-transparent text-sm text-slate-100 outline-none placeholder:text-slate-500"
                  name="email"
                  value={loginData.email}
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
                  type="password"
                  placeholder="Your password"
                  className="w-full bg-transparent text-sm text-slate-100 outline-none placeholder:text-slate-500"
                  name="password"
                  value={loginData.password}
                  onChange={handleInputChange}
                />
              </div>
            </div>

            <button type="submit" className="w-full rounded-2xl text-lg bg-slate-700 px-4 py-2.5 text-sm font-medium text-slate-100 transition hover:bg-slate-600">
              {loading ? <><Spinner />Signing...</> : 'Continue'}
            </button>

            <button type="button" onClick={() => {
              const query = loginData.email.trim() ? `?email=${encodeURIComponent(loginData.email)}&mode=login` : '?mode=login'
              navigate(`/send-otp${query}`)
            }} className="w-full mt-2 rounded-2xl border border-slate-700 px-4 py-2.5 text-sm font-medium text-slate-200 transition hover:border-slate-600 hover:bg-slate-800">
              Login with Email OTP
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
            Don't have an account?{' '}
            <Link to="/signup" className="text-slate-300 hover:text-slate-200">
              Sign up
            </Link>
          </p>
        </div>
      </div>
    </div>
  )
}

export default Login
