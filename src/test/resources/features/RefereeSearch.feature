Feature: Referee search by name
  As an organizer
  I want to search referees by name
  So I can find them quickly in large lists

  Scenario: Search returns matching referees (200)
    Given referees exist with names "Maria Garcia" and "Alice Brown"
    When I search referees by name containing "maria"
    Then The response code is 200
    And the referees search response should contain 1 result
    And the referees search response should include referee named "Maria Garcia"

  Scenario: Search returns empty list when no match (200)
    Given referees exist with names "Mark White" and "Anna Green"
    When I search referees by name containing "NoRefereeWillMatchThisToken"
    Then The response code is 200
    And the referees search response should contain 0 results
