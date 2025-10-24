package com.pki.example.util;

import ua_parser.Client;
import ua_parser.Parser;

public class DeviceUtils {

    private static final Parser uaParser = new Parser();

    public static String getDeviceName(String userAgent) {
        if (userAgent == null || userAgent.isEmpty()) return "Nepoznat uređaj";

        Client client = uaParser.parse(userAgent);

        String browser = client.userAgent.family;      // npr. "Chrome"
        String os = client.os.family;                  // npr. "Windows"
        String osVersion = client.os.major != null ? client.os.major : "";

        return browser + " na " + os + " " + osVersion;
    }
}
