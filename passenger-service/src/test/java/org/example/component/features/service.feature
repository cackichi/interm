Feature: Passenger Service

  Scenario: Save a new passenger
    Given I have passenger details
    When I save the passenger
    Then the passenger should be saved successfully

  Scenario: Find all not deleted passengers
      Given I have a list of not deleted passengers
      When I find all not deleted passengers
      Then I should get a paginated list of passengers

  Scenario: Find a passenger by id
      Given I have a passenger with id 100
      When I find the passenger by id
      Then I should get the passenger details

  Scenario: Soft delete a passenger
    Given I have a passenger with id 100
    When I soft delete the passenger
    Then the passenger should be soft deleted

  Scenario: Update passenger data
    Given I have a passenger with id 100
    When I update the passenger data
    Then the passenger data should be updated

  Scenario: Hard delete a passenger
    Given I have a passenger with id 100
    When I hard delete the passenger
    Then the passenger should be hard deleted