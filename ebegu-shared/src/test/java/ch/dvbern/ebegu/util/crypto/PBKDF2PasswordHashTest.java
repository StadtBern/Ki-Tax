package ch.dvbern.ebegu.util.crypto;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static ch.dvbern.ebegu.util.crypto.PBKDF2PasswordHash.createHash;
import static ch.dvbern.ebegu.util.crypto.PBKDF2PasswordHash.validatePassword;

public class PBKDF2PasswordHashTest {
	private static final Logger LOG = LoggerFactory.getLogger(PBKDF2PasswordHashTest.class);

	@Test
	public void testCrypto() throws Exception {

		// Print out 10 hashes
		for (int i = 0; i < 10; i++) {
			String hash = createHash("p\r\nassw0Rd!");
			Assert.assertNotNull(hash);
//			LOG.info("generated hash: "+ hash);
		}

		// Test password validation
		for (int i = 0; i < 100; i++) {
			String password = "" + i;
			String hash = createHash(password);
			String secondHash = createHash(password);
			if (hash.equals(secondHash)) {
				Assert.fail("FAILURE: TWO HASHES ARE EQUAL!");
			}
			String wrongPassword = "" + (i + 1);
			if (validatePassword(wrongPassword, hash)) {
				Assert.fail("FAILURE: WRONG PASSWORD ACCEPTED!");
			}
			if (!validatePassword(password, hash)) {
				Assert.fail("FAILURE: GOOD PASSWORD NOT ACCEPTED!");
			}
		}
	}

	@Test
	public void testCreate() throws InvalidKeySpecException, NoSuchAlgorithmException {
		String hash = createHash("password10");
		Assert.assertNotNull(hash);
//		System.out.println(hash);
	}
}
