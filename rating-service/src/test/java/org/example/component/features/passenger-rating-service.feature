Feature: Passenger Rating Service

  Scenario: Create a new passenger rating
    Given I have passenger rating details
    When I create the passenger rating
    Then the passenger rating should be created successfully

  Scenario: Update or save a passenger rating
    Given I have a passenger rating with id 1
    When I update or save the passenger rating with rating 4.5
    Then the passenger rating should be updated or saved successfully

  Scenario: Find a passenger rating
    Given I have a passenger rating with id 1 and rating 4.5
    When I find the passenger rating
    Then I should get the passenger rating details

  Scenario: Find all not deleted passenger ratings
    Given I have a list of not deleted passenger ratings
    When I find all not deleted passenger ratings
    Then I should get a list of not deleted passenger ratings

  Scenario: Soft delete a passenger rating
    Given I have a passenger rating with id 1
    When I soft delete the passenger rating
    Then the passenger rating should be soft deleted

  Scenario: Hard delete a passenger rating
    Given I have a passenger rating with id 1
    When I hard delete the passenger rating
    Then the passenger rating should be hard deleted