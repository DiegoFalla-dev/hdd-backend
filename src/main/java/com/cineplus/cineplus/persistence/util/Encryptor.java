package com.cineplus.cineplus.persistence.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class Encryptor {

	private static final String ALGO = "AES";
	private static final String TRANSFORMATION = "AES/GCM/NoPadding";
	private static final int GCM_TAG_LENGTH = 128;
	private static final int IV_LENGTH = 12;

	private static byte[] keyBytes;

	@Value("${encryption.secret:}")
	private String secretKeyConfig;

	@PostConstruct
	public void init() {
		if (secretKeyConfig != null && !secretKeyConfig.isBlank()) {
			keyBytes = Base64.getDecoder().decode(secretKeyConfig);
		} else {
			// generate a random key if none provided (useful for tests/dev)
			try {
				KeyGenerator keyGen = KeyGenerator.getInstance(ALGO);
				keyGen.init(256);
				SecretKey key = keyGen.generateKey();
				keyBytes = key.getEncoded();
			} catch (Exception e) {
				throw new RuntimeException("Unable to generate encryption key", e);
			}
		}
	}

	private static SecretKeySpec keySpec() {
		return new SecretKeySpec(keyBytes, ALGO);
	}

	public static String encrypt(String plainText) {
		if (plainText == null) return null;
		try {
			byte[] iv = new byte[IV_LENGTH];
			SecureRandom random = new SecureRandom();
			random.nextBytes(iv);

			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec(), spec);
			byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

			byte[] combined = new byte[iv.length + encrypted.length];
			System.arraycopy(iv, 0, combined, 0, iv.length);
			System.arraycopy(encrypted, 0, combined, iv.length, encrypted.length);

			return Base64.getEncoder().encodeToString(combined);
		} catch (Exception e) {
			throw new RuntimeException("Error encrypting data", e);
		}
	}

	public static String decrypt(String cipherText) {
		if (cipherText == null) return null;
		try {
			byte[] decoded = Base64.getDecoder().decode(cipherText);
			byte[] iv = new byte[IV_LENGTH];
			System.arraycopy(decoded, 0, iv, 0, iv.length);
			int encLen = decoded.length - iv.length;
			byte[] enc = new byte[encLen];
			System.arraycopy(decoded, iv.length, enc, 0, encLen);

			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
			cipher.init(Cipher.DECRYPT_MODE, keySpec(), spec);
			byte[] decrypted = cipher.doFinal(enc);
			return new String(decrypted, StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new RuntimeException("Error decrypting data", e);
		}
	}
}
