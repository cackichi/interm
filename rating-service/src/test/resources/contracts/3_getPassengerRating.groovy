package contracts

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'GET'
        urlPath('/api/v1/passenger/rating/150')
    }
    response {
        status 200
        headers {
            contentType('application/json')
        }
        body([
                message: "Рейтинг пассажира с id:150 = 4.2"
        ])
    }
}