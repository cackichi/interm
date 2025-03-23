package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'POST'
        url '/api/v1/drivers'
        headers {
            contentType('application/json')
        }
        body([
                id: "driver-123",
                name: "Иван Иванов",
                experience: 5,
                phone: "+79999999999",
                email: "ivan@example.com",
                deleted: false,
                status: "FREE",
                cars: []
        ])
    }
    response {
        status 201
    }
}