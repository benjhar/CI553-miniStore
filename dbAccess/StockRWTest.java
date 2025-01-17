package dbAccess;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import catalogue.Product;
import middle.StockException;

class StockRWTest {

	private StockRW readWrite;
	
	@BeforeEach
	void init() throws StockException {
		readWrite = new StockRW();
	}
	
	@Test
	void check_sql_injection_false_negative() throws StockException {
		if (readWrite.buyStock("abcd", 1) != false) {
			fail("buyStock accepted non digit input");
		}
	}
	
	@Test
	void check_valid_buy() throws StockException {
		Product prod = readWrite.getDetails("0001");
		int originalStockLevel = prod.getQuantity();
		prod.setQuantity(0);
		readWrite.modifyStock(prod);
		readWrite.addStock("0001", 1);
		if (readWrite.getDetails("0001").getQuantity() != 1) {
			fail("Failed to add stock");
		}
		
		if (readWrite.exists("0001") == false) {
			fail("No longer exists");
		}
		
		if (readWrite.buyStock("0001", 2) == true) {
			fail("Buy without enough stock");
		}

		if (readWrite.buyStock("0001", 1) != true) {
			fail("Could not buy");
		}
		
		// Return database to original state.
		// TODO: use a testing database
		prod.setQuantity(originalStockLevel);
		
		readWrite.modifyStock(prod);
	}

}
