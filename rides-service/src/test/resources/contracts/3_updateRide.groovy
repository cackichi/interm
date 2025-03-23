package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'PATCH'
        urlPath('/api/v1/rides/100')
        headers {
            contentType('application/json')
        }
        body([
                pointA: "New Point A",
                pointB: "New Point B"
        ])
    }
    response {
        status 204
    }
}