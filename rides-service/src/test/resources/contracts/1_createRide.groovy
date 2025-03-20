package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'POST'
        url '/api/v1/rides'
        headers {
            contentType('application/json')
        }
        body([
                id: 1,
                passengerId: 100,
                driverId: "driver-123",
                pointA: "Point A",
                pointB: "Point B",
                status: "TRAVELING",
                deleted: false
        ])
    }
    response {
        status 201
    }
}