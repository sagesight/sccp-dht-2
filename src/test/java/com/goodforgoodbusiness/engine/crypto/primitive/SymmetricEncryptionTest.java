package com.goodforgoodbusiness.engine.crypto.primitive;

import com.goodforgoodbusiness.engine.crypto.primitive.SymmetricEncryption;
import com.goodforgoodbusiness.engine.crypto.primitive.key.EncodeableSecretKey;

public class SymmetricEncryptionTest {
	public static void main(String[] args) throws Exception {
		// create
		EncodeableSecretKey key = SymmetricEncryption.createKey();
		
		var ciphertext = SymmetricEncryption.encrypt("hello world", key);
		System.out.println(ciphertext);
		
		System.out.println(key.toEncodedString());
		
		// decode
		var key2 = new EncodeableSecretKey(key.toEncodedString());
		
		System.out.println(SymmetricEncryption.decrypt(ciphertext, key2));
	}
}