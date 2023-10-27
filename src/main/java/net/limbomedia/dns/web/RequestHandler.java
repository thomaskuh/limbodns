package net.limbomedia.dns.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.kuhlins.lib.webkit.ex.ClientException;

@FunctionalInterface
public interface RequestHandler {

    boolean handle(HttpServletRequest req, HttpServletResponse res) throws IOException, ClientException;
}
