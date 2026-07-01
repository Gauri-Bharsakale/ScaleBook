// src/api/resourceApi.js

import axiosClient from './axiosClient'

export const getAllResources = async () => {
    const response = await axiosClient.get('/resources')
    return response.data
}

export const getResourceById = async (id) => {
    const response = await axiosClient.get(`/resources/${id}`)
    return response.data
}