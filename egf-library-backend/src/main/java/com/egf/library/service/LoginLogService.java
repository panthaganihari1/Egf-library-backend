package com.egf.library.service;

import com.egf.library.model.LoginLog;
import com.egf.library.repository.LoginLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LoginLogService {

    @Autowired
    private LoginLogRepository loginLogRepository;

    public void log(HttpServletRequest request, String username, String fullName, String status) {
        try {
            // 1. Get Real IP
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isBlank()) {
                ip = request.getRemoteAddr();
            } else {
                ip = ip.split(",")[0].trim();
            }

            // 2. Parse User-Agent
            String userAgent = request.getHeader("User-Agent");
            String deviceType = detectDevice(userAgent);
            String browser    = detectBrowser(userAgent);
            String os         = detectOS(userAgent);

            // 3. Save log
            LoginLog log = new LoginLog();
            log.setUsername(username);
            log.setFullName(fullName);
            log.setIpAddress(ip);
            log.setDeviceType(deviceType);
            log.setBrowser(browser);
            log.setOs(os);
            log.setStatus(status);

            loginLogRepository.save(log);

        } catch (Exception e) {
            System.err.println("⚠️ Login log error: " + e.getMessage());
        }
    }

    // ── Device Detection ───────────────────────────────
    private String detectDevice(String ua) {
        if (ua == null) return "Desktop";
        String u = ua.toLowerCase();
        if (u.contains("iphone") || u.contains("android") && u.contains("mobile"))
            return "Mobile";
        if (u.contains("ipad") || u.contains("tablet"))
            return "Tablet";
        return "Desktop";
    }

    // ── Browser Detection ──────────────────────────────
    private String detectBrowser(String ua) {
        if (ua == null) return "Unknown";
        if (ua.contains("Edg"))    return "Edge";
        if (ua.contains("Chrome")) return "Chrome";
        if (ua.contains("Firefox"))return "Firefox";
        if (ua.contains("Safari")) return "Safari";
        if (ua.contains("OPR"))    return "Opera";
        return "Unknown";
    }

    // ── OS Detection ───────────────────────────────────
    private String detectOS(String ua) {
        if (ua == null) return "Unknown";
        if (ua.contains("Windows NT 10.0")) return "Windows 10/11";
        if (ua.contains("Windows NT 6.1"))  return "Windows 7";
        if (ua.contains("Windows"))         return "Windows";
        if (ua.contains("Mac OS X"))        return "macOS";
        if (ua.contains("Android"))         return "Android";
        if (ua.contains("iPhone") || ua.contains("iPad")) return "iOS";
        if (ua.contains("Linux"))           return "Linux";
        return "Unknown";
    }
}