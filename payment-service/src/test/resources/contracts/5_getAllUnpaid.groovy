package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'GET'
        urlPath('/api/v1/payment/unpaid/1') {
            queryParameters {
                parameter 'page': '0'
                parameter 'size': '10'
            }
        }
    }
    response {
        status 200
        headers {
            contentType('application/json')
        }
        body([
                payments: [
                        [
                                id: 100,
                                passengerId: 1,
                                rideId: 100,
                                cost: 50.0,
                                status: "WAITING",
                                deleted: false
                        ]
                ],
                totalElements: 1,
                totalPages: 1,
                size: 10,
                number: 0
        ])
    }
}