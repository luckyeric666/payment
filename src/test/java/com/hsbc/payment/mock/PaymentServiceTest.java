package com.hsbc.payment.mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.hsbc.payment.PaymentException;
import com.hsbc.payment.PaymentService;

/**
 * @author yuancongjia
 * 
 * This class represents unit test for PaymentService.
 * It's for demo purpose only, not for completeness of testing logic.
 * Some of senarios match payment_service.feature
 * 
 */
@RunWith(MockitoJUnitRunner.class)
public class PaymentServiceTest {

	@Mock
	ConcurrentHashMap map;

	PaymentService paymentService;

	@Before
	public void setup() {
		paymentService = new PaymentService();
	}

	//loadInitialFile
	@Test
	public void testLoadInitialFileWithInvalidCurrency() throws Exception {
		//given
		List<String> initialFile = new ArrayList<String>() {
			{
				add("USDD 1000");
				add("HKD 100");
			}
		};

		//when then
		assertThatThrownBy(() -> {
			paymentService.loadInitialFile(initialFile);
		}).isInstanceOf(RuntimeException.class).hasMessageContaining("Currency must be 3 chars");

	}

	
	@Test
	public void testLoadInitialFile() throws Exception {
		//given
		List<String> initialFile = new ArrayList<String>() {
			{
				add("USD 1000");
				add("HKD 100");
				add("USD -100");
				add("CNY 2000");
				add("HKD 200");
			}
		};
		paymentService.setMap(map);

		//when 
		paymentService.loadInitialFile(initialFile);

		//then
		verify(map).merge(eq("USD"), eq(1000), any(BiFunction.class));
		verify(map).merge(eq("HKD"), eq(100), any(BiFunction.class));
		verify(map).merge(eq("USD"), eq(-100), any(BiFunction.class));
		verify(map).merge(eq("CNY"), eq(2000), any(BiFunction.class));
		verify(map).merge(eq("HKD"), eq(200), any(BiFunction.class));

	}

	//addPayment
	@Test
	public void testAddPaymentWithInvalidAmount() throws Exception {
		//given
		String line = "USD FIFTY";

		//when then
		assertThatThrownBy(() -> {
			paymentService.addPayment(line);
		}).isInstanceOf(PaymentException.class).hasMessageContaining("Amount must be integer");

	}

	//printPayments
	@Test
	public void testPrintPayments() throws Exception {
		//given
		ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<String, Integer>() {
			{
				put("HKD", 300);
				put("USD", 900);
				put("CNY", 2000);
			}
		};
		Map<String, Double> exchangeRate = new HashMap<String, Double>() {
			{
				put("AUD", 1.2);
				put("HKD", 6.5);
				put("CNY", 6.0);
			}
		};
		paymentService.setMap(map);
		paymentService.setExchangeRate(exchangeRate);

		//when then
		assertThat(paymentService.printPayments()).isEqualToIgnoringWhitespace(
				"HKD 300 (USD 46.15)\r\nUSD 900\r\nCNY 2000 (USD 333.33)\r\n");
	}

	//integration
	//Scenario: Load valid initial file and User type valid payment and all net to zero
	@Test
	public void testWithNetToAllZero() throws Exception {
		List<String> initialFile = new ArrayList<String>() {
			{
				add("USD 1000");
				add("HKD 100");
				add("USD -100");
				add("CNY 2000");
				add("HKD 200");
			}
		};
		Map<String, Double> exchangeRate = new HashMap<String, Double>() {
			{
				put("AUD", 1.2);
				put("HKD", 6.5);
				put("CNY", 6.0);
			}
		};
		paymentService.setExchangeRate(exchangeRate);

		//Given a initial file
		paymentService.loadInitialFile(initialFile);
		//And user type
		paymentService.addPayment("USD -900");
		paymentService.addPayment("CNY -2000");
		paymentService.addPayment("HKD -300");

		//When system prints
		//Then we should see
		assertThat(paymentService.printPayments()).isEqualToIgnoringWhitespace("");
	}

}
