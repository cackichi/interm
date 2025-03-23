package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'PATCH'
        urlPath('/api/v1/balance/top-up/1') {
            queryParameters {
                parameter 'deposit': '50.0'
            }
        }
    }
    response {
        status 204
    }
}