Feature: Ride Service

  Scenario: Create a new ride
    Given I have ride details
    When I create the ride
    Then the ride should be created successfully

  Scenario: Update ride details
    Given I have a ride with id 100
    When I update the ride details
    Then the ride details should be updated

  Scenario: Find all not deleted rides
    Given I have a list of not deleted rides
    When I find all not deleted rides
    Then I should get a paginated list of rides

  Scenario: Find a ride by id
    Given I have a ride with id 100
    When I find the ride by id
    Then I should get the ride details

  Scenario: Attach a driver to a ride
    Given I have a ride with id 100
    When I attach driver "driver-123" to the ride
    Then the driver should be attached to the ride

  Scenario: Update ride status
      Given I have a ride with id 100
      When I update the ride status to "TRAVELING"
      Then the ride status should be updated to "TRAVELING"

  Scenario: Soft delete a ride
      Given I have a ride with id 100
      When I soft delete the ride
      Then the ride should be soft deleted

  Scenario: Hard delete a ride
      Given I have a ride with id 100
      When I hard delete the ride
      Then the ride should be hard deleted