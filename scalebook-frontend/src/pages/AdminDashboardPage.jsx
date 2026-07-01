// src/pages/AdminDashboardPage.jsx

import { useQuery } from '@tanstack/react-query'
import axiosClient from '../api/axiosClient'
import Navbar from '../components/ui/Navbar'

export default function AdminDashboardPage() {
    const { data: allBookings, isLoading } = useQuery({
        queryKey: ['admin-bookings'],
        queryFn: async () => {
            const res = await axiosClient.get('/admin/bookings')
            return res.data
        }
    })

    const stats = {
        total: allBookings?.length ?? 0,
        confirmed: allBookings?.filter(b => b.status === 'CONFIRMED').length ?? 0,
        cancelled: allBookings?.filter(b => b.status === 'CANCELLED').length ?? 0,
    }

    return (
        <div className="min-h-screen bg-gray-50">
            <Navbar />

            <div className="max-w-6xl mx-auto px-6 py-8">
                <h1 className="text-2xl font-bold text-gray-900 mb-8">Admin Dashboard</h1>

                <div className="grid grid-cols-3 gap-4 mb-8">
                    {[
                        { label: 'Total Bookings', value: stats.total, color: 'blue' },
                        { label: 'Confirmed', value: stats.confirmed, color: 'green' },
                        { label: 'Cancelled', value: stats.cancelled, color: 'red' },
                    ].map(stat => (
                        <div key={stat.label} className="bg-white border border-gray-200 rounded-xl p-5">
                            <p className="text-sm text-gray-500">{stat.label}</p>
                            <p className={`text-3xl font-bold mt-1 text-${stat.color}-600`}>{stat.value}</p>
                        </div>
                    ))}
                </div>

                <div className="bg-white border border-gray-200 rounded-xl overflow-hidden">
                    <div className="px-5 py-4 border-b border-gray-100">
                        <h2 className="font-semibold text-gray-900">All Bookings</h2>
                    </div>

                    {isLoading ? (
                        <div className="p-5 text-sm text-gray-400">Loading...</div>
                    ) : (
                        <table className="w-full text-sm">
                            <thead className="bg-gray-50 text-gray-500 text-left">
                            <tr>
                                <th className="px-5 py-3 font-medium">User</th>
                                <th className="px-5 py-3 font-medium">Resource</th>
                                <th className="px-5 py-3 font-medium">Start</th>
                                <th className="px-5 py-3 font-medium">Status</th>
                            </tr>
                            </thead>
                            <tbody className="divide-y divide-gray-100">
                            {allBookings?.map(booking => (
                                <tr key={booking.id} className="hover:bg-gray-50 transition-colors">
                                    <td className="px-5 py-3">{booking.user?.email}</td>
                                    <td className="px-5 py-3">{booking.resource?.name}</td>
                                    <td className="px-5 py-3">{new Date(booking.startTime).toLocaleString()}</td>
                                    <td className="px-5 py-3">
                      <span className={`px-2 py-1 rounded-full text-xs font-medium
                        ${booking.status === 'CONFIRMED' ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-600'}`}>
                        {booking.status}
                      </span>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    )}
                </div>
            </div>
        </div>
    )
}