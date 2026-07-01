// src/hooks/useResources.js

import { useQuery } from '@tanstack/react-query'
import { getAllResources } from '../api/resourceApi'

export function useResources() {
    const { data, isLoading, error } = useQuery({
        queryKey: ['resources'],       // unique cache key — TanStack uses this to cache + deduplicate requests
        queryFn: getAllResources,
        staleTime: 1000 * 60 * 5,     // consider cache "fresh" for 5 minutes (matches our Redis TTL idea)
    })

    return {
        resources: data ?? [],
        isLoading,
        error
    }
}