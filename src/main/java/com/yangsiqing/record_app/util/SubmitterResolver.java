package com.yangsiqing.record_app.util;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 按请求的 Host（子域名）解析提交者：nfy / ysq。
 * 例如 nfy.nfysq.top → nfy，ysq.nfysq.top → ysq；同一 /mood 表单，谁从哪个域名打开就记成谁。
 * 优先 X-Forwarded-Host（反向代理/Cloudflare），否则 Host。
 */
public final class SubmitterResolver {

    private static final String SUBMITTER_NFY = "nfy";
    private static final String SUBMITTER_YSQ = "ysq";

    public static String resolve(HttpServletRequest request, List<String> hostsNfy, List<String> hostsYsq) {
        String host = request.getHeader("X-Forwarded-Host");
        if (host == null || host.isBlank()) {
            host = request.getHeader("Host");
        }
        String h = normalizeHost(host);
        if (h == null) {
            return SUBMITTER_YSQ;
        }
        for (String n : hostsNfy) {
            if (h.equals(normalizeHost(n))) {
                return SUBMITTER_NFY;
            }
        }
        for (String y : hostsYsq) {
            if (h.equals(normalizeHost(y))) {
                return SUBMITTER_YSQ;
            }
        }
        return SUBMITTER_YSQ;
    }

    public static List<String> parseHosts(String csv) {
        if (csv == null || csv.isBlank()) {
            return List.of();
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private static String normalizeHost(String host) {
        if (host == null || host.isBlank()) {
            return null;
        }
        String s = host.trim().toLowerCase();
        int i = s.indexOf(':');
        return i > 0 ? s.substring(0, i) : s;
    }
}
