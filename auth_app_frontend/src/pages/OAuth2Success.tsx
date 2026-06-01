import { useEffect, useState } from 'react'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import toast from 'react-hot-toast'
import useAuth from '@/auth/Store'
import type LoginResponseData from '@/models/LoginResponseData'

function OAuth2Success() {
    const [searchParams] = useSearchParams()
    const navigate = useNavigate()
    const setAuthSession = useAuth(state => state.setAuthSession)
    const [statusMessage, setStatusMessage] = useState('Processing OAuth sign in...')

    useEffect(() => {
        const accessToken = searchParams.get('accessToken')
        const refreshToken = searchParams.get('refreshToken')
        // const userParam = searchParams.get('user')
        const user = {
            email: searchParams.get("email"),
            name: searchParams.get("name"),
            image: searchParams.get("image"),
        };
        // let user = null
        // if (userParam) {
        //     try {
        //         user = JSON.parse(decodeURIComponent(userParam))
        //     } catch {
        //         user = null
        //     }
        // }

        if (!accessToken) {
            const errorMessage = searchParams.get('error') ?? 'OAuth authentication failed.'
            setStatusMessage(errorMessage)
            toast.error(errorMessage)
            return
        }

        setAuthSession({
            accessToken,
            refreshToken: refreshToken ?? undefined,
            user: user ?? undefined
        } as Partial<LoginResponseData>)

        toast.success('Signed in successfully')
        navigate('/dashboard')
    }, [navigate, searchParams, setAuthSession])


    return (
        <div className="min-h-screen bg-slate-950 text-slate-100 flex items-center justify-center px-4">
            <div className="w-full max-w-md rounded-2xl border border-slate-700 bg-slate-900 p-8 shadow-2xl shadow-black/60 text-center">
                <h1 className="text-2xl font-semibold">OAuth Login</h1>
                <p className="mt-4 text-slate-400">{statusMessage}</p>
                <div className="mt-6">
                    <Link to="/login" className="rounded-lg border border-slate-700 px-4 py-2 text-sm text-slate-100 transition hover:border-slate-500">
                        Back to login
                    </Link>
                </div>
            </div>
        </div>
    )
}

export default OAuth2Success
