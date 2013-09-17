package httpServer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.traffic.AbstractTrafficShapingHandler;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.GlobalEventExecutor;

import javax.activation.MimetypesFileTypeMap;

import Statistic.Connection;
import Statistic.Query;
import Statistic.Redirect;
import Statistic.ServerStatistic;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URLDecoder;
import java.nio.channels.ServerSocketChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.*;
import static io.netty.handler.codec.http.HttpMethod.*;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;


public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
    public static final String HTTP_DATE_GMT_TIMEZONE = "GMT";
    public static final int HTTP_CACHE_SECONDS = 60;
    
    public HttpServerHandler() {
        
    }
    
        
    @Override
    public void channelRead0(
            ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
    	
    	ServerStatistic.addNumberOfQuery();
    	ServerStatistic.addConnection(ctx, request);
    	ServerStatistic.addQuery(ctx, request);
    	    	
    	
    	
        if (!request.getDecoderResult().isSuccess()) {
            sendError(ctx, BAD_REQUEST);
            return;
        }

        if (request.getMethod() != GET) {
            sendError(ctx, METHOD_NOT_ALLOWED);
            return;
        }

        final String uri = request.getUri();
        final String path = parseUri(uri);
        if (path == null) {
            sendError(ctx, FORBIDDEN);
            return;
        }
        
        
        if (path.equalsIgnoreCase("/hello")) {
            sendHello(ctx, request);
        	return;
        }
        
        
        if (path.equalsIgnoreCase("/status")) {
            sendStatus(ctx, request);
        	return;
        }

        if (path.startsWith("/redirect?url=")) {
            sendRedirect(ctx, uri);
            return;
        }

                
        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        
        // Write the initial line and the header.
        ctx.write(response);

       
        // Write the end marker
        ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

        // Decide whether to close the connection or not.
        if (!isKeepAlive(request)) {
            // Close the connection when the whole content is written out.
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
        
        
        
    }
    
       

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if (ctx.channel().isActive()) {
            sendError(ctx, INTERNAL_SERVER_ERROR);
        }
    }

   
    private static String parseUri(String uri) {
        // Decode the path.
        try {
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            try {
                uri = URLDecoder.decode(uri, "ISO-8859-1");
            } catch (UnsupportedEncodingException e1) {
                throw new Error();
            }
        }

        if (!uri.startsWith("/")) {
            return null;
        }

        
        return uri;
    }

    private void sendHello(final ChannelHandlerContext ctx, FullHttpRequest request) throws InterruptedException {
    	ScheduledExecutorService executor = Executors.newScheduledThreadPool(10);  
    	executor.execute(new Runnable() {
            public void run() {
            	
            	FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
                response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
                
                try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
            	StringBuilder buf = new StringBuilder();
            	buf.append("<!DOCTYPE html>\r\n");
                buf.append("<html><head><title>");
                buf.append("Hello World </title></head><body>\r\n");

                buf.append("<h3> Hello World");
                buf.append("</h3>\r\n");
                buf.append("</body></html>\r\n");

                
                ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
                response.content().writeBytes(buffer);
                buffer.release();
                
                ctx.write(response);
                ChannelFuture writeFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
                
                writeFuture.addListener(ChannelFutureListener.CLOSE);
            }
        });
       }
    
    private static void sendStatus(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
                
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
        response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
        
        StringBuilder buf = new StringBuilder();
        

        buf.append("<!DOCTYPE html>\r\n");
        buf.append("<html><head><title>");
        buf.append("Status </title></head><body>\r\n");

        buf.append("<h2> Status:");
        buf.append("</h2>\r\n");
        buf.append("<h4> 1. Number of Queries: ");
        buf.append(ServerStatistic.getNumberOfQuery());
        buf.append("</h4>\r\n");
        
        buf.append("<h4> 2. Number of Active Connection: ");
        buf.append(ServerStatistic.getNumberOfConnection());
        
        buf.append("</h4>\r\n");
        
        buf.append("<h4> 3. Number of Redirects: </h4>\r\n");
        buf.append("<table border=\"3\"> <tr>  <td>Redirect</td><td>Number</td>  </tr>");
        
        Redirect [] redi = ServerStatistic.getRedirects();
        
        for (Redirect temp : redi){
        	
        	buf.append("<tr><td>" + temp.getUri()+" </td><td>" + temp.getNumberOfUri()+ "</td></tr>");
        	
        }
        
        buf.append("</table>\r\n");
       
        buf.append("<h4> 4. List of Connection: </h4>\r\n");
        
        buf.append("<table border=\"3\"> <tr>  <td>src_ip</td><td>URI</td><td>timestamp</td><td>send_bytes</td>"
        		+ "<td>recieved_bytes</td><td>speed (bytes/sec)</td>  </tr>");
        
        Connection [] conns = ServerStatistic.getConnections();
        for (Connection temp : conns){
        	
        	buf.append("<tr><td>" + temp.getSrc_ip()+" </td><td>" + temp.getUri()+ " </td><td>" + temp.getTimestamp() + " </td><td>" +
        			temp.getSent_bytes() + " </td><td>" + temp.getReceived_bytes() + " </td><td>" + temp.getSpeed() + "</td></tr>");
        	
        }
        
        buf.append("</table>\r\n");
        
                
        buf.append("<h4> 5. List of Query: </h4>\r\n");
        
        buf.append("<table border=\"3\"> <tr>  <td>IP</td><td>Number</td><td>Time last query</td>  </tr>");
        Query [] quers = ServerStatistic.getQueries();
        
        for(Query temp : quers){
        	buf.append("<tr><td>" + temp.getIp()+" </td><td>" + temp.getNumberOfQuery() + " </td><td>" + temp.getTimestamp() + "</td></tr>");
        	
        }
        
        buf.append("</table>\r\n");
        
        buf.append("<h4> 6. Number of Unique Queries: </h4>\r\n");
        
        buf.append("<table border=\"3\"> <tr>  <td>IP</td><td>Number of Unique Queries</td> </tr>");
        
        Query [] quers1 =  ServerStatistic.getQueries();
        
        for(Query temp : quers1){
        	buf.append("<tr><td>" + temp.getIp()+" </td><td>" + temp.getNumberOfUniqueQueries() + "</td></tr>");
        	
        }
        
        buf.append("</table>\r\n");
        
        
        buf.append("</body></html>\r\n");

        
        ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
        response.content().writeBytes(buffer);
        buffer.release();
        
        // Close the connection as soon as the error message is sent.
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
              
    }

    private static void sendRedirect(ChannelHandlerContext ctx, String newUri) {
    	
    	newUri = newUri.substring(17, newUri.length()-3); 
    	
    	ServerStatistic.addRedirect(newUri);
    	
    	FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK);
        response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");

        StringBuilder buf = new StringBuilder();
        
        buf.append("<meta http-equiv=\"refresh\" content=\"0; url=http://" + newUri +"\"> ");
        ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
        response.content().writeBytes(buffer);
        buffer.release();
    	
        // Close the connection as soon as the error message is sent.
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        
    }

    private static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HTTP_1_1, status, Unpooled.copiedBuffer("Failure: " + status.toString() + "\r\n", CharsetUtil.UTF_8));
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
        
        // Close the connection as soon as the error message is sent.
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        
    }
    
    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, CONTINUE);
        ctx.write(response);
    }

}
