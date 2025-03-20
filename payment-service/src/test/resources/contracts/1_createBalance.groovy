package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'POST'
        url '/api/v1/balance'
        headers {
            contentType('application/json')
        }
        body([
                passengerId: 1,
                balance: 100.0,
                timeLastDeposit: "2023-10-01T12:00:00",
                deleted: false
        ])
    }
    response {
        status 201
    }
}