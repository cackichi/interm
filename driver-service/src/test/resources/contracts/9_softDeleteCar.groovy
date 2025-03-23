package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'DELETE'
        urlPath('/api/v1/drivers/driver-123/car/A123BC') {
            queryParameters {
                parameter 'driverId': 'driver-123'
                parameter 'number': 'A123BC'
            }
        }
    }
    response {
        status 204
    }
}