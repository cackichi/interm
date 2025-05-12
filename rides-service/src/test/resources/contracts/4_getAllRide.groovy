package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'GET'
        urlPath('/api/v1/rides') {
            queryParameters {
                parameter 'page': '0'
                parameter 'size': '10'
                parameter 'total': '6'
            }
        }
    }
    response {
        status 200
        headers {
            contentType('application/json')
        }
        body([
                rides: [
                        [
                                id: 100,
                                passengerId: 100,
                                driverId: "driver-123",
                                pointA: "New Point A",
                                pointB: "New Point B",
                                status: "WAITING",
                                deleted: false
                        ]
                ],
                totalElem: 1,
                totalPages: 1,
                size: 10,
                number: 0
        ])
    }
}