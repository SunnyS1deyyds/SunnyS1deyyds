package com.tanhua.dubbo.api;

public interface UserLikeApi {

    //保存喜欢的数据  isLike 为true就是喜欢   为false就是不喜欢
    boolean saveLikeUser(Long userId, Long likeUserId, boolean isLike);
}
