import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter, Rate, Trend } from 'k6/metrics';

// ---------------- Metrics ----------------

const bookingSuccesses = new Counter('booking_successes');
const bookingConflicts = new Counter('booking_conflicts');
const bookingErrors = new Counter('booking_errors');
const bookingDuration = new Trend('booking_duration_ms');
const errorRate = new Rate('error_rate');

let token = "";


// ---------------- Options ----------------

export const options = {
    scenarios: {

        ramp_up_test: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '30s', target: 20 },
                { duration: '1m', target: 20 },
                { duration: '30s', target: 0 },
            ],
            exec: 'bookingTest',
        },

        spike_test: {
            executor: 'constant-vus',
            vus: 50,
            duration: '30s',
            startTime: '2m30s',
            exec: 'concurrentSameSlotTest',
        },
    },

    thresholds: {
        http_req_duration: ['p(95)<500'],
        error_rate: ['rate<0.1'],
    },
};

export function setup() {

    const res = http.post(
        "http://localhost:8080/api/auth/login",
        JSON.stringify({
            email: "gauri@gmail.com",
            password: "gauri123"
        }),
        {
            headers: {
                "Content-Type": "application/json"
            }
        }
    );

    if (res.status !== 200) {
        throw new Error("Login failed");
    }

    token = JSON.parse(res.body).token;

    return {
        token: token
    };
}

// ---------------------------------------------------
// Scenario 1
// ---------------------------------------------------

export function bookingTest(data) {

    const jwt = data.token;
    if (!jwt) return;

    const hoursOffset = Math.floor(Math.random() * 48) + 2;

    const start = new Date(Date.now() + hoursOffset * 3600000).toISOString();

    const end = new Date(Date.now() + (hoursOffset + 1) * 3600000).toISOString();

    const payload = JSON.stringify({
        resourceId: 5,
        startTime: start,
        endTime: end
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${jwt}`
        }
    };

    const begin = Date.now();

    const res = http.post(
        'http://localhost:8080/api/bookings',
        payload,
        params
    );

    bookingDuration.add(Date.now() - begin);

    const success = res.status === 200;
    const conflict = res.status === 409;

    check(res, {
        'booking status is 200 or 409': r => r.status === 200 || r.status === 409,
        'response has body': r => r.body.length > 0,
    });

    if (res.status !== 200 && res.status !== 409) {
        console.log("ERROR STATUS:", res.status);
        console.log("ERROR BODY:", res.body);
    }

    if (success)
        bookingSuccesses.add(1);

    if (conflict)
        bookingConflicts.add(1);

    if (!success && !conflict) {
        bookingErrors.add(1);
        errorRate.add(true);
    } else {
        errorRate.add(false);
    }

    sleep(1);
}

// ---------------------------------------------------
// Scenario 2
// ---------------------------------------------------

export function concurrentSameSlotTest(data) {

    const jwt = data.token;
    if (!jwt) return;

    // Tomorrow at 10 AM
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    tomorrow.setHours(10, 0, 0, 0);

    const endDate = new Date(tomorrow.getTime() + 60 * 60 * 1000);

    const payload = JSON.stringify({
        resourceId: 5,
        startTime: tomorrow.toISOString(),
        endTime: endDate.toISOString()
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${jwt}`
        }
    };

    const res = http.post(
        'http://localhost:8080/api/bookings',
        payload,
        params
    );
    check(res, {
        'only 200 or 409 allowed': r =>
            r.status === 200 || r.status === 409,
    });

    if (res.status !== 200 && res.status !== 409) {
        console.log("ERROR STATUS:", res.status);
        console.log("ERROR BODY:", res.body);
    }

    if (res.status === 200)
        bookingSuccesses.add(1);

    if (res.status === 409)
        bookingConflicts.add(1);

    if (res.status !== 200 && res.status !== 409) {
        bookingErrors.add(1);
        errorRate.add(true);
    } else {
        errorRate.add(false);
    }
}




