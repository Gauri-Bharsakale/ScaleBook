// src/components/booking/ResourceCard.jsx

import { Link } from 'react-router-dom'
import Button from '../ui/Button'

export default function ResourceCard({ resource }) {
    return (
        <div className="bg-white border border-gray-200 rounded-xl p-5 flex flex-col gap-3 hover:shadow-md transition-shadow">
            <div className="flex items-start justify-between">
                <div>
                    <h3 className="font-semibold text-gray-900">{resource.name}</h3>
                    <p className="text-sm text-gray-500 mt-1">{resource.description}</p>
                </div>
                <span className="bg-green-100 text-green-700 text-xs font-medium px-2 py-1 rounded-full">
          Available
        </span>
            </div>

            {resource.capacity && (
                <p className="text-sm text-gray-500">
                    Capacity: <span className="font-medium text-gray-700">{resource.capacity}</span>
                </p>
            )}

            <Link to={`/book/${resource.id}`}>
                <Button className="w-full">
                    Book Now
                </Button>
            </Link>
        </div>
    )
}