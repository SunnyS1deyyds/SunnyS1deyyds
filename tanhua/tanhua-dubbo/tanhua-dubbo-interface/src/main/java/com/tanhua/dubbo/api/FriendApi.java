package com.tanhua.dubbo.api;

import java.util.List;

public interface FriendApi {

    //添加好友功能
    void addContact(Long userId, Long friendId);

    //根据用户id查询所有的好友id
    List<Long> findFriends(Long userId);
}
