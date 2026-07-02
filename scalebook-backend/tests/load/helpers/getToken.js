// tests/load/helpers/getToken.js

import http from 'k6/http'

export function getToken() {
    const res = http.post('http://localhost:8080/api/auth/login',
        JSON.stringify({ email: 'test@example.com', password: 'password123' }),
        { headers: { 'Content-Type': 'application/json' } }
    )
    return JSON.parse(res.body).token
}