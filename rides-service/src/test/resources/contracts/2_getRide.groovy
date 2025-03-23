package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'GET'
        urlPath('/api/v1/rides/100')
    }
    response {
        status 200
        headers {
            contentType('application/json')
        }
        body([
                id: 100,
                passengerId: 100,
                driverId: "driver-123",
                pointA: "Point A",
                pointB: "Point B",
                status: "WAITING",
                deleted: false
        ])
    }
}