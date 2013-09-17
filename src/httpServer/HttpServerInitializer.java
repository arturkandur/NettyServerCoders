package httpServer;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
	
	
	HttpServerInitializer(){
		
	}
		
    @Override
    public void initChannel(SocketChannel ch) throws Exception {
    	// Create a default pipeline implementation.
        ChannelPipeline pipeline = ch.pipeline();
        EventExecutorGroup e1 = new DefaultEventExecutorGroup(10);
        
        // Uncomment the following line if you want HTTPS
        //SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
        //engine.setUseClientMode(false);
        //pipeline.addLast("ssl", new SslHandler(engine));
        
        pipeline.addLast("stateHandler", new StateHandler());
        pipeline.addLast("shaper", new TrafficHandler(1000000));
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
        
        
        //pipeline.addLast("Hellohandler", new HttpHelloHandler());
        pipeline.addLast("handler", new HttpServerHandler());
        
        
    }
    
    

}
