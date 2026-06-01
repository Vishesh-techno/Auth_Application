// import useAuth from '@/auth/Store'
// import { Link, useNavigate } from 'react-router-dom'

// function Navbar() {
//   const checkLogin = useAuth(state => state.checkLogin)
//   const logout = useAuth(state => state.logout)
//   const user = useAuth(state => state.user)
//   const isLoggedIn = checkLogin()
//   const navigate = useNavigate();

//   return (
//     <nav className="border-b border-slate-800/80 bg-slate-950/95 px-4 py-4 backdrop-blur-xl">
//       <div className="mx-auto flex max-w-6xl items-center justify-between gap-4">
//         <Link to="/" className="text-lg font-semibold text-slate-100">
//           Auth App
//         </Link>
//         <div className="flex flex-wrap items-center gap-3 text-sm text-slate-300">
//           {isLoggedIn ? (
//             <>
//               <span className="transition hover:text-slate-100">{user?.name ?? 'User'}</span>
//               <button
//                 onClick={() => {
//                   logout();
//                   navigate("/");
//                 }
//                 }
//                 className="rounded-full border border-slate-700 px-4 py-2 text-slate-100 transition hover:border-slate-500"
//               >
//                 Logout
//               </button>
//             </>
//           ) : (
//             <>
//               <Link to="/" className="transition hover:text-slate-100">
//                 Home
//               </Link>
//               <Link to="/login" className="rounded-full border border-slate-700 px-4 py-2 transition hover:border-slate-500">
//                 Login
//               </Link>
//               <Link to="/signup" className="rounded-full border border-slate-700 px-4 py-2 transition hover:border-slate-500">
//                 Signup
//               </Link>
//             </>
//           )}
//         </div>
//       </div>
//     </nav>
//   )
// }

// export default Navbar


import useAuth from '@/auth/Store'
import { Link, useNavigate } from 'react-router-dom'

function Navbar() {
  const authStatus = useAuth(state => state.authStatus)
  const logout = useAuth(state => state.logout)
  const user = useAuth(state => state.user)
  console.log("Navbar Auth Status:", authStatus)
  const navigate = useNavigate();

  return (
    <nav className="border-b border-slate-800/80 bg-slate-950/95 px-4 py-4 backdrop-blur-xl">
      <div className="mx-auto flex max-w-6xl items-center justify-between gap-4">
        <Link
          to={authStatus ? "/dashboard" : "/"}
          className="text-lg font-semibold text-slate-100"
        >
          Auth App
        </Link>

        <div className="flex flex-wrap items-center gap-3 text-sm text-slate-300">
          {authStatus ? (
            <>
              <span>{user?.name ?? 'User'}</span>

              <button
                onClick={async () => {
                  logout();
                  navigate("/");
                }}
                className="rounded-full border border-slate-700 px-4 py-2 text-slate-100"
              >
                Logout
              </button>
            </>
          ) : (
            <>
              <Link to="/">Home</Link>
              <Link to="/login">Login</Link>
              <Link to="/signup">Signup</Link>
            </>
          )}
        </div>
      </div>
    </nav>
  )
}

export default Navbar