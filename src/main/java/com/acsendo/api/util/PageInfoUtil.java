package com.acsendo.api.util;

import graphql.relay.ConnectionCursor;
import graphql.relay.PageInfo;

public class PageInfoUtil implements PageInfo{
	
    private ConnectionCursor start_cursor;
    private ConnectionCursor end_cursor;
	private Boolean has_previous_page;
    private Boolean has_next_page;


    
	public PageInfoUtil(ConnectionCursor start_cursor, ConnectionCursor end_cursor, Boolean has_previous_page,
			Boolean has_next_page) {
		super();
		this.start_cursor = start_cursor;
		this.end_cursor = end_cursor;
		this.has_previous_page = has_previous_page;
		this.has_next_page = has_next_page;
	}

	@Override
	public ConnectionCursor getStartCursor() {
		// TODO Auto-generated method stub
		return start_cursor;
	}

	@Override
	public ConnectionCursor getEndCursor() {
		// TODO Auto-generated method stub
		return end_cursor;
	}

	@Override
	public boolean isHasPreviousPage() {
		// TODO Auto-generated method stub
		return has_previous_page;
	}

	@Override
	public boolean isHasNextPage() {
		// TODO Auto-generated method stub.
		return has_next_page;
	}

}
