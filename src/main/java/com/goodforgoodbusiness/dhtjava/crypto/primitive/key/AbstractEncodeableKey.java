package com.goodforgoodbusiness.dhtjava.crypto.primitive.key;

import java.security.Key;

public abstract class AbstractEncodeableKey implements Key, EncodeableKey {
	protected final Key key;

	public AbstractEncodeableKey(Key key) {
		this.key = key;
	}

	@Override
	public String getAlgorithm() {
		return key.getAlgorithm();
	}

	@Override
	public String getFormat() {
		return key.getFormat();
	}

	@Override
	public byte[] getEncoded() {
		return key.getEncoded();
	}
	
	@Override
	public int hashCode() {
		return key.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) { 
			return true;
		}
		
		if (o instanceof AbstractEncodeableKey) {
			return key.equals(((AbstractEncodeableKey)o).key);
		}
		
		if (o instanceof Key) {
			return key.equals((Key)o);
		}
		
		return false;
	}
}
