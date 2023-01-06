package vttp2022.paf.assessment.eshop.controllers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import vttp2022.paf.assessment.eshop.models.Customer;
import vttp2022.paf.assessment.eshop.models.Order;
import vttp2022.paf.assessment.eshop.models.OrderStatus;
import vttp2022.paf.assessment.eshop.respositories.CustomerRepository;
import vttp2022.paf.assessment.eshop.respositories.OrderRepository;
import vttp2022.paf.assessment.eshop.services.WarehouseService;

@RestController
@RequestMapping(path = "/api")
public class OrderController {

	@Autowired
	private CustomerRepository cRepo;

	@Autowired
	private OrderRepository oRepo;

	@Autowired
	private WarehouseService wService;

	//TODO: Task 3
	@PostMapping(path = "/order", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> InsertOrder(@RequestBody Order o) {
		String name = o.getName();
		System.out.println(">>>>>>" + name);
		JsonObjectBuilder job = Json.createObjectBuilder();
		Optional<Customer> oc = cRepo.findCustomerByName(name);
		if(oc.isEmpty()) {
			job.add("error", "Customer %s not found".formatted(name));
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(job.build().toString());
		}
		Customer c = oc.get();
		o.setOrderId(UUID.randomUUID().toString().substring(0,8));
		o.setCustomer(c);
		if(!(oRepo.insertFullOrder(o))) {
			JsonObjectBuilder job2 = Json.createObjectBuilder();
			job2.add("error", "Error inserting order");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(job2.build().toString());
		}

		OrderStatus os = wService.dispatch(o);
		if(os.getStatus().equals("dispatched")) {
			oRepo.updateOrder(os, o.getOrderId());
			oRepo.insertOrderStatus(os);

			o.setDeliveryId(os.getDeliveryId());
			o.setStatus(os.getStatus());

			JsonObjectBuilder results = Json.createObjectBuilder();
			results.add("orderId", o.getOrderId());
			results.add("deliveryId", o.getDeliveryId());
			results.add("status", o.getStatus());
			return ResponseEntity.status(HttpStatus.ACCEPTED).body(results.build().toString());
		}

		JsonObjectBuilder results = Json.createObjectBuilder();
		results.add("orderId", o.getOrderId());
		results.add("status", "pending");
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(results.build().toString());
	}

	@GetMapping(path = "/order/{name}/status", produces = MediaType.APPLICATION_JSON_VALUE) 
	public ResponseEntity<String> checkStatusOfOrders(@PathVariable String name) {
		System.out.println("name >>>>>>>>> " + name);
		List<Integer> orders = oRepo.checkStatus(name);
		Integer dispatched = orders.get(0);
		Integer pending = orders.get(1);

		JsonObjectBuilder job = Json.createObjectBuilder();
		job.add("name", name);
		job.add("dispatched", dispatched);
		job.add("pending", pending);

		return ResponseEntity.ok().body(job.build().toString());
	}
}
