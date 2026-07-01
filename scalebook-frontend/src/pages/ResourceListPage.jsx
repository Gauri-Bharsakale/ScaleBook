// src/pages/ResourceListPage.jsx

import Navbar from '../components/ui/Navbar'
import ResourceCard from '../components/booking/ResourceCard'
import { useResources } from '../hooks/useResources'

export default function ResourceListPage() {
    const { resources, isLoading, error } = useResources()

    return (
        <div className="min-h-screen bg-gray-50">
            <Navbar />

            <div className="max-w-6xl mx-auto px-6 py-8">
                <div className="mb-6">
                    <h1 className="text-2xl font-bold text-gray-900">Available Resources</h1>
                    <p className="text-sm text-gray-500 mt-1">Select a resource to make a booking</p>
                </div>

                {isLoading && (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                        {[...Array(6)].map((_, i) => (
                            <div key={i} className="bg-white border border-gray-200 rounded-xl p-5 animate-pulse">
                                <div className="h-4 bg-gray-200 rounded w-3/4 mb-3" />
                                <div className="h-3 bg-gray-200 rounded w-full mb-2" />
                                <div className="h-3 bg-gray-200 rounded w-2/3" />
                            </div>
                        ))}
                    </div>
                )}

                {error && (
                    <div className="bg-red-50 border border-red-200 rounded-xl p-4 text-red-700 text-sm">
                        Failed to load resources. Please try again.
                    </div>
                )}

                {!isLoading && !error && (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                        {resources.map(resource => (
                            <ResourceCard key={resource.id} resource={resource} />
                        ))}
                    </div>
                )}

                {!isLoading && !error && resources.length === 0 && (
                    <div className="text-center py-16 text-gray-400">
                        No resources available right now.
                    </div>
                )}
            </div>
        </div>
    )
}