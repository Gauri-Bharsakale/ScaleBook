// src/pages/DashboardPage.jsx

import Navbar from '../components/ui/Navbar'
import BookingCard from '../components/booking/BookingCard'
import { useMyBookings } from '../hooks/useBookings'
import { useAuthContext } from '../context/AuthContext'
import { Link } from 'react-router-dom'
import Button from '../components/ui/Button'

export default function DashboardPage() {
    const { user } = useAuthContext()
    const { bookings, isLoading, error } = useMyBookings()

    const activeBookings = bookings.filter(b => b.status === 'CONFIRMED')
    const pastBookings = bookings.filter(b => b.status !== 'CONFIRMED')

    return (
        <div className="min-h-screen bg-gray-50">
            <Navbar />

            <div className="max-w-4xl mx-auto px-6 py-8">
                <div className="flex items-center justify-between mb-8">
                    <div>
                        <h1 className="text-2xl font-bold text-gray-900">My Dashboard</h1>
                        <p className="text-sm text-gray-500 mt-1">Welcome back, {user?.email}</p>
                    </div>
                    <Link to="/resources">
                        <Button>New Booking</Button>
                    </Link>
                </div>

                {isLoading && (
                    <div className="space-y-3">
                        {[...Array(3)].map((_, i) => (
                            <div key={i} className="bg-white border border-gray-200 rounded-xl p-5 animate-pulse">
                                <div className="h-4 bg-gray-200 rounded w-1/3 mb-2" />
                                <div className="h-3 bg-gray-200 rounded w-1/2" />
                            </div>
                        ))}
                    </div>
                )}

                {error && (
                    <div className="bg-red-50 border border-red-200 rounded-xl p-4 text-red-700 text-sm">
                        Failed to load bookings.
                    </div>
                )}

                {!isLoading && !error && (
                    <div className="space-y-8">
                        <section>
                            <h2 className="text-base font-semibold text-gray-700 mb-3">
                                Active Bookings ({activeBookings.length})
                            </h2>
                            {activeBookings.length === 0 ? (
                                <div className="text-center py-12 bg-white border border-dashed border-gray-300 rounded-xl text-gray-400 text-sm">
                                    No active bookings. <Link to="/resources" className="text-blue-600">Book a resource →</Link>
                                </div>
                            ) : (
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                    {activeBookings.map(booking => (
                                        <BookingCard key={booking.id} booking={booking} />
                                    ))}
                                </div>
                            )}
                        </section>

                        {pastBookings.length > 0 && (
                            <section>
                                <h2 className="text-base font-semibold text-gray-700 mb-3">
                                    Past Bookings
                                </h2>
                                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                                    {pastBookings.map(booking => (
                                        <BookingCard key={booking.id} booking={booking} />
                                    ))}
                                </div>
                            </section>
                        )}
                    </div>
                )}
            </div>
        </div>
    )
}