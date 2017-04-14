Feature: PaymentService
  A payment can only be:
  * of 2 entries
  * Currency of 3 chars
  * Amount of integer
  Will abort if initial file is invalid.
  Will continue if user type is invalid.
  Will not print payment if net is 0.
  Will not print USD if no exchangeRate is setup.
  
  		
    
  Scenario: Load invalid initial file
    Given a initial file and we should have error "Currency must be 3 chars":
      | USDD 1000 |
      | HKD  100  |
       
       
  Scenario: Load valid initial file
    Given a initial file:
      | USD 1000 |
      | HKD 100  |
      | USD -100 |
      | CNY 2000 |
      | HKD 200  |
    When system prints
    Then we should see
      """
      HKD 300 (USD 46.15)
	  USD 900 
      CNY 2000 (USD 333.33)
      """
 
  Scenario: Load valid initial file and User type invalid payment
    Given a initial file:
      | USD 1000 |
      | HKD 100  |
      | USD -100 |
      | CNY 2000 |
      | HKD 200  |
    And user type "USD 100 200" and we should have error "Each line must be 2 entries"
    And user type "USDD 100" and we should have error "Currency must be 3 chars"   
    And user type "USD FIFTY" and we should have error "Amount must be integer" 
    When system prints
    Then we should see
      """
      HKD 300 (USD 46.15)
	  USD 900 
      CNY 2000 (USD 333.33)
      """
       
  Scenario: Load valid initial file and User type valid payment
    Given a initial file:
      | USD 1000 |
      | HKD 100  |
      | USD -100 |
      | CNY 2000 |
      | HKD 200  |  
    # net USD is -100
    And user type "USD -1000"
    # net HKD is 0
    And user type "HKD -300"
    # AUD has exchangeRate setup
    And user type "AUD 99"
    # SGD dosen't have exchangeRate setup
    And user type "SGD 200" 
    When system prints
    Then we should see
      """
      AUD 99 (USD 82.5)
	  SGD 200 
      USD -100 
      CNY 2000 (USD 333.33)
      """
      
  Scenario: Load valid initial file and User type valid payment and all net to zero
    Given a initial file:
      | USD 1000 |
      | HKD 100  |
      | USD -100 |
      | CNY 2000 |
      | HKD 200  |  
 	And user type "USD -900"
 	And user type "CNY -2000"
 	And user type "HKD -300"
    When system prints
    Then we should see
      """
      """
 
