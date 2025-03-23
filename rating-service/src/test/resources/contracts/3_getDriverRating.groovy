package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'GET'
        urlPath('/api/v1/drivers/rating/driver-123')
    }
    response {
        status 200
        headers {
            contentType('application/json')
        }
        body([
                message: "Рейтинг водителя с id:driver-123 = 4.2"
        ])
    }
}