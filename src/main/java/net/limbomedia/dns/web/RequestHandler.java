package net.limbomedia.dns.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kuhlins.webkit.ex.ClientException;

@FunctionalInterface
public interface RequestHandler {
  
  boolean handle(HttpServletRequest req, HttpServletResponse res) throws IOException, ClientException;
  
}
