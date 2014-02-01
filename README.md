vaadin-sitekit
==============

Site Kit simplifies web site creation by providing functionality for registration, virtual hosting and business functionality.

Demo
----

https://54.246.89.141/groom

Demo and example site credentials are:

User email: "admin@admin.org"
User password: "password"

Features
--------

* Site description model which can be persisted for example to database.
    * Custom layouts and themes
    * Master pages
    * Page flows
    * Page versions
    * Navigation versions
* Virtual hosting
* Localization
* Privilege framework
* Access control
* Login / Logout
* Registration
* Email verification
* User Management
* Group Management
* Customer self registration with optional company registration
* Automatic creation of customer user groups
* Customer company self service on user group management
* User account information self service
* Example site
* Example theme
* Components
    * Site views with viewlet slots
    * Viewlets
    * Flow Viewlet (~component flows)
    * Flowlets (individual components which can be added to flows)
        * Navigating in the flow disabled when having dirty data.
    * Real time Vaadin 7 filtering data grid.
    * Real time validating data editor
        * Field status indicators and error messages.
        * Real time enabled Save / Discard buttons.
* Navigation Viewlet
* User Management Viewlet
* Group Management Viewlet
* Company Management Viewlet
* Customer Management Viewlet
* Extra validators and formatters

Requirements
------------

1. Java 7
2. Maven 3
3. Postgresql or MySQL (JPA, Eclipse Link and Liquibase used but other databases not tested.)

Usage
-----

1. Create maven project with Vaadin archetype.
2. Add Site Kit maven dependency to your project.
2. Create database with owner "site" and password "password"
3. Copy the example main class to you project.
4. Run the example and login with demo credentials.