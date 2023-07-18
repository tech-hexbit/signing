package com.sign;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;

public class VerifySign {

	static final Logger log = Logger.getLogger(VerifySign.class.getName());

	static final String PUBLIC_KEY = "5ibCsc7A0QoJYMdtr9RvsaRPKDgZ2F1bJGtR8pz2rFg=";
	static final String KID = "beta.mystore.in|96c42708-b788-4410-bafe-3c82a282a1aa|ed25519";

	static final String SIGNATURE = "b5clV8mEPvS/IE2vQgra/CUa6alfjVqlabA2lm52WnowyiKFaFmWtfLMdScGGUlCBrsYC8aTBOF3Bk/KDhxwAg==";
	static final int CREATED = 1658669027;
	static final int EXPIRES = 1658670027;

	static final String FILE_PATH = "src/main/resources/request.json";

	public static void main(String[] args) throws Exception {

		String req = Files.lines(Paths.get(FILE_PATH)).collect(Collectors.joining(System.lineSeparator()));
		System.out.println("request body is: " + req);
		System.out.println("\n");

		String blakeHash = generateBlakeHash(req);
		System.out.println("\n");

		String signingString = "(created): " + CREATED + "\n(expires): " + EXPIRES + "\ndigest: BLAKE-512=" + blakeHash + "";

		System.out.println("Signature:=> " + SIGNATURE);

		// To Verify Signature
		System.out.println("\n==============================Verify Signature================================");

		verifySignature(SIGNATURE, signingString, PUBLIC_KEY);
	}

	public static String generateBlakeHash(String req) {
		Blake2bDigest blake2bDigest = new Blake2bDigest(512);
		byte[] test = req.getBytes();
		blake2bDigest.update(test, 0, test.length);
		byte[] hash = new byte[blake2bDigest.getDigestSize()];
		blake2bDigest.doFinal(hash, 0);
		String bs64 = Base64.getEncoder().encodeToString(hash);
		System.out.println("BlakeHash is: " + bs64);
		return bs64;
	}

	public static boolean verifySignature(String sign, String requestData, String dbPublicKey) {
		boolean isVerified = false;
		try {
			Ed25519PublicKeyParameters publicKey = new Ed25519PublicKeyParameters(Base64.getDecoder().decode(dbPublicKey), 0);
			Signer sv = new Ed25519Signer();
			sv.init(false, publicKey);
			sv.update(requestData.getBytes(), 0, requestData.length());

			byte[] decodedSign = Base64.getDecoder().decode(sign);
			isVerified = sv.verifySignature(decodedSign);
			System.out.println("Is Sign Verified : " + isVerified);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isVerified;
	}

}
