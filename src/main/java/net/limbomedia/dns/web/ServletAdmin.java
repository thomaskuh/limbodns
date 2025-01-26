package net.limbomedia.dns.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.limbomedia.dns.ZoneManager;
import net.limbomedia.dns.model.Config;
import net.limbomedia.dns.model.XRecord;
import net.limbomedia.dns.model.XZone;
import org.kuhlins.lib.webkit.HttpUtils;

public class ServletAdmin extends ServletGeneric {

    private static final long serialVersionUID = 1L;
    private Config config;
    private ZoneManager zoneManager;

    public ServletAdmin(Config config, ZoneManager zoneManager) {
        this.config = config;
        this.zoneManager = zoneManager;
        handlers.add(this::handleProbe);
        handlers.add(this::handleZoneGets);
        handlers.add(this::handleZoneGets);
        handlers.add(this::handleZoneGet);
        handlers.add(this::handleZoneDelete);
        handlers.add(this::handleZoneCreate);
        handlers.add(this::handleRecordGet);
        handlers.add(this::handleRecordCreate);
        handlers.add(this::handleRecordDelete);
        handlers.add(this::handleRecordUpdate);
    }

    static Pattern PATTERN_PROBE = Pattern.compile("^/probe$");

    static Pattern PATTERN_ZONE_GETS = Pattern.compile("^/zones$");
    static Pattern PATTERN_ZONE_CREATE = Pattern.compile("^/zone$");
    static Pattern PATTERN_ZONE_GET_UPDATE_DELETE = Pattern.compile("^/zone/([a-zA-Z0-9\\-\\.@]+)$");

    static Pattern PATTERN_RECORD_CREATE = Pattern.compile("^/zone/([a-zA-Z0-9\\-\\.@]+)/record$");
    static Pattern PATTERN_RECORD_GET_UPDATE_DELETE =
            Pattern.compile("^/zone/([a-zA-Z0-9\\-\\.@]+)/record/([a-zA-Z0-9\\-]+)$");

    private boolean handleProbe(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Matcher matcher = PATTERN_PROBE.matcher(path(req));
        if (!"GET".equals(req.getMethod()) || !matcher.matches()) {
            return false;
        }
        resp.setStatus(200);
        return true;
    }

    private boolean handleZoneGets(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Matcher matcher = PATTERN_ZONE_GETS.matcher(path(req));
        if (!"GET".equals(req.getMethod()) || !matcher.matches()) {
            return false;
        }
        mapper.writeValue(resp.getOutputStream(), zoneManager.zoneGets());
        return true;
    }

    private boolean handleZoneGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Matcher matcher = PATTERN_ZONE_GET_UPDATE_DELETE.matcher(path(req));
        if (!"GET".equals(req.getMethod()) || !matcher.matches()) {
            return false;
        }
        mapper.writeValue(resp.getOutputStream(), zoneManager.zoneGet(matcher.group(1)));
        return true;
    }

    private boolean handleZoneDelete(HttpServletRequest req, HttpServletResponse resp) {
        Matcher matcher = PATTERN_ZONE_GET_UPDATE_DELETE.matcher(path(req));
        if (!"DELETE".equals(req.getMethod()) || !matcher.matches()) {
            return false;
        }
        zoneManager.zoneDelete(HttpUtils.remoteAdr(req, config.getRemoteAddressHeader()), matcher.group(1));
        return true;
    }

    private boolean handleZoneCreate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Matcher matcher = PATTERN_ZONE_CREATE.matcher(path(req));
        if (!"POST".equals(req.getMethod()) || !matcher.matches()) {
            return false;
        }
        XZone zone = zoneManager.zoneCreate(
                HttpUtils.remoteAdr(req, config.getRemoteAddressHeader()),
                mapper.readValue(req.getInputStream(), XZone.class));
        mapper.writeValue(resp.getOutputStream(), zone);
        return true;
    }

    private boolean handleRecordGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Matcher matcher = PATTERN_RECORD_GET_UPDATE_DELETE.matcher(path(req));
        if (!"GET".equals(req.getMethod()) || !matcher.matches()) {
            return false;
        }
        XRecord record = zoneManager.recordGet(matcher.group(1), matcher.group(2));
        mapper.writeValue(resp.getOutputStream(), record);
        return true;
    }

    private boolean handleRecordCreate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Matcher matcher = PATTERN_RECORD_CREATE.matcher(path(req));
        if (!"POST".equals(req.getMethod()) || !matcher.matches()) {
            return false;
        }
        XRecord record = zoneManager.recordCreate(
                HttpUtils.remoteAdr(req, config.getRemoteAddressHeader()),
                matcher.group(1),
                mapper.readValue(req.getInputStream(), XRecord.class));
        mapper.writeValue(resp.getOutputStream(), record);
        return true;
    }

    private boolean handleRecordDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Matcher matcher = PATTERN_RECORD_GET_UPDATE_DELETE.matcher(path(req));
        if (!"DELETE".equals(req.getMethod()) || !matcher.matches()) {
            return false;
        }
        zoneManager.recordDelete(
                HttpUtils.remoteAdr(req, config.getRemoteAddressHeader()), matcher.group(1), matcher.group(2));
        return true;
    }

    private boolean handleRecordUpdate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Matcher matcher = PATTERN_RECORD_GET_UPDATE_DELETE.matcher(path(req));
        if (!"POST".equals(req.getMethod()) || !matcher.matches()) {
            return false;
        }
        XRecord record = zoneManager.recordUpdate(
                HttpUtils.remoteAdr(req, config.getRemoteAddressHeader()),
                matcher.group(1),
                matcher.group(2),
                mapper.readValue(req.getInputStream(), XRecord.class));
        mapper.writeValue(resp.getOutputStream(), record);
        return true;
    }
}
