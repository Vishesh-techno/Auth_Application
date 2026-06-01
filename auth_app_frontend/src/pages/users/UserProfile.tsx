import { Link } from 'react-router-dom'
import useAuth from '@/auth/Store'
import { Avatar, AvatarImage, AvatarFallback, } from "@/components/ui/avatar";

function UserProfile() {
    const user = useAuth((s) => s.user)

    if (!user) {
        return (
            <div className="rounded-3xl border border-slate-800 bg-slate-900 p-8 text-center">
                <p className="text-slate-400">You are not signed in.</p>

                <Link
                    to="/login"
                    className="mt-4 inline-block rounded-xl bg-slate-700 px-5 py-2.5 text-sm text-white hover:bg-slate-600 transition"
                >
                    Sign In
                </Link>
            </div>
        )
    }

    return (
        <div className="space-y-6">

            {/* Profile Header */}
            <div className="rounded-3xl border border-slate-800 bg-slate-900 p-8">
                <div className="flex flex-col md:flex-row md:items-center gap-6">

                    <Avatar className="h-24 w-24 border border-slate-700">
                        <AvatarImage
                            src={`https://api.dicebear.com/9.x/adventurer/svg?seed=${user.email}`}
                            alt={user.name}
                        />

                        <AvatarFallback className="text-2xl">
                            {(user.name || user.email || "U")[0]?.toUpperCase()}
                        </AvatarFallback>
                    </Avatar>

                    <div>
                        <p className="text-sm text-slate-400">Profile</p>

                        <h1 className="mt-1 text-3xl font-bold text-white">
                            {user.name ?? "User"}
                        </h1>

                        <p className="mt-2 text-slate-400">
                            {user.email}
                        </p>
                    </div>
                </div>
            </div>

            {/* Details */}
            <div className="grid gap-4 md:grid-cols-2">

                <div className="rounded-2xl border border-slate-800 bg-slate-900 p-6">
                    <p className="text-xs uppercase tracking-wide text-slate-500">
                        Authentication Provider
                    </p>

                    <h3 className="mt-2 text-lg font-semibold">
                        {user.provider ?? "Local"}
                    </h3>
                </div>

                <div className="rounded-2xl border border-slate-800 bg-slate-900 p-6">
                    <p className="text-xs uppercase tracking-wide text-slate-500">
                        Member Since
                    </p>

                    <h3 className="mt-2 text-lg font-semibold">
                        {user.createdAt ? new Date(user.createdAt).toLocaleDateString()
                            : "Unknown"}
                    </h3>
                </div>

            </div>

            {/* Account Info */}
            <div className="rounded-3xl border border-slate-800 bg-slate-900 p-6">
                <h2 className="text-lg font-semibold">
                    Account Information
                </h2>

                <div className="mt-5 space-y-4">

                    <div className="flex justify-between border-b border-slate-800 pb-3">
                        <span className="text-slate-400">Name</span>
                        <span>{user.name ?? "-"}</span>
                    </div>

                    <div className="flex justify-between border-b border-slate-800 pb-3">
                        <span className="text-slate-400">Email</span>
                        <span>{user.email}</span>
                    </div>

                    <div className="flex justify-between">
                        <span className="text-slate-400">Provider</span>
                        <span>{user.provider ?? "Local"}</span>
                    </div>

                </div>
            </div>

            <div>
                <Link
                    to="/dashboard"
                    className="inline-flex items-center rounded-xl border border-slate-700 px-5 py-2.5 text-sm hover:border-slate-500 transition"
                >
                    ← Back to Dashboard
                </Link>
            </div>

        </div>
    )
}

export default UserProfile