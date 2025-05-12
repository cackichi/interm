package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'DELETE'
        urlPath('/api/v1/rating/passenger/150')
    }
    response {
        status 204
    }
}