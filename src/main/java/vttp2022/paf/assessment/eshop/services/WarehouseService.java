package vttp2022.paf.assessment.eshop.services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import vttp2022.paf.assessment.eshop.models.LineItem;
import vttp2022.paf.assessment.eshop.models.Order;
import vttp2022.paf.assessment.eshop.models.OrderStatus;

@Service
public class WarehouseService {

	// You cannot change the method's signature
	// You may add one or more checked exceptions
	public OrderStatus dispatch(Order order) {

		// TODO: Task 4
		String url = "http://paf.chuklee.com/dispatch/{orderId}";

		JsonArrayBuilder jab = Json.createArrayBuilder();
		for (LineItem li : order.getLineItems()) {
			JsonObjectBuilder job = Json.createObjectBuilder();
			job.add("item", li.getItem());
			job.add("quantity", li.getQuantity());
			jab.add(job.build());
		}

		JsonObjectBuilder job = Json.createObjectBuilder();
		job.add("orderId", order.getOrderId());
		job.add("name", order.getName());
		job.add("address", order.getAddress());
		job.add("email", order.getEmail());
		job.add("lineItems", jab.build());
		job.add("createdBy", "Brendon Teo Wei Jie");

		RestTemplate rTemplate = new RestTemplate();
		ResponseEntity<String> resp = null;
		OrderStatus os = new OrderStatus();

		try {
			resp = rTemplate.exchange(RequestEntity.post
			(url, order.getOrderId()).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).body(job.build().toString()),
				String.class);
			
			System.out.println(resp.getBody());

			InputStream is = new ByteArrayInputStream(resp.getBody().getBytes());
			JsonReader r = Json.createReader(is);
			JsonObject o = r.readObject();

			os.setDeliveryId(o.getString("deliveryId"));
			os.setOrderId(o.getString("orderId"));
			os.setStatus("dispatched");

			return os;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		os.setOrderId(order.getOrderId());
		os.setStatus("Pending");
		return os;
	}
}
