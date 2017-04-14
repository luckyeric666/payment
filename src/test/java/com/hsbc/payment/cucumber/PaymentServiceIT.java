package com.hsbc.payment.cucumber;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;



@RunWith(Cucumber.class)
@CucumberOptions(glue = {"com.hsbc.payment.cucumber"})
public class PaymentServiceIT {
}
