package com.flowhub.business.utils;

/**
 * @author haidv
 * @version 1.0
 */
public class Authority {

  public static final String ROLE_SYSTEM = "hasAuthority('ROLE_SYSTEM')";

  private Authority() {
    throw new IllegalStateException("Utility class");
  }
}
