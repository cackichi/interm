Feature: Driver Service

  Scenario: Create a new driver
    Given I have driver details
    When I create the driver
    Then the driver should be created successfully

  Scenario: Update driver data
    Given I have a driver with id "driver-123"
    When I update the driver data
    Then the driver data should be updated

  Scenario: Find all not deleted drivers
    Given I have a list of not deleted drivers
    When I find all not deleted drivers
    Then I should get a paginated list of drivers

  Scenario: Find a driver by id
    Given I have a driver with id "driver-123"
    When I find the driver by id
    Then I should get the driver details

  Scenario: Update driver status for travel
    Given I have a driver with id "driver-123" and status "FREE"
    When I update the driver status to "BUSY"
    Then the driver status should be updated to "BUSY"

  Scenario: Soft delete a driver
      Given I have a driver with id "driver-123"
      When I soft delete the driver
      Then the driver should be soft deleted

  Scenario: Hard delete a driver
      Given I have a driver with id "driver-123"
      When I hard delete the driver
      Then the driver should be hard deleted