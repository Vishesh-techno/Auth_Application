import { Navigate, Outlet } from "react-router-dom";
import useAuth from "@/auth/Store";

function PublicRoute() {
    const authStatus = useAuth(state => state.authStatus);

    if (authStatus) {
        return <Navigate to="/dashboard" replace />;
    }

    return <Outlet />;
}

export default PublicRoute;