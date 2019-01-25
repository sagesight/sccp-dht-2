package com.goodforgoodbusiness.engine.crypto;

import static org.apache.jena.graph.NodeFactory.createURI;
import static org.apache.jena.sparql.util.NodeFactoryExtra.createLiteralNode;

import java.util.stream.Collectors;

import org.apache.jena.graph.Triple;

import com.goodforgoodbusiness.engine.Pattern;
import com.goodforgoodbusiness.engine.crypto.ClaimCrypter;
import com.goodforgoodbusiness.engine.crypto.PointerCrypter;
import com.goodforgoodbusiness.engine.crypto.primitive.key.EncodeableShareKey;
import com.goodforgoodbusiness.engine.store.keys.impl.MemKeyStore;
import com.goodforgoodbusiness.engine.store.keys.spec.ShareKeySpec;
import com.goodforgoodbusiness.kpabe.KPABEInstance;
import com.goodforgoodbusiness.model.Pointer;

public class PointerCrypterTest {
	public static void main(String[] args) throws Exception {
		var triple = new Triple(
			createURI("https://twitter.com/ijmad8x"),
			createURI("http://xmlns.com/foaf/0.1/name"),
			createLiteralNode("Ian Maddison", null, "http://www.w3.org/2001/XMLSchema#string")
		);
		
		var kpabe = KPABEInstance.newKeys();
		var store = new MemKeyStore();
		var crypter = new PointerCrypter(kpabe, store);
		
		crypter.saveShareKey(new ShareKeySpec(triple), new EncodeableShareKey(kpabe.shareKey(Pattern.forSearch(triple))));
				
		var key = new ClaimCrypter().getSecretKey();
		var pointer = new Pointer("abc123", key.toEncodedString(), 1234l);
		var data = crypter.encrypt(pointer, Pattern.forPublish(triple).collect(Collectors.toList()));
		
		System.out.println(data);
		
		var pointer2 = crypter.decrypt(triple, data);
		System.out.println(pointer2);
	}
}