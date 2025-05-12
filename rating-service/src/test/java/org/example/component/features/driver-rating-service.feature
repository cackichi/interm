Feature: Driver Rating Service

  Scenario: Create a new driver rating
    Given I have driver rating details
    When I create the driver rating
    Then the driver rating should be created successfully

  Scenario: Update or save a driver rating
    Given I have a driver rating with id "driver-123"
    When I update or save the driver rating with rating 4.5
    Then the driver rating should be updated or saved successfully

  Scenario: Find a driver rating
    Given I have a driver rating with id "driver-123" and rating 4.5
    When I find the driver rating
    Then I should get the driver rating details

  Scenario: Find all not deleted driver ratings
    Given I have a list of not deleted driver ratings
    When I find all not deleted driver ratings
    Then I should get a list of not deleted driver ratings

  Scenario: Soft delete a driver rating
    Given I have a driver rating with id "driver-123"
    When I soft delete the driver rating
    Then the driver rating should be soft deleted

  Scenario: Hard delete a driver rating
    Given I have a driver rating with id "driver-123"
    When I hard delete the driver rating
    Then the driver rating should be hard deleted