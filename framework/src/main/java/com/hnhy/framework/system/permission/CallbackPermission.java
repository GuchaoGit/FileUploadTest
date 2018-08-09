package com.hnhy.framework.system.permission;

import java.util.List;

/**
 * Author: hardcattle
 * Time: 2018/4/2 下午3:43
 * Description:
 */
public interface CallbackPermission {
    void onGranted();

    void onDenied(List<String> permissions);
}
