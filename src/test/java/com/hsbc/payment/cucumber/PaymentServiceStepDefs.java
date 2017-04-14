package com.hsbc.payment.cucumber;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.test.context.ContextConfiguration;

import com.hsbc.Application;
import com.hsbc.payment.PaymentException;
import com.hsbc.payment.PaymentService;

import cucumber.api.DataTable;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * @author yuancongjia
 * This class represents integration test by specification/behavior
 * Senarios are written in Gherkin at "payment_service.feature"
 * 
 */
@ContextConfiguration(classes = Application.class, loader = SpringBootContextLoader.class)
public class PaymentServiceStepDefs {

	@Autowired
	private PaymentService paymentService;

	private String result;

	@Before
	public void setup() {
		paymentService.setMap(new ConcurrentHashMap<String, Integer>());
	}

	@Given("^a initial file:$")
	public void a_initial_file(List<String> lines) {
		paymentService.loadInitialFile(lines);
	}

	@Given("^a initial file and we should have error \"([^\"]*)\":$")
	public void a_initial_file_and_we_should_have_error(String error, DataTable lines)
			throws Throwable {
		assertThatThrownBy(() -> {
			paymentService.loadInitialFile(lines.asList(String.class));
		}).isInstanceOf(RuntimeException.class).hasMessageContaining(error);
	}

	@Given("^user type \"([^\"]*)\"$")
	public void user_type(String type) throws Throwable {
		paymentService.addPayment(type);
	}

	@Given("^user type \"([^\"]*)\" and we should have error \"([^\"]*)\"$")
	public void user_type_and_we_should_have_error(String type, String error) throws Throwable {
		assertThatThrownBy(() -> {
			paymentService.addPayment(type);
		}).isInstanceOf(PaymentException.class).hasMessageContaining(error);
	}

	@When("^system prints$")
	public void system_prints() throws Throwable {
		result = paymentService.printPayments();
	}

	@Then("^we should see$")
	public void we_should_see(String see) throws Throwable {
		assertThat(result).isEqualToIgnoringWhitespace(see);
	}

}
