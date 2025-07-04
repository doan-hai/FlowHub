package com.flowhub.business.utils;


import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

/**
 * @author haidv
 * @version 1.0
 */
public class Utils {

  private Utils() {
    throw new IllegalStateException("Utility class");
  }

  public static String makeLikeParameter(Object param) {
    return ObjectUtils.isEmpty(param) ? StringUtils.EMPTY : "%" + param + "%";
  }

  public static <E> Collection<E> makeInCollectionParameter(Collection<E> param) {
    return CollectionUtils.isEmpty(param) ? null : param;
  }
}
