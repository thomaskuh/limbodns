package net.limbomedia.dns.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.limbomedia.dns.ZoneManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateServlet extends HttpServlet {
	
	private static final Logger L = LoggerFactory.getLogger(UpdateServlet.class);
	
	private static final long serialVersionUID = -6612011519927117470L;
	
	private ZoneManager zoneManager;
	
	public UpdateServlet(ZoneManager zoneManager) {
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
		
		String value = null;
		if(parts == 2) {
			// Detect IP
			value = req.getRemoteAddr();
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
