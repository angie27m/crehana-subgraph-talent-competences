package com.acsendo.api.util;

import java.util.List;

import org.apache.poi.ss.formula.functions.T;

import graphql.relay.Connection;
import graphql.relay.Edge;
import graphql.relay.PageInfo;

@SuppressWarnings("hiding")
public class ConnectionUtil<T> implements Connection<T>{

	private int total_count;
	private final List<Edge<T>> edges;
	private final PageInfoUtil page_info;


	 public ConnectionUtil(int totalCount, List<Edge<T>> edges, PageInfoUtil pageInfo) {
		super();
		this.total_count = totalCount;
		this.edges = edges;
		this.page_info = pageInfo;
	}

	@Override
	 public List<Edge<T>> getEdges() {
	     return edges;
	 }

	 @Override
	 public PageInfo getPageInfo() {
	        return page_info;
	 
	 }

	public int getTotalCount() {
		return total_count;
	}

	public void setTotalCount(int totalCount) {
		this.total_count = totalCount;
	}

	@Override
	public String toString() {
		return "ConnectionUtilD [totalCount=" + total_count + ", edges=" + edges + ", pageInfo=" + page_info + "]";
	}
}
