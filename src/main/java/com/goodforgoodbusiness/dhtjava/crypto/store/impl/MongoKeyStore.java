package com.goodforgoodbusiness.dhtjava.crypto.store.impl;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.or;
import static com.mongodb.client.model.Indexes.ascending;
import static java.util.stream.StreamSupport.stream;

import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Stream;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.goodforgoodbusiness.dhtjava.crypto.primitive.key.EncodeableShareKey;
import com.goodforgoodbusiness.dhtjava.crypto.store.ShareKeyIndex;
import com.goodforgoodbusiness.dhtjava.crypto.store.ShareKeyStore;
import com.goodforgoodbusiness.shared.JSON;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

@Singleton
public class MongoKeyStore implements ShareKeyStore {
	private static final String CL_KEYS = "keys";
	
	private final MongoClient client;
	private final ConnectionString connectionString;
	private final MongoDatabase database;
	
	@Inject
	public MongoKeyStore(@Named("keystore.connectionUrl") String connectionUrl) {
		this.connectionString = new ConnectionString(connectionUrl);
		this.client =  MongoClients.create(connectionString);
		this.database = client.getDatabase(connectionString.getDatabase());
		
		var keyCollection = database.getCollection(CL_KEYS);
		
		keyCollection.createIndex(ascending("subject"));
		keyCollection.createIndex(ascending("predicate"));
		keyCollection.createIndex(ascending("object"));
		
		keyCollection.createIndex(ascending("key"), new IndexOptions().unique(true));
	}
	
	@Override
	public void saveKey(ShareKeyIndex idx, EncodeableShareKey key) {
		database
			.getCollection(CL_KEYS)
			.insertOne(
				Document
					.parse(JSON.encodeToString(idx))
					.append("key", Document.parse(JSON.encodeToString(key)))
			)
		;
	}

	@Override
	public Stream<EncodeableShareKey> findKeys(ShareKeyIndex idx) {
		var filters = new LinkedList<Bson>();
		
		if (idx.getSubject() != null) {
			filters.add(eq("subject", idx.getSubject()));
		}
		
		if (idx.getPredicate() != null) {
			filters.add(eq("predicate", idx.getPredicate()));
		}
		
		if (idx.getObject() != null) {
			filters.add(eq("object", idx.getObject()));
		}
		
		return 
			stream(
				database
					.getCollection(CL_KEYS)
					.find(or(filters))
					.spliterator(),
				true
			)
			.map(doc -> JSON.decode( ((Document)doc.get("key")).toJson() , EncodeableShareKey.class))
			.filter(Objects::nonNull)
		;
	}
}
