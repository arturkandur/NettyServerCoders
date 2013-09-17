package httpServer;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.traffic.AbstractTrafficShapingHandler;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;

public class TrafficHandler extends ChannelTrafficShapingHandler{
	
	private static long lastWrittenBytes;
	private static long lastReadBytes;
	private static long lastReadThroughput;
	private static long lastWriteThroughput;
	
	private TrafficCounter counter1;
	

	public TrafficHandler(long checkInterval) {
        super(checkInterval);
        
    }
	
	@Override
    protected void doAccounting(TrafficCounter counter) {
		lastWrittenBytes = counter.lastWrittenBytes();
		lastReadBytes = counter.lastReadBytes();
		lastReadThroughput = counter.lastReadThroughput();
		lastWriteThroughput	= counter.lastWriteThroughput();
			
		this.counter1 = counter;
		
		
    }

	public static long getLastWrittenBytes() {
		return lastWrittenBytes;
	}

	public void setLastWrittenBytes(long lastWrittenBytes) {
		TrafficHandler.lastWrittenBytes = lastWrittenBytes;
	}

	public static long getLastReadBytes() {
		return lastReadBytes;
	}

	public void setLastReadBytes(long lastReadBytes) {
		TrafficHandler.lastReadBytes = lastReadBytes;
	}

	public static long getLastReadThroughput() {
		return lastReadThroughput;
	}

	public void setLastReadThroughput(long lastReadThroughput) {
		TrafficHandler.lastReadThroughput = lastReadThroughput;
	}

	public static long getLastWriteThroughput() {
		return lastWriteThroughput;
	}

	public void setLastWriteThroughput(long lastWriteThroughput) {
		TrafficHandler.lastWriteThroughput = lastWriteThroughput;
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
