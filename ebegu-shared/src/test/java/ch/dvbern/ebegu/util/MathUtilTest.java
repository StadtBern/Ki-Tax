package ch.dvbern.ebegu.util;

import org.junit.Test;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests für MathUtil
 */
public class MathUtilTest {


	private static final RoundingMode DFLT_ROUNDING = RoundingMode.HALF_UP;
	private static final int DFLT_PRECISION = 19;
	private static final int DFLT_SCALE = 2;

	public static void assertCompare(@Nullable BigDecimal ref, @Nullable BigDecimal other) {
		String msg = "ref != other: " + ref + " != " + other;
		assertTrue(msg, ref == null ? other == null : other != null);
		if (ref != null) {
			assertEquals(msg, 0, ref.compareTo(other));
		}
	}

	@Test
	public void testFrom_long() throws Exception {
		assertNull(MathUtil.DEFAULT.from((Long) null));

		BigDecimal val = MathUtil.DEFAULT.from(123L);
		assertEquals(5, val.precision());
		assertEquals(DFLT_SCALE, val.scale());
		assertCompare(val, new BigDecimal(123L).setScale(DFLT_SCALE, DFLT_ROUNDING));
	}

	@Test
	public void testFrom_Double() throws Exception {
		assertNull(MathUtil.DEFAULT.from((Double) null));

		BigDecimal val = MathUtil.DEFAULT.from(123.45);
		assertEquals(5, val.precision());
		assertEquals(DFLT_SCALE, val.scale());

		assertCompare(val, BigDecimal.valueOf(123.45).setScale(DFLT_SCALE, DFLT_ROUNDING));
	}

	@Test
	public void testFrom_BigDecimal() throws Exception {
		assertNull(MathUtil.DEFAULT.from((BigDecimal) null));

		BigDecimal val = MathUtil.DEFAULT.from(BigDecimal.valueOf(123.45).setScale(DFLT_SCALE, DFLT_ROUNDING));
		assertEquals(5, val.precision());
		assertEquals(DFLT_SCALE, val.scale());

		assertCompare(val, BigDecimal.valueOf(123.45).setScale(DFLT_SCALE, DFLT_ROUNDING));
	}

	@Test
	public void testFrom_BigInteger() throws Exception {
		assertNull(MathUtil.DEFAULT.from((BigInteger) null));

		BigDecimal val = MathUtil.DEFAULT.from(BigInteger.valueOf(123));
		assertEquals(5, val.precision());
		assertEquals(DFLT_SCALE, val.scale());

		assertCompare(val, BigDecimal.valueOf(123).setScale(DFLT_SCALE, DFLT_ROUNDING));
	}

	@Test(expected = PrecisionTooLargeException.class)
	public void test_LargeMathUtil1() throws Exception {
		MathUtil.DEFAULT.from(new BigDecimal("12345678901234567890"));
	}

	@Test
	public void testAdd() throws Exception {
		BigDecimal val = MathUtil.DEFAULT.add(MathUtil.DEFAULT.from(123L), MathUtil.DEFAULT.from(456L));
		assertCompare(val, BigDecimal.valueOf(123L + 456L).setScale(DFLT_SCALE, DFLT_ROUNDING));
		assertCompare(BigDecimal.valueOf(579.00), val);
		assertEquals(5, val.precision());
		assertEquals(DFLT_SCALE, val.scale());
	}

	@Test
	public void testSubtract() throws Exception {
		BigDecimal val = MathUtil.DEFAULT.subtract(MathUtil.DEFAULT.from(456L), MathUtil.DEFAULT.from(123L));
		assertCompare(val, BigDecimal.valueOf(456L - 123L).setScale(DFLT_SCALE, DFLT_ROUNDING));
		assertCompare(BigDecimal.valueOf(333.00), val);
		assertEquals(5, val.precision());
		assertEquals(DFLT_SCALE, val.scale());
	}

	@Test
	public void testMultiply() throws Exception {
		BigDecimal val = MathUtil.DEFAULT.multiply(MathUtil.DEFAULT.from(123L), MathUtil.DEFAULT.from(456L));
		assertCompare(val, BigDecimal.valueOf(123L * 456L).setScale(DFLT_SCALE, DFLT_ROUNDING));
		assertCompare(BigDecimal.valueOf(56088.00), val);
		assertEquals(7, val.precision());
		assertEquals(DFLT_SCALE, val.scale());
	}

	@Test
	public void testMultiplyWithManyArguments() throws Exception {
		BigDecimal val = MathUtil.DEFAULT.multiply(MathUtil.DEFAULT.from(123L), MathUtil.DEFAULT.from(456L), MathUtil.DEFAULT.from(789L));
		assertCompare(val, BigDecimal.valueOf(123L * 456L * 789L).setScale(DFLT_SCALE, DFLT_ROUNDING));
		assertCompare(BigDecimal.valueOf(44253432.00), val);
		assertEquals(10, val.precision());
		assertEquals(DFLT_SCALE, val.scale());
	}

	@Test
	public void testDivide() throws Exception {
		BigDecimal val = MathUtil.DEFAULT.divide(MathUtil.DEFAULT.from(123L), MathUtil.DEFAULT.from(456L));
		assertCompare(val, BigDecimal.valueOf(123.0 / 456.0).setScale(DFLT_SCALE, DFLT_ROUNDING));
		// 0.27 (gerundet von 0.2697368421)
		assertCompare(BigDecimal.valueOf(0.27), val);
		assertEquals(2, val.precision());
		assertEquals(DFLT_SCALE, val.scale());
	}

	@Test
	public void testRoundToFrankenRappen() {
		assertEquals(new BigDecimal("1.05"), MathUtil.roundToFrankenRappen(new BigDecimal("1.051")));
		assertEquals(new BigDecimal("1.10"), MathUtil.roundToFrankenRappen(new BigDecimal("1.075")));
		assertEquals(new BigDecimal("1.10"), MathUtil.roundToFrankenRappen(new BigDecimal("1.0749")));
		assertEquals(new BigDecimal("1.05"), MathUtil.roundToFrankenRappen(new BigDecimal("1.0744")));
	}

	@Test
	public void testRoundIntToTens() {
		assertEquals(0, MathUtil.roundIntToTens(-1));
		assertEquals(0, MathUtil.roundIntToTens(0));
		assertEquals(0, MathUtil.roundIntToTens(1));
		assertEquals(10, MathUtil.roundIntToTens(5)); // special case giving errors in Java6
		assertEquals(10, MathUtil.roundIntToTens(10));
		assertEquals(10, MathUtil.roundIntToTens(11));
		assertEquals(10, MathUtil.roundIntToTens(14));
		assertEquals(20, MathUtil.roundIntToTens(15));
		assertEquals(20, MathUtil.roundIntToTens(16));
		assertEquals(20, MathUtil.roundIntToTens(19));
		assertEquals(152360, MathUtil.roundIntToTens(152362));
		assertEquals(152370, MathUtil.roundIntToTens(152365));
	}
}
