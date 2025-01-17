package orders;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import catalogue.Basket;
import catalogue.Product;
import middle.OrderException;

class OrderTest {

	private Order order;
	private Basket bought;
	
	@BeforeEach
	void setUpBeforeClass() throws Exception {
		order = new Order();
		bought = new Basket();
		bought.add(new Product("0001", "Test", 0, 0));
	}

	@Test
	void testUniqueNumber() throws OrderException {
		int first = order.uniqueNumber();
		int second = order.uniqueNumber();
		if (first == second) {
			fail("Non unique numbers generated");
		}
	}
	
	@Test
	void testFullRun() throws OrderException {
		order.newOrder(bought);
		
		Basket toPack = order.getOrderToPack();
		
		if (toPack != bought) {
			fail("Received to-pack order not identical to that bought");
		}
		
		if (!order.informOrderPacked(bought.getOrderNum())) {
			fail("Order number 1 was not being packed");
		}
		
		if (!order.informOrderCollected(bought.getOrderNum())) {
			fail("Order number 1 was not ready to be collected");
		}
	}
	
	@Test
	void testNewOrder() throws OrderException {
		order.newOrder(bought);
		
		if (order.getOrderState().get("Waiting").get(0) != bought.getOrderNum()) {
			fail("newOrder did not insert waiting order");
		}
	}
	
	@Test
	void testGetOrderToPack() throws OrderException {
		if (order.getOrderToPack() != null) {
			fail("Received non null from what should be null");
		}
		
		order.newOrder(bought);
	
		if (order.getOrderToPack() != bought) {
			fail("Received to-pack order not identical to that bought");
		}
	}
	
	@Test
	void testInformOrderPacked() throws OrderException {
		order.newOrder(bought);
		int orderId = bought.getOrderNum();
		order.getOrderToPack();
		
		if (!order.informOrderPacked(orderId)) {
			fail("Could not pack given order");
		}
		
		int otherOrderNumber = 0;
		while (otherOrderNumber == orderId) {
			otherOrderNumber++;
		}
		
		if (order.informOrderPacked(otherOrderNumber)) {
			fail("Packed non-existant order");
		}
	}

	@Test
	void testInformOrderCollected() throws OrderException {
		order.newOrder(bought);
		int orderId = bought.getOrderNum();
		order.getOrderToPack();
		order.informOrderPacked(orderId);
		
		if (!order.informOrderCollected(orderId)) {
			fail("Could not collect given order");
		}
		
		int otherOrderNumber = 0;
		while (otherOrderNumber == orderId) {
			otherOrderNumber++;
		}
		
		if (order.informOrderCollected(otherOrderNumber)) {
			fail("Collected non-existant order");
		}	
	}
	
	@Test
	void testOrderState() throws OrderException {
		Map<String, List<Integer>> state;
		state = order.getOrderState();
		if (state.get("Waiting") != null && !state.get("Waiting").isEmpty()) {
			fail("Should be no waiting orders");
		}
		if (state.get("BeingPacked") != null && !state.get("BeingPacked").isEmpty()) {
			fail("Should be no beingpacked orders");
		}
		if (state.get("ToBeCollected") != null && !state.get("ToBeCollected").isEmpty()) {
			fail("Should be no toBeCollected orders");
		}
		
		order.newOrder(bought);
		state = order.getOrderState();
		
		if (state.get("Waiting").isEmpty()) {
			fail("bought should be waiting");
		}
		
		order.getOrderToPack();
		state = order.getOrderState();
		
		if (state.get("BeingPacked") != null && state.get("BeingPacked").isEmpty()) {
			fail("bought should be being packed");
		}
		if (state.get("Waiting") != null && !state.get("Waiting").isEmpty()) {
			fail("Should be no waiting orders");
		}
	    
		order.informOrderPacked(bought.getOrderNum());
		state = order.getOrderState();

		if (state.get("ToBeCollected") != null && state.get("ToBeCollected").isEmpty()) {
			fail("bought should be no to-be-collected");
		}
		if (state.get("BeingPacked") != null && !state.get("BeingPacked").isEmpty()) {
			fail("Should be no beingpacked orders");
		}
		
		order.informOrderCollected(bought.getOrderNum());
		state = order.getOrderState();

		if (state.get("ToBeCollected") != null && !state.get("ToBeCollected").isEmpty()) {
			fail("Should be no toBeCollected orders");
		}
	}
}
