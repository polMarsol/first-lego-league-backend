Feature: Manage Venue
    In order to manage venues
    As a user
    I want to be able to create, retrieve, edit and delete venues

    Scenario: Create a venue
        Given I login as "demo" with password "password"
        And There is no venue with name "My Venue"
        When I create a new venue with name "My Venue" and city "Lleida"
        Then The response code is 201
        And A venue with name "My Venue" and city "Lleida" exists

    Scenario: Retrieve a venue
        Given I login as "demo" with password "password"
        And There is no venue with name "Read Venue"
        And There is a venue with name "Read Venue" and city "Igualada"
        When I retrieve the venue with name "Read Venue"
        Then The response code is 200
        And The response contains venue name "Read Venue" and city "Igualada"

    Scenario: Update a venue
        Given I login as "demo" with password "password"
        And There is no venue with name "Update Venue"
        And There is a venue with name "Update Venue" and city "Barcelona"
        When I update the venue with name "Update Venue" to city "Manresa"
        Then The response code is 200
        And The venue with name "Update Venue" has city "Manresa"

    Scenario: Delete a venue
        Given I login as "demo" with password "password"
        And There is no venue with name "Delete Venue"
        And There is a venue with name "Delete Venue" and city "Tarragona"
        When I delete the venue with name "Delete Venue"
        Then The response code is 204
        And No venue with name "Delete Venue" exists
