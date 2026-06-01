import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App'
import Navbar from './components/Navbar'
import { Toaster } from 'react-hot-toast'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Login from './pages/Login'
import Signup from './pages/Signup'
import VerifyOtp from './pages/VerifyOtp'
import SendOtp from './pages/SendOtp'
import About from './pages/About'
import Services from './pages/Services'
import UserLayout from './pages/users/UserLayout'
import HomePage from './pages/users/HomePage'
import UserProfile from './pages/users/UserProfile'
import PublicRoute from './components/PublicRoute'
import OAuth2Success from './pages/OAuth2Success'
import OAuth2Failure from './pages/OAuth2Failure'

// createRoot(document.getElementById('root')!).render(
//   <BrowserRouter>
//     <Toaster />
//     <Navbar />
//     <Routes>
//       <Route path='/' element={<App />} />
//       <Route path='/login' element={<Login />} />
//       <Route path='/signup' element={<Signup />} />
//       <Route path='/send-otp' element={<SendOtp />} />
//       <Route path='/verify-otp' element={<VerifyOtp />} />
//       <Route path='/service' element={<Services />} />
//       <Route path='/about' element={<About />} />
//       <Route path='/dashboard' element={<UserLayout />}>
//         <Route index element={<HomePage />} />
//         <Route path="profile" element={<UserProfile />} />
//       </Route>
//     </Routes>
//   </BrowserRouter>
// )

createRoot(document.getElementById('root')!).render(
  <BrowserRouter>
    <Toaster />
    <Navbar />

    <Routes>

      <Route path='/' element={<App />} />

      <Route element={<PublicRoute />}>
        <Route path='/login' element={<Login />} />
        <Route path='/signup' element={<Signup />} />
      </Route>

      <Route path='/send-otp' element={<SendOtp />} />
      <Route path='/verify-otp' element={<VerifyOtp />} />
      <Route path='/service' element={<Services />} />
      <Route path='/about' element={<About />} />
      <Route path='/oauth/success' element={<OAuth2Success />} />
      <Route path='/oauth/failure' element={<OAuth2Failure />} />

      <Route path='/dashboard' element={<UserLayout />}>
        <Route index element={<HomePage />} />
        <Route path='profile' element={<UserProfile />} />
      </Route>

    </Routes>
  </BrowserRouter>
)