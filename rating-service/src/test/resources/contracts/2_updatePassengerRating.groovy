package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'PATCH'
        urlPath('/api/v1/rating/passenger/150') {
            queryParameters {
                parameter 'rating': '1.2'
            }
        }
    }
    response {
        status 204
    }
}