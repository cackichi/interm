package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'GET'
        urlPath('/api/v1/drivers') {
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
                drivers: [
                        [
                                id: "driver-123",
                                name: "Иван Петров",
                                experience: 6,
                                phone: "+79998887766",
                                email: "ivan.petrov@example.com",
                                deleted: false,
                                status: "AVAILABLE",
                                cars: [
                                        number: "A123BC",
                                        brand: "Toyota",
                                        color: "White",
                                        deleted: false
                                ]
                        ]
                ],
                totalElements: 2,
                totalPages: 1,
                size: 10,
                number: 0
        ])
    }
}