Feature: Administrator search by username
  As an organizer
  I want to search administrators by username
  So I can find them quickly in large lists

  Scenario: Search returns matching administrators (200)
    Given There is an administrator with username "admin" and password "password" and email "admin@sample.app"
    And There is an administrator with username "adminHelper" and password "password" and email "helper@sample.app"
    And I login as "admin" with password "password"
    When I search administrators by username containing "adm"
    Then The response code is 200
    And the administrators search response should contain 2 results
    And the administrators search response should include administrator with username "admin"
    And the administrators search response should include administrator with username "adminHelper"

  Scenario: Search returns empty list when no match (200)
    Given There is an administrator with username "admin" and password "password" and email "admin@sample.app"
    And I login as "admin" with password "password"
    When I search administrators by username containing "NoAdminWillMatchThisToken"
    Then The response code is 200
    And the administrators search response should contain 0 results
