package com.ditto;

/**
 * Common constants for the code.
 */
public abstract class Constants {
    private Constants() { /* no instances allowed */ }

    public static final String HEADER_DELAY = "X-Ditto-DelayResponseSeconds";
    public static final String HEADER_BODY_MATCH = "X-Ditto-BodyMatchWildcard";

}
