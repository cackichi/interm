package contracts

org.springframework.cloud.contract.spec.Contract.make {
    request {
        method 'PATCH'
        url '/api/v1/passenger/100'
        body([
                name: "Jane Doe",
                email: "jane@example.com",
                phoneNumber: "+78888888888"
        ])
        headers {
            contentType('application/json')
        }
    }
    response {
        status 204
    }
}