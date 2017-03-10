package net.limbomedia.dns.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.limbomedia.dns.ZoneManager;
import net.limbomedia.dns.model.Config;

public class UpdateServlet extends HttpServlet {
	
	private static final Logger L = LoggerFactory.getLogger(UpdateServlet.class);
	
	private static final long serialVersionUID = -6612011519927117470L;
	
	private ZoneManager zoneManager;
	private Config config;
	
	public UpdateServlet(Config config, ZoneManager zoneManager) {
		this.config = config;
		this.zoneManager = zoneManager;
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		processRequest(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		processRequest(req, resp);
	}
	
	private void processRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String[] split = req.getPathInfo().split("/");
		int parts = split.length;
		
		// Get remote address from request or header (reverse proxy configuration)
		String remoteAddress = null;
		
		if(config.getRemoteAddressHeader() != null && !config.getRemoteAddressHeader().isEmpty()) {
			String addrHeader = req.getHeader(config.getRemoteAddressHeader());
			if(addrHeader != null) {
				// multiple proxies may concat "client, proxy1, proxy2, proxy3, ...";
				String[] split2 = addrHeader.split(",");
				remoteAddress = split2[0].trim();
			}
		}
		
		// Fallback to classic remote address
		if(remoteAddress == null || remoteAddress.isEmpty()) {
			remoteAddress = req.getRemoteAddr();
		}
		
		
		String value = null;
		if(parts == 2) {
			// Detect IP
			value = remoteAddress;
		}
		else if (parts == 3) {
			// Use submitted IP
			value = split[2];
		}
		else {
			// invalid. Will result in a 404.
			return;
		}
		
		// Record identifier
		String recordID = split[1];
		
		resp.setContentType("text/plain");
		resp.setCharacterEncoding("UTF-8");
		
		PrintWriter w = resp.getWriter();
		try {
			zoneManager.recordUpdate(req.getRemoteAddr(), recordID,value);
			w.println("OK. New value: " + value);
			resp.setStatus(200);
		} catch (Exception e) {
			w.println("Fail. " + e.getMessage());
			resp.setStatus(500);
		}
		
	}
}
