// src/hooks/useBookings.js

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { getMyBookings, createBooking, cancelBooking } from '../api/bookingApi'
import toast from 'react-hot-toast'

export function useMyBookings() {
    const { data, isLoading, error } = useQuery({
        queryKey: ['my-bookings'],
        queryFn: getMyBookings,
    })

    return { bookings: data ?? [], isLoading, error }
}

export function useCreateBooking() {
    const queryClient = useQueryClient()

    return useMutation({
        mutationFn: createBooking,
        onSuccess: () => {
            // After a successful booking, automatically refetch the bookings list
            queryClient.invalidateQueries({ queryKey: ['my-bookings'] })
            toast.success('Booking confirmed!')
        },
        onError: (error) => {
            const message = error.response?.data?.message ?? 'Booking failed, please try again'
            toast.error(message)
        }
    })
}

export function useCancelBooking() {
    const queryClient = useQueryClient()

    return useMutation({
        mutationFn: cancelBooking,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['my-bookings'] })
            toast.success('Booking cancelled')
        },
        onError: () => {
            toast.error('Failed to cancel booking')
        }
    })
}