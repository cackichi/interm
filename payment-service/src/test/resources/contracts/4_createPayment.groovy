package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'POST'
        url '/api/v1/payment'
        headers {
            contentType('application/json')
        }
        body([
                passengerId: 1,
                rideId: 100,
                cost: 50.0,
                status: "WAITING",
                deleted: false
        ])
    }
    response {
        status 201
    }
}