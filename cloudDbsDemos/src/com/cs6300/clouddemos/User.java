package com.cs6300.clouddemos;

import org.bson.types.ObjectId;

public class User {
	  private ObjectId id;
	  private String name;
	  private String encryptedPwd;

	  public ObjectId getId() {
	    return id;
	  }

	  public void setId(ObjectId id) {
	    this.id = id;
	  }

	  public String getName() {
	    return name;
	  }

	  public void setName(String name) {
	    this.name = name;
	  }
	  
	  public String getPwd() {
		  return encryptedPwd;
	  }
	  
	  public void setPwd(String pwd) {
		  this.encryptedPwd = pwd;
	  }

	  @Override
	  public String toString() {
	    return name;
	  }
} 
