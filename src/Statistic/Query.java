package Statistic;

import java.net.InetAddress;
import java.util.Date;

public class Query {

	private InetAddress ip;
	private String uri;
	private String timestamp;
	private int numberOfQuery;
	private int numberOfUniqueQueries;
	
	public InetAddress getIp() {
		return ip;
	}
	public void setIp(InetAddress ip) {
		this.ip = ip;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public int getNumberOfQuery() {
		return numberOfQuery;
	}
	public void addNumberOfQuery() {
		this.numberOfQuery += 1;
	}
	
	public long getNumberOfUniqueQueries() {
		return numberOfUniqueQueries;
	}
	public void addNumberOfUniqueQueries() {
		this.numberOfUniqueQueries += 1;
	}
	 
	
}
