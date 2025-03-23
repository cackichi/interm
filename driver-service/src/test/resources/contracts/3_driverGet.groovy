package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'GET'
        urlPath('/api/v1/drivers/driver-123')
    }
    response {
        status 200
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
                cars: [
                        number: "A123BC",
                        brand: "Toyota",
                        color: "Black",
                        deleted: false
                ]
        ])
    }
}