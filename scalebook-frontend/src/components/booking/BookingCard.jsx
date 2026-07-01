// src/components/booking/BookingCard.jsx

import Button from '../ui/Button'
import { useCancelBooking } from '../../hooks/useBookings'

export default function BookingCard({ booking }) {
    const { mutate: cancelBooking, isPending } = useCancelBooking()

    const statusColors = {
        CONFIRMED: 'bg-green-100 text-green-700',
        CANCELLED: 'bg-red-100 text-red-700',
        COMPLETED: 'bg-gray-100 text-gray-600'
    }

    const formatDate = (dateString) => {
        return new Date(dateString).toLocaleString('en-IN', {
            dateStyle: 'medium',
            timeStyle: 'short'
        })
    }

    return (
        <div className="bg-white border border-gray-200 rounded-xl p-5">
            <div className="flex items-start justify-between mb-3">
                <div>
                    <h3 className="font-semibold text-gray-900">{booking.resource?.name}</h3>
                    <p className="text-sm text-gray-500 mt-1">
                        {formatDate(booking.startTime)} → {formatDate(booking.endTime)}
                    </p>
                </div>
                <span className={`text-xs font-medium px-2 py-1 rounded-full ${statusColors[booking.status]}`}>
          {booking.status}
        </span>
            </div>

            {booking.status === 'CONFIRMED' && (
                <Button
                    variant="danger"
                    onClick={() => cancelBooking(booking.id)}
                    disabled={isPending}
                    className="text-sm"
                >
                    {isPending ? 'Cancelling...' : 'Cancel Booking'}
                </Button>
            )}
        </div>
    )
}