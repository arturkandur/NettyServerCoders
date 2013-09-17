package Statistic;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import httpServer.StateHandler;
import httpServer.TrafficHandler;

public class ServerStatistic {
	
	private static long numberOfQuery = 0;
	private static long numberOfUniqueQuery = 0;
	
	private volatile static ArrayList<Redirect> redirects = new ArrayList<Redirect>();
	private volatile static ArrayList<Connection> connections = new ArrayList<Connection>();
	private volatile static ArrayList<Query> queries = new ArrayList<Query>();
	
	
	public synchronized static long getNumberOfQuery() {
		return numberOfQuery;
	}
	public synchronized static void addNumberOfQuery() {
		ServerStatistic.numberOfQuery++;
	}
	public synchronized static long getNumberOfUniqueQuery() {
		return numberOfUniqueQuery;
	}
	public synchronized static void setNumberOfUniqueQuery(long numberOfUniqueQuery) {
		ServerStatistic.numberOfUniqueQuery = numberOfUniqueQuery;
	}
	public static int getNumberOfConnection() {
		return StateHandler.activeConnection;
	}
	
	public synchronized static Redirect[] getRedirects() {
		Redirect [] tempRed = new Redirect[redirects.size()];
		tempRed = redirects.toArray(tempRed);
		return tempRed;
	}
	
	public synchronized static void addRedirect(String uri){
		boolean isRedirect = false;
		
		for (Redirect tempRedirects: redirects ){
			if (tempRedirects.getUri().equalsIgnoreCase(uri)){
				isRedirect = true;
				tempRedirects.addNumberOfUri(1);
			}
		}
		
		if(!isRedirect){
			Redirect newRedirect = new Redirect();
			newRedirect.setUri(uri);
			newRedirect.setNumberOfUri(1);
			redirects.add(newRedirect);
		}
	}
	
	public synchronized static void addConnection(ChannelHandlerContext ctx, FullHttpRequest request) throws ParseException{
		Connection newConn = new Connection();
				
		InetSocketAddress sa =   (InetSocketAddress) ctx.channel().remoteAddress();
    	InetAddress inetAddr = sa.getAddress();
    	
    	newConn.setSrc_ip(inetAddr);
    	newConn.setUri(request.getUri());
    	newConn.setTimestamp(getServerTime());
    	
    	//TrafficHandler traff = (TrafficHandler) ctx.channel().pipeline().get("shaper");
    	
    	newConn.setReceived_bytes(TrafficHandler.getLastReadBytes());
    	newConn.setSent_bytes(TrafficHandler.getLastWrittenBytes());
    	newConn.setSpeed(TrafficHandler.getLastReadThroughput() + TrafficHandler.getLastWriteThroughput());
    	
    	if  ( connections.size() > 15){
    		ArrayList<Connection> tempConn = new ArrayList<Connection>();
    		for (int i = 1; i<16; i++){
    			tempConn.add(connections.get(i));
    		}
    		
    		connections.clear();
    		connections.addAll(tempConn);
    	}
    	
    	connections.add(newConn);
	}
	
	public synchronized static Connection [] getConnections() {
		Connection [] conns = new Connection[connections.size()];
		conns = connections.toArray(conns);
		return conns;
	}
	
	
	
	
	
	public synchronized static void addQuery(ChannelHandlerContext ctx, FullHttpRequest request) throws ParseException{
		Query newQuery = new Query();
		
		boolean isAddr = false;
		boolean isUri = false;
		
		InetSocketAddress sa =   (InetSocketAddress) ctx.channel().remoteAddress();
    	InetAddress inetAddr = sa.getAddress();
    	
    	for (Query tempQuery : queries){
    		if(tempQuery.getUri().equalsIgnoreCase(request.getUri())){
    			isUri = true;
    			    			
    		}
    	}
    	
    	 	
    	
    	for (Query tempQuery : queries){
    		if(tempQuery.getIp().getHostAddress().equalsIgnoreCase(inetAddr.getHostAddress())){
    			isAddr = true;
    			tempQuery.addNumberOfQuery();
    			tempQuery.setTimestamp(getServerTime());
    			if(!isUri){
    				tempQuery.addNumberOfUniqueQueries();
    			}
    		}
    	}
    	
    	if (!isAddr && !isUri){
    		newQuery.addNumberOfUniqueQueries();
    		newQuery.setUri(request.getUri());
    		newQuery.setIp(inetAddr);
    		newQuery.addNumberOfQuery();
    		newQuery.setTimestamp(getServerTime());
    		queries.add(newQuery);
    	}
    	
	}
	
	
	
	public  synchronized static Query [] getQueries() {
		Query [] quers = new Query [queries.size()];
        quers = queries.toArray(quers);
		return quers;
	}
	
	
	private static String getServerTime() {
	    Calendar calendar = Calendar.getInstance();
	    SimpleDateFormat dateFormat = new SimpleDateFormat(
	        "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
	    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+2"));
	    return dateFormat.format(calendar.getTime());
	}
	
}
