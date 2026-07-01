// src/components/ui/Navbar.jsx

import { Link, useNavigate } from 'react-router-dom'
import { useAuthContext } from '../../context/AuthContext'
import Button from './Button'

export default function Navbar() {
    const { user, logout } = useAuthContext()
    const navigate = useNavigate()

    const handleLogout = () => {
        logout()
        navigate('/login')
    }

    return (
        <nav className="bg-white border-b border-gray-200 px-6 py-4">
            <div className="max-w-6xl mx-auto flex items-center justify-between">
                <Link to="/dashboard" className="text-xl font-bold text-blue-600">
                    ScaleBook
                </Link>

                <div className="flex items-center gap-6">
                    <Link to="/resources" className="text-sm text-gray-600 hover:text-blue-600 transition-colors">
                        Resources
                    </Link>
                    <Link to="/dashboard" className="text-sm text-gray-600 hover:text-blue-600 transition-colors">
                        My Bookings
                    </Link>
                    {user?.role === 'ADMIN' && (
                        <Link to="/admin" className="text-sm text-gray-600 hover:text-blue-600 transition-colors">
                            Admin
                        </Link>
                    )}
                    <div className="flex items-center gap-3">
                        <span className="text-sm text-gray-500">{user?.email}</span>
                        <Button variant="secondary" onClick={handleLogout}>
                            Logout
                        </Button>
                    </div>
                </div>
            </div>
        </nav>
    )
}