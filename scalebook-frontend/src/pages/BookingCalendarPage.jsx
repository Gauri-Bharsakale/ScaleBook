// src/pages/BookingCalendarPage.jsx

import { useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { useQuery } from '@tanstack/react-query'
import { getResourceById } from '../api/resourceApi'
import { useCreateBooking } from '../hooks/useBookings'
import Navbar from '../components/ui/Navbar'
import Button from '../components/ui/Button'
import Input from '../components/ui/Input'

export default function BookingCalendarPage() {
    const { resourceId } = useParams()
    const navigate = useNavigate()

    const [form, setForm] = useState({ startTime: '', endTime: '' })
    const [validationError, setValidationError] = useState('')

    const { data: resource, isLoading } = useQuery({
        queryKey: ['resource', resourceId],
        queryFn: () => getResourceById(resourceId)
    })

    const { mutate: createBooking, isPending } = useCreateBooking()

    const handleChange = (e) => {
        setForm(prev => ({ ...prev, [e.target.name]: e.target.value }))
        setValidationError('')
    }

    const handleSubmit = (e) => {
        e.preventDefault()

        if (!form.startTime || !form.endTime) {
            setValidationError('Both start and end time are required')
            return
        }

        if (new Date(form.startTime) >= new Date(form.endTime)) {
            setValidationError('End time must be after start time')
            return
        }

        if (new Date(form.startTime) <= new Date()) {
            setValidationError('Start time must be in the future')
            return
        }

        createBooking(
            {
                resourceId: parseInt(resourceId),
                startTime: form.startTime,
                endTime: form.endTime
            },
            {
                onSuccess: () => navigate('/dashboard')
            }
        )
    }

    if (isLoading) {
        return (
            <div className="min-h-screen bg-gray-50">
                <Navbar />
                <div className="max-w-xl mx-auto px-6 py-8">
                    <div className="animate-pulse bg-white rounded-2xl h-64 border border-gray-200" />
                </div>
            </div>
        )
    }

    return (
        <div className="min-h-screen bg-gray-50">
            <Navbar />

            <div className="max-w-xl mx-auto px-6 py-8">
                <div className="bg-white border border-gray-200 rounded-2xl p-6">

                    <div className="mb-6">
                        <h1 className="text-xl font-bold text-gray-900">Book: {resource?.name}</h1>
                        <p className="text-sm text-gray-500 mt-1">{resource?.description}</p>
                        {resource?.capacity && (
                            <span className="inline-block mt-2 text-xs bg-blue-50 text-blue-700 px-2 py-1 rounded-full">
                Capacity: {resource.capacity}
              </span>
                        )}
                    </div>

                    <form onSubmit={handleSubmit} className="flex flex-col gap-4">
                        <Input
                            label="Start Date & Time"
                            name="startTime"
                            type="datetime-local"
                            value={form.startTime}
                            onChange={handleChange}
                        />
                        <Input
                            label="End Date & Time"
                            name="endTime"
                            type="datetime-local"
                            value={form.endTime}
                            onChange={handleChange}
                        />

                        {validationError && (
                            <p className="text-sm text-red-600 bg-red-50 px-3 py-2 rounded-lg">
                                {validationError}
                            </p>
                        )}

                        <div className="flex gap-3 mt-2">
                            <Button
                                type="button"
                                variant="secondary"
                                onClick={() => navigate('/resources')}
                                className="flex-1"
                            >
                                Cancel
                            </Button>
                            <Button type="submit" disabled={isPending} className="flex-1">
                                {isPending ? 'Booking...' : 'Confirm Booking'}
                            </Button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    )
}