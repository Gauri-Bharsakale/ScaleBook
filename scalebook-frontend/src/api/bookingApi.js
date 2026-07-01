// src/api/bookingApi.js

import axiosClient from './axiosClient'

export const createBooking = async (bookingData) => {
    const response = await axiosClient.post('/bookings', bookingData)
    return response.data
}

export const getMyBookings = async () => {
    const response = await axiosClient.get('/bookings/my')
    return response.data
}

export const cancelBooking = async (id) => {
    const response = await axiosClient.patch(`/bookings/${id}/cancel`)
    return response.data
}