package net.limbomedia.dns.dns;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xbill.DNS.Message;

public class RunnerTCP implements Runnable {

    private static final Logger L = LogManager.getLogger(RunnerTCP.class);

    private Socket socket;
    private Resolver resolver;
    private boolean log;

    public RunnerTCP(Resolver resolver, Socket socket, boolean log) {
        super();
        this.resolver = resolver;
        this.socket = socket;
        this.log = log;
    }

    @Override
    public void run() {
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
                if (log) {
                    L.info(
                            "Query: {} from {}",
                            ResolverImpl.toString(query.getQuestion()),
                            socket.getRemoteSocketAddress());
                }

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
        } catch (Exception e) {
            L.warn(
                    "Error processing TCP request from {}:{}. {} -> {}.",
                    socket.getRemoteSocketAddress(),
                    socket.getPort(),
                    e.getClass().getSimpleName(),
                    e.getMessage(),
                    e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                /* Silent close */
            }
        }
    }
}
