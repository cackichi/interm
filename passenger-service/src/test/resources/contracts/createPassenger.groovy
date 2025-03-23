package contracts

org.springframework.cloud.contract.spec.Contract.make {
    request {
        method 'POST'
        url '/api/v1/passenger'
        body([
                name: "John Doe",
                email: "john@example.com",
                phoneNumber: "+79999999999",
                status: "NOT_ACTIVE"
        ])
        headers {
            contentType('application/json')
        }
    }
    response {
        status 201
        body([
                id: 100,
                name: "John Doe",
                email: "john@example.com",
                phoneNumber: "+79999999999",
                status: "NOT_ACTIVE"
        ])
        headers {
            contentType('application/json')
        }
    }
}