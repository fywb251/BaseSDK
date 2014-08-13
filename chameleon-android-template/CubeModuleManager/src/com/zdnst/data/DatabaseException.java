package com.zdnst.data;

public class DatabaseException extends Exception {

	private static final long serialVersionUID = 1396155837630180169L;

	public DatabaseException(Exception e) {
		super(e);
	}
}
