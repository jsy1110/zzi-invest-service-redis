package zzipay.investservice.cache;

public class CacheKey {
    public static final int DEFAULT_EXPIRE_SEC = 60; // 1 minutes
    public static final String ITEM = "item";
    public static final int ITEM_EXPIRE_SEC = 60 * 3; // 3 minutes
    public static final String HISTORY = "orderHistory";
    public static final String SUMMARY = "orderSummary";
    public static final int ORDER_EXPIRE_SEC = 60 * 10; // 10 minutes
}