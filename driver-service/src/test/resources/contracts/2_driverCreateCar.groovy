package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'POST'
        urlPath('/api/v1/drivers/driver-123/car')
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
    response {
        status 201
    }
}