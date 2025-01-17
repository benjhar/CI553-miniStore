package middle;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import dbAccess.StockRW;

class StockReadWriterTest {

	@Test
	void check_sql_injection() throws StockException {
		StockRW readWrite = new StockRW();
		if (readWrite.buyStock("abcd", 1) != false) {
			fail("buyStock accepted non digit input");
		}
	}

}
