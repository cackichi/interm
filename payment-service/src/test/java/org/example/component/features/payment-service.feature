Feature: Payment Service

  Scenario: Create a new payment
    Given I have payment details
    When I create the payment
    Then the payment should be created successfully

  Scenario: Close a payment
    Given I have a payment with passenger id 1 and cost 50.0
    And I have a balance with passenger id 1 and balance 100.0
    When I close the payment
    Then the payment should be closed successfully

  Scenario: Get unpaid payments
    Given I have a list of unpaid payments for passenger id 1
    When I get unpaid payments for passenger id 1
    Then I should get a paginated list of unpaid payments

  Scenario: Get paid payments
    Given I have a list of paid payments for passenger id 1
    When I get paid payments for passenger id 1
    Then I should get a paginated list of paid payments

  Scenario: Soft delete payments
    Given I have a payment with passenger id 1
    When I soft delete payments for passenger id 1
    Then the payments should be soft deleted

  Scenario: Hard delete payments
    Given I have a payment with passenger id 1
    When I hard delete payments for passenger id 1
    Then the payments should be hard deleted