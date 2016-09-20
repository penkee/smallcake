package com.fastcloud.sys;

public class MyException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8956311497894106792L;
	
	public MyException(String _errCode,String _errMsg){
		this.errCode=_errCode;
		this.errMsg=_errMsg;
	}
	private String errCode;
	private String errMsg;
	
	public String getErrCode() {
		return errCode;
	}
	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}
	public String getErrMsg() {
		return errMsg;
	}
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
}
