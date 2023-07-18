package com.sign;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;

public class Sign {

	static final Logger log = Logger.getLogger(Sign.class.getName());

	static final String PRIVATE_KEY = "nEBl8KdG95oGlaxOAtvWI8jk6s4H6ShLykfLDtEzckHmJsKxzsDRCglgx22v1G+xpE8oOBnYXVska1HynPasWA==";
	static final String KID = "beta.mystore.in|96c42708-b788-4410-bafe-3c82a282a1aa|ed25519";

	private static final int VALIDITY = 1000;
	static final String FILE_PATH = "src/main/resources/request.json";

	public static void main(String[] args) throws Exception {
		log.setLevel(Level.INFO);

		String req = Files.lines(Paths.get(FILE_PATH)).collect(Collectors.joining(System.lineSeparator()));
		System.out.println("request body is: " + req);

		long currentTime = System.currentTimeMillis() / 1000L;
		// long currentTime = 1625252924; // old time to test for expired messages
		System.out.println("Timestamp :" + currentTime);

		System.out.println("\n==============================Json Request===================================");

		String blakeHash = generateBlakeHash(req);

		System.out.println("\n==============================Digest Value ===================================");
		String signingString = "(created): " + currentTime + "\n(expires): " + (currentTime + VALIDITY)
				+ "\ndigest: BLAKE-512=" + blakeHash + "";

		System.out.println("\n==============================Data to Sign===================================");
		System.out.println("header signingString:=>" + signingString);

		String signature = generateSignature(signingString, PRIVATE_KEY);

		System.out.println("\n==============================Signature===================================");

		System.out.println("Signature:=> " + signature);
		String authHeader = "Signature keyId=\"" + KID + "\",algorithm=\"ed25519\", created=\"" + currentTime
				+ "\", expires=\"" + (currentTime + VALIDITY)
				+ "\", headers=\"(created) (expires) digest\", signature=\"" + signature + "\"";

		System.out.println("\n==============================Signed Request=================================");
		System.out.println("Auth Header is:=> " + authHeader);

	}

	public static String generateSignature(String req, String pk) {
		String signature = null;
		try {
			Ed25519PrivateKeyParameters privateKey = new Ed25519PrivateKeyParameters(Base64.getDecoder().decode(pk.getBytes()), 0);
			Signer sig = new Ed25519Signer();
			sig.init(true, privateKey);
			sig.update(req.getBytes(), 0, req.length());
			byte[] s1 = sig.generateSignature();
			signature = Base64.getEncoder().encodeToString(s1);
		} catch (DataLengthException | CryptoException e) {
			e.printStackTrace();
		}
		return signature;
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

}
