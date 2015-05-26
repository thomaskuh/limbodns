package net.limbomedia.dns.dns;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.Message;

public class RunnerTCP implements Runnable {
	
	private static final Logger LOG = LoggerFactory.getLogger(RunnerTCP.class);
	
	private Socket socket;
	private Resolver resolver;
	
	public RunnerTCP(Resolver resolver, Socket socket) {
		super();
		this.resolver = resolver;
		this.socket = socket;
	}

	@Override
	public void run() {
		try{
			try {
				int inLength;
				DataInputStream dataIn;
				DataOutputStream dataOut;
				byte[] in;

				InputStream is = socket.getInputStream();
				dataIn = new DataInputStream(is);
				inLength = dataIn.readUnsignedShort();
				in = new byte[inLength];
				dataIn.readFully(in);

				Message query;
				byte[] response = null;
				try {
					query = new Message(in);
					LOG.info("Query: " + ResolverImpl.toString(query.getQuestion()) + " from " + socket.getRemoteSocketAddress());

					response = resolver.generateReply(query, in, in.length, socket);

					if (response == null) {
						return;
					}
				} catch (IOException e) {
					response = resolver.formerrMessage(in);
				}
				dataOut = new DataOutputStream(socket.getOutputStream());
				dataOut.writeShort(response.length);
				dataOut.write(response);
			} catch (IOException e) {
				LOG.error("Error sending TCP response to " + socket.getRemoteSocketAddress() + ":" + socket.getPort() + ", " + e);

			} finally {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}

		}catch(Exception e){
			LOG.error("Error processing TCP connection from " + socket.getRemoteSocketAddress() + ". " + e.getMessage(),e);
		}		
	}

}
