package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'PATCH'
        urlPath('/api/v1/passenger/rating/150') {
            queryParameters {
                parameter 'rating': '1.2'
            }
        }
    }
    response {
        status 204
    }
}