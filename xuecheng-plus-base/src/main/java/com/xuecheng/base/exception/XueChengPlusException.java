package com.xuecheng.base.exception;

public class XueChengPlusException extends RuntimeException{
    private String errMessage;

    public String getErrMessage() {
        return errMessage;
    }

    public XueChengPlusException() {
    }

    public XueChengPlusException(String errMessage) {
        super(errMessage);
        this.errMessage = errMessage;
    }

    public static void cast(String errMessage){
        throw new XueChengPlusException(errMessage);
    }

    public static void cast(CommonError commonError){
        throw new XueChengPlusException(commonError.getErrMessage());
    }


}
