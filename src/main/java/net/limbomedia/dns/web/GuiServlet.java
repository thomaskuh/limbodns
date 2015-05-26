package net.limbomedia.dns.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.limbomedia.dns.NotFoundException;
import net.limbomedia.dns.UpdateException;
import net.limbomedia.dns.ValidationException;
import net.limbomedia.dns.ZoneManager;
import net.limbomedia.dns.model.XType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;


public class GuiServlet extends HttpServlet {

	private static final Logger L = LoggerFactory.getLogger(GuiServlet.class);
	
	private static final long serialVersionUID = 5944920558918363076L;

	private ObjectMapper mapper = new ObjectMapper();
	
	private ZoneManager zoneManager;
	
	public GuiServlet(ZoneManager zoneManager) {
		this.zoneManager = zoneManager;
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
		
		try {
			if("/zoneCreate".equals(pathInfo)) {
				zoneManager.zoneCreate(req.getRemoteAddr(), req.getParameter("name"), req.getParameter("nameserver"));
			}
			if("/zoneDelete".equals(pathInfo)) {
				zoneManager.zoneDelete(req.getRemoteAddr(), req.getParameter("name"));
			}
			if("/recordDelete".equals(pathInfo)) {
				zoneManager.recordDelete(req.getRemoteAddr(), req.getParameter("id"));
			}
			if("/recordCreate".equals(pathInfo)) {
				zoneManager.recordCreate(req.getRemoteAddr(), req.getParameter("zonename"), req.getParameter("name"), XType.valueOf(req.getParameter("type")), req.getParameter("value"));
			}
			if("/recordUpdate".equals(pathInfo)) {
				zoneManager.recordUpdate(req.getRemoteAddr(), req.getParameter("id"), req.getParameter("value"));
			}
		} catch (NotFoundException | UpdateException | ValidationException e) {
			resp.setContentType("text/plain");
			resp.setCharacterEncoding("UTF-8");
			resp.setStatus(500);
			resp.getWriter().println(e.getMessage());
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		L.info("Incoming request: " +req.getRequestURI());
		String pathInfo = req.getPathInfo();
		
		if("/zones".equals(pathInfo)) {
			mapper.writeValue(resp.getOutputStream(), zoneManager.getXZones());
		}
	}
	
}
