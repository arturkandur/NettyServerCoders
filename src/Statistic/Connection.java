package Statistic;

import java.net.InetAddress;
import java.util.Date;

public class Connection {

	private InetAddress src_ip;
	private String uri;
	private String timestamp;
	private long sent_bytes;
	private long received_bytes;
	private long speed;
	
	public InetAddress getSrc_ip() {
		return src_ip;
	}
	public void setSrc_ip(InetAddress src_ip) {
		this.src_ip = src_ip;
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
	public long getSent_bytes() {
		return sent_bytes;
	}
	public void setSent_bytes(long sent_bytes) {
		this.sent_bytes = sent_bytes;
	}
	public long getReceived_bytes() {
		return received_bytes;
	}
	public void setReceived_bytes(long received_bytes) {
		this.received_bytes = received_bytes;
	}
	public long getSpeed() {
		return speed;
	}
	public void setSpeed(long speed) {
		this.speed = speed;
	}
	
}
