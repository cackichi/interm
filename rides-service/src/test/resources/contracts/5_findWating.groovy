package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'PATCH'
        urlPath('/api/v1/rides/start/driver-123')
    }
    response {
        status 200
        headers {
            contentType('application/json')
        }
        body([
                message: "Поездка найдена, запрос на проверку id водителя отправлен"
        ])
    }
}