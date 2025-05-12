package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'DELETE'
        urlPath('/api/v1/rating/drivers/driver-123')
    }
    response {
        status 204
    }
}