package net.limbomedia.dns.dns;

import java.io.IOException;
import java.net.Socket;
import org.xbill.DNS.Message;

public interface Resolver {

    byte[] generateReply(Message query, byte[] in, int length, Socket socket) throws IOException;

    byte[] formerrMessage(byte[] in);
}
