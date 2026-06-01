import { FcGoogle } from "react-icons/fc";
import { FaGithub } from "react-icons/fa";


const baseUrl = import.meta.env.VITE_BASE_URL ?? "http://localhost:8080";

function OAuth2Buttons() {
    return (
        <div className="space-y-2">
            <a href={`${baseUrl}/oauth2/authorization/google`} className="block">
                <button type="button" className="flex w-full items-center justify-center gap-2 rounded-lg border border-slate-700 px-4 py-2.5 text-sm text-slate-200 transition hover:border-slate-600 hover:bg-slate-800">
                    <FcGoogle className="h-4 w-4" />
                    Google
                </button>
            </a>
            <a href={`${baseUrl}/oauth2/authorization/github`} className="block">
                <button type="button" className="flex w-full items-center justify-center gap-2 rounded-lg border border-slate-700 px-4 py-2.5 text-sm text-slate-200 transition hover:border-slate-600 hover:bg-slate-800">
                    <FaGithub className="h-4 w-4" />
                    GitHub
                </button>
            </a>
        </div>
    )
}

export default OAuth2Buttons
