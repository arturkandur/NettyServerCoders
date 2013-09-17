package httpServer;

import java.util.Date;

import Statistic.ServerStatistic;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpRequest;

public class StateHandler extends ChannelInboundHandlerAdapter {
	
	public static int activeConnection = 0;
	TrafficHandler trafficHandler;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("xxx exception caught: " + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    	
    	Date date = new Date();
    	activeConnection--;
        //System.out.println("channel went inactive" + ctx.name() + " " + date.getSeconds());
        super.channelInactive(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        //System.out.println("channel went unregistered");
        super.channelUnregistered(ctx);

    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    	
    	Date date = new Date();
    	ctx.name();
    	activeConnection++;
        //System.out.println("channel went active " + ctx.name() + " " +date.getSeconds());
        super.channelActive(ctx);
    }
    
        
}
