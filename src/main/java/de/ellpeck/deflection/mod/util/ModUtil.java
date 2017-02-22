package de.ellpeck.deflection.mod.util;

import de.ellpeck.deflection.api.DeflectionAPI;

public final class ModUtil{

    public static final String MOD_ID = DeflectionAPI.MOD_ID;
    public static final String NAME = "Deflection";
    public static final String VERSION = "@VERSION@";

    private static final String PROXY_PATH = "de.ellpeck.deflection.mod.proxy.";
    public static final String CLIENT_PROXY = PROXY_PATH+"ClientProxy";
    public static final String SERVER_PROXY = PROXY_PATH+"ServerProxy";
}
