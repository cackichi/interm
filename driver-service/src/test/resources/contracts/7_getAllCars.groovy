package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'GET'
        urlPath('/api/v1/drivers/driver-123/cars') {
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
                cars: [
                        [
                                number: "A123BC",
                                brand: "Toyota",
                                color: "White",
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