#Payment Java Coding Task

#####The program   
load payments from an optional initialFile  
then take input from user  
will abort if initial file is invalid  
will continue if user type is invalid  
will print payment every 10 secs  
will print USD if exchangeRate is setup  
will not print payment if net is 0  
will not print USD if no exchangeRate is setup  



#####User input can only be  
of 2 entries  
Currency of 3 chars  
Amount of integer  
eg: "USD 100", "quit"    
ps: Don't worry about the exception trace if input is of wrong format, they are just logs.  



#####Config in application.yml   
_payment.service.pollingInMil_&nbsp;&nbsp;&nbsp;for printing rate, default _10sec_  
_payment.service.exchangeRate_&nbsp;&nbsp;&nbsp;for adding/deleting exchange rate, default _{"AUD":1.2,"HKD":6.5,"CNY":6.0}_  
_payment.service.initialFile_&nbsp;&nbsp;&nbsp;for default initial file, default _#TO BE OVERRIDEN#_       


#Running the code

#####running from maven
cd \{project_root\}  
mvn spring-boot:run -Drun.arguments="--payment.service.initialFile=file:src/test/resources/initial.txt"

#####or running in a microservice
cd \{project_root\}  
mvn clean install  
cd \{project_root\}/target  
java -jar payment-0.0.1.jar --payment.service.initialFile=file:../src/test/resources/initial.txt 

#####or running in eclipse
if you dont want to supply initialFile  
Right click _com.hsbc.Application_  
Run As / Java Application  

if you want to supply initialFile  
Right click _com.hsbc.Application_  
Run As / Run Configurations...  
Java Application / Arguments / Program arguments  
put "--payment.service.initialFile=file:D:/workspace/hsbc/payment/src/test/resources/initial.txt"  
Click Run  



#Running the tests

#####BDD Integration Test
I've decided to use Cucumber to demo integration test by specification/behavior.  
Scenarios are self-explain. Testing code is concise/minimal.  
Feature file: *payment_service.feature*  
StepDef: _com.hsbc.payment.cucumber.PaymentServiceStepDefs_  


Right click _com.hsbc.payment.cucumber.PaymentServiceIT_  
Run As / Junit Test

#####Unit Test
I've also done a unit test to demo the diff between traditional unit/integration test and bdd test.   
Testing code is not self-explain and not concise/minimal.  

Right click _com.hsbc.payment.mock.PaymentServiceTest_  
Run As / Junit Test


![alt text](file:test_result.jpg "Test Result")


