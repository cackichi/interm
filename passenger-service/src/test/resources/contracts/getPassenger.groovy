package contracts

org.springframework.cloud.contract.spec.Contract.make {
    description("""
        Получение информации о пассажире по ID.
    """)

    request {
        method GET()
        url("/api/v1/passenger/100")
        headers {
            contentType(applicationJson())
        }
    }

    response {
        status OK()
        headers {
            contentType(applicationJson())
        }
        body([
                id: 100,
                name: "John Doe",
                email: "john@example.com",
                phoneNumber: "+79999999999",
                deleted: false,
                status: "NOT_ACTIVE"
        ])
    }
}