Feature: Passenger API

  Scenario: Create a new passenger
    Given that I have passenger details
    When I send a POST request to endpoint "/api/v1/passenger"
    Then the response status code should be 201
    And the response body should contain the new passenger

  Scenario: Update a passenger
    Given that I have passenger details
    When I send a PATCH request to endpoint "/api/v1/passenger" with id 100
    Then the response status code should be 204

  Scenario: Get all passengers with pagination
    When I send a GET request to endpoint "/api/v1/passenger" with page 0 and size 10
    Then the response status code should be 200
    And the response body should contain a list of passengers

  Scenario: Get a passenger by id
    When I send a GET request to endpoint "/api/v1/passenger" with id 100
    Then the response status code should be 200
    And the response body should contain the passenger with id 100

  Scenario: Order a taxi
    When I send a PATCH request to endpoint "/api/v1/passenger/order-taxi" with passenger id 100, pointA "Point A" and pointB "Point B"
    Then the response status code should be 200
    And the response body should contain a message "Запрос на создание заявки на поездку отправлен, ожидайте пока водитель примет ее"

  Scenario: Soft delete a passenger
    Given that I have passenger details
    When I send a DELETE request to endpoint "/api/v1/passenger" with id 100
    Then the response status code should be 204