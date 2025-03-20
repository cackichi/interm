package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'GET'
        urlPath('/api/v1/drivers/driver-123/car/A123BC')
    }
    response {
        status 200
        headers {
            contentType('application/json')
        }
        body([
                number: "A123BC",
                brand: "Toyota",
                color: "Black",
                deleted: false
        ])
    }
}