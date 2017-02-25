package de.ellpeck.sparks.mod.util;

import de.ellpeck.sparks.api.SparksAPI;

public final class ModUtil{

    public static final String MOD_ID = SparksAPI.MOD_ID;
    public static final String NAME = "Sparks";
    public static final String VERSION = "@VERSION@";

    private static final String PROXY_PATH = "de.ellpeck."+MOD_ID+".mod.proxy.";
    public static final String CLIENT_PROXY = PROXY_PATH+"ClientProxy";
    public static final String SERVER_PROXY = PROXY_PATH+"ServerProxy";
}
