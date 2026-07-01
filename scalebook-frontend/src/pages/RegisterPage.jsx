// src/pages/RegisterPage.jsx

import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useMutation } from '@tanstack/react-query'
import toast from 'react-hot-toast'
import { registerUser } from '../api/authApi'
import { useAuthContext } from '../context/AuthContext'
import Input from '../components/ui/Input'
import Button from '../components/ui/Button'

export default function RegisterPage() {
    const navigate = useNavigate()
    const { login } = useAuthContext()

    const [form, setForm] = useState({ fullName: '', email: '', password: '' })
    const [errors, setErrors] = useState({})

    const { mutate, isPending } = useMutation({
        mutationFn: registerUser,
        onSuccess: (data) => {
            login({ email: data.email, role: data.role }, data.token)
            toast.success('Account created!')
            navigate('/dashboard')
        },
        onError: (error) => {
            toast.error(error.response?.data?.message ?? 'Registration failed')
        }
    })

    const validate = () => {
        const newErrors = {}
        if (!form.fullName.trim()) newErrors.fullName = 'Name is required'
        if (!form.email.includes('@')) newErrors.email = 'Valid email required'
        if (form.password.length < 8) newErrors.password = 'Minimum 8 characters'
        return newErrors
    }

    const handleChange = (e) => {
        setForm(prev => ({ ...prev, [e.target.name]: e.target.value }))
        if (errors[e.target.name]) {
            setErrors(prev => ({ ...prev, [e.target.name]: '' }))
        }
    }

    const handleSubmit = (e) => {
        e.preventDefault()
        const validationErrors = validate()
        if (Object.keys(validationErrors).length > 0) {
            setErrors(validationErrors)
            return
        }
        mutate(form)
    }

    return (
        <div className="min-h-screen bg-gray-50 flex items-center justify-center px-4">
            <div className="bg-white w-full max-w-md rounded-2xl shadow-sm border border-gray-200 p-8">

                <div className="mb-8 text-center">
                    <h1 className="text-2xl font-bold text-gray-900">Create an account</h1>
                    <p className="text-sm text-gray-500 mt-1">Start booking resources today</p>
                </div>

                <form onSubmit={handleSubmit} className="flex flex-col gap-4">
                    <Input label="Full Name" name="fullName" value={form.fullName}
                           onChange={handleChange} error={errors.fullName} placeholder="Jane Doe" />
                    <Input label="Email" name="email" type="email" value={form.email}
                           onChange={handleChange} error={errors.email} placeholder="you@example.com" />
                    <Input label="Password" name="password" type="password" value={form.password}
                           onChange={handleChange} error={errors.password} placeholder="Min 8 characters" />

                    <Button type="submit" disabled={isPending} className="w-full mt-2">
                        {isPending ? 'Creating account...' : 'Create account'}
                    </Button>
                </form>

                <p className="text-center text-sm text-gray-500 mt-6">
                    Already have an account?{' '}
                    <Link to="/login" className="text-blue-600 font-medium hover:underline">
                        Sign in
                    </Link>
                </p>
            </div>
        </div>
    )
}