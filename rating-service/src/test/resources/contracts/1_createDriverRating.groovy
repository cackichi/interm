package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'POST'
        url '/api/v1/drivers/rating'
        headers {
            contentType('application/json')
        }
        body([
                driverId: "driver-123",
                averageRating: 4.5,
                ratingCount: 10,
                deleted: false
        ])
    }
    response {
        status 201
    }
}