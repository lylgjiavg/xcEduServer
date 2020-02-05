package com.xuecheng.framework.exception;

import com.xuecheng.framework.model.response.ResultCode;

/**
 * @Classname CustomExceptionCast
 * @Description 自定义异常抛出类
 * @Date 2020/2/2 10:46
 * @Created by 姜立成
 */
public class CustomExceptionCast {

    /**
     * 静态方法: 抛出指定错误代码的自定义异常
     * @param resultCode 错误代码
     */
    public static void cast(ResultCode resultCode) {
        throw new CustomException(resultCode);
    }

}
