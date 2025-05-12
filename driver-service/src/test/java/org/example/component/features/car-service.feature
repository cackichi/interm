Feature: Car Service

  Scenario: Create a new car for a driver
    Given I have a driver with id "driver-123" and car details
    When I create the car for the driver
    Then the car should be created successfully

  Scenario: Update car details
    Given I have a driver with id "driver-123" and a car with number "ABC123"
    When I update the car details
    Then the car details should be updated

  Scenario: Find cars for a driver
    Given I have a driver with id "driver-123" and a list of cars
    When I find all cars for the driver
    Then I should get a paginated list of cars

  Scenario: Find a car by number
    Given I have a driver with id "driver-123" and a car with number "ABC123"
    When I find the car by number
    Then I should get the car details

  Scenario: Remove a car from a driver
      Given I have a driver with id "driver-123" and a car with number "ABC123"
      When I remove the car from the driver
      Then the car should be removed from the driver