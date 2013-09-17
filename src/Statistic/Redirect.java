package Statistic;

public class Redirect {
	private String uri;
	private int numberOfUri;
	
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public int getNumberOfUri() {
		return numberOfUri;
	}
	public void setNumberOfUri(int numberOfUri) {
		this.numberOfUri = numberOfUri;
	}
	
	public void addNumberOfUri(int numberOfUri) {
		this.numberOfUri += numberOfUri;
	}
}
