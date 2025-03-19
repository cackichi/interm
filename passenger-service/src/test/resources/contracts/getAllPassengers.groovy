package contracts

org.springframework.cloud.contract.spec.Contract.make {
    request {
        method 'GET'
        urlPath('/api/v1/passenger') {
            queryParameters {
                parameter 'page': '0'
                parameter 'size': '10'
            }
        }
    }
    response {
        status 200
        body([
                passengers: [
                        [
                                id: 100,
                                name: "John Doe",
                                email: "john@example.com",
                                phoneNumber: "+79999999999",
                                status: "NOT_ACTIVE"
                        ]
                ],
                totalElements: 1,
                totalPages: 1,
                size: 10,
                number: 0
        ])
        headers {
            contentType('application/json')
        }
    }
}