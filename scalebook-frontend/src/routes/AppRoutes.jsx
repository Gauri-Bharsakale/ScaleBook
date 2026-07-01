// src/routes/AppRoutes.jsx

import { Navigate, Route, Routes } from 'react-router-dom'
import { useAuthContext } from '../context/AuthContext'
import LoginPage from '../pages/LoginPage'
import RegisterPage from '../pages/RegisterPage'
import DashboardPage from '../pages/DashboardPage'
import ResourceListPage from '../pages/ResourceListPage'
import BookingCalendarPage from '../pages/BookingCalendarPage'
import AdminDashboardPage from '../pages/AdminDashboardPage'

function ProtectedRoute({ children }) {
    const { user } = useAuthContext()
    return user ? children : <Navigate to="/login" replace />
}

function AdminRoute({ children }) {
    const { user } = useAuthContext()
    if (!user) return <Navigate to="/login" replace />
    if (user.role !== 'ADMIN') return <Navigate to="/dashboard" replace />
    return children
}

export default function AppRoutes() {
    return (
        <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />

            <Route path="/dashboard" element={
                <ProtectedRoute><DashboardPage /></ProtectedRoute>
            } />
            <Route path="/resources" element={
                <ProtectedRoute><ResourceListPage /></ProtectedRoute>
            } />
            <Route path="/book/:resourceId" element={
                <ProtectedRoute><BookingCalendarPage /></ProtectedRoute>
            } />
            <Route path="/admin" element={
                <AdminRoute><AdminDashboardPage /></AdminRoute>
            } />

            <Route path="/" element={<Navigate to="/dashboard" replace />} />
        </Routes>
    )
}