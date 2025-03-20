package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'PATCH'
        urlPath('/api/v1/rides/stop/driver-123') {
            queryParameters {
                parameter 'rating': '4.5'
                parameter 'cost': '100.0'
            }
        }
    }
    response {
        status 404
        headers {
            contentType('application/json')
        }
        body([
                message: "У водителя нет действующей поездок"
        ])
    }
}