package com.hsbc.payment;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@ConfigurationProperties(prefix = "payment.service")
public class PaymentService {

	private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

	@Autowired
	private ApplicationContext context;

	private Map<String, Double> exchangeRate;

	private Resource initialFile;

	private ConcurrentHashMap<String, Integer> map; // merged payments by currency

	public PaymentService() {
		map = new ConcurrentHashMap<String, Integer>();
	}

	public void start() throws IOException {

		// load initialFile if any
		if (initialFile != null) {
			log.info("Loading from initial file {}", initialFile);
			List<String> lines = IOUtils.readLines(initialFile.getInputStream(), "UTF-8");
			loadInitialFile(lines);
		}

		// user input
		log.info("Taking user input");
		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.println("Enter a payment:");
			String line = sc.nextLine();
			if (line.equalsIgnoreCase("quit")) {
				break;
			}
			try {
				addPayment(line);
			} catch (PaymentException e) {
				// continue program if user input is not valid
				log.error("Error reading user input " + line, e);
				System.err.println("Error reading user input " + line + ": " + e.getMessage());
			}
		}

		log.info("Program shutdown");
		SpringApplication.exit(context, () -> 0);

	}

	public void loadInitialFile(List<String> lines) {
		lines.forEach((l) -> {
			try {
				addPayment(l);
			} catch (PaymentException e) {
				// abort program if initial file is not valid
				log.error("Error loading initial file " + initialFile, e);
				throw new RuntimeException(e);
			}
		});
	}

	public void addPayment(String line) throws PaymentException {
		// parse
		String[] la = line.trim().split(" ");

		// validate
		if (la.length != 2) {
			throw new PaymentException("Each line must be 2 entries");
		} else if (!la[0].matches("^[a-zA-Z]{3}$")) {
			throw new PaymentException("Currency must be 3 chars");
		} else if (!la[1].matches("^-?\\d+$")) {
			throw new PaymentException("Amount must be integer");
		}

		// merge amount by currency
		String currency = la[0].toUpperCase();
		Integer amount = Integer.parseInt(la[1]);
		map.merge(currency, amount, (oldAmt, newAmt) -> oldAmt + newAmt);
	}

	@Scheduled(fixedDelayString = "${payment.service.pollingInMil}")
	public String printPayments() {
		log.info("Printing payments:");

		String result = map
				.entrySet()
				.stream()
				.map(entry -> {
					String currency = entry.getKey();
					Integer amount = entry.getValue();
					if (amount.equals(0)) {
						return "";
					} else {
						String USD = exchangeRate.containsKey(currency) ? "(USD "
								+ new DecimalFormat("#.##").format(amount
										/ exchangeRate.get(currency)) + ")" : "";
						return currency + " " + amount + " " + USD + "\r\n"; //eg: HKD 300 (USD 46.15)
					}
				}).collect(Collectors.joining());

		System.out.print(result);
		return result;

	}

	// getter and setter
	public Map<String, Double> getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(Map<String, Double> exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public Resource getInitialFile() {
		return initialFile;
	}

	public void setInitialFile(Resource initialFile) {
		this.initialFile = initialFile;
	}

	public ConcurrentHashMap<String, Integer> getMap() {
		return map;
	}

	public void setMap(ConcurrentHashMap<String, Integer> map) {
		this.map = map;
	}

}
