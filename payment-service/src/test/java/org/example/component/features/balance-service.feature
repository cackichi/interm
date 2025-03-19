Feature: Balance Service

  Scenario: Create a new balance
    Given I have balance details
    When I create the balance
    Then the balance should be created successfully

  Scenario: Top up the balance
    Given I have a balance with passenger id 1
    When I top up the balance with 100.0
    Then the balance should be updated with 100.0

  Scenario: Get the balance
    Given I have a balance with passenger id 1
    When I get the balance
    Then I should get the balance details

  Scenario: Soft delete the balance
    Given I have a balance with passenger id 1
    When I soft delete the balance
    Then the balance should be soft deleted

  Scenario: Hard delete the balance
    Given I have a balance with passenger id 1
    When I hard delete the balance
    Then the balance should be hard deleted