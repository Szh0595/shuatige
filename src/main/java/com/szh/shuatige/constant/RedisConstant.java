package com.szh.shuatige.constant;

public interface RedisConstant {
    // 用户签到记录的Redis Key前缀
    public static final String USER_SIGN_IN_REDIS_KEY_PREFIX = "user:signins";

    /**
     * 获取用户签到记录的Redis Key
     * @param year
     * @param userId
     * @return
     */
    static String getUserSignInRedisKey(int year,long userId){
        return String.format("%s:%s:%s",USER_SIGN_IN_REDIS_KEY_PREFIX,year,userId);
    }


}
