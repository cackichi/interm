package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'PATCH'
        urlPath('/api/v1/drivers/driver-123')
        headers {
            contentType('application/json')
        }
        body([
                name: "Иван Петров",
                experience: 6,
                phone: "+79998887766",
                email: "ivan.petrov@example.com",
                deleted: false,
                status: "AVAILABLE",
                cars: []
        ])
    }
    response {
        status 200
    }
}