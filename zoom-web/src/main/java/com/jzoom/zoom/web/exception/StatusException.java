package com.jzoom.zoom.web.exception;


public class StatusException extends Exception implements RestException {

	/**
	 * 429 请求过多被限制
	 * 
	 * @author jzoom
	 *
	 */
	public static class TooManyRequestsException extends StatusException {

		public TooManyRequestsException() {
			super(429);
			// TODO Auto-generated constructor stub
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 386179606607321203L;

	}

	/**
	 * 401 未授权
	 * 
	 * @author jzoom
	 *
	 */
	public static class UnAuthException extends StatusException {

		public UnAuthException() {
			super(401);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 8498120634675992738L;

	}

	/**
	 * 501 接口未实现
	 * 
	 * @author jzoom
	 *
	 */
	public static class UnImplementedException extends StatusException {

		public UnImplementedException() {
			super(501);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 2525119368785546027L;

	}

	/**
	 * 500 系统内部错误
	 * 
	 * @author jzoom
	 *
	 */
	public static class ServerException extends StatusException {

		public ServerException(String code, String error) {
			super(500, code, error);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 6628601169560404899L;

	}

	/**
	 * 400 参数列表错误(缺少，格式不匹配)
	 * 
	 * @author jzoom
	 *
	 */
	public static class BadRequest extends StatusException {

		public BadRequest(String code, String error) {
			super(400, code, error);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 8730845662683886569L;

	}

	/**
	 * 404 资源未找到,
	 * 
	 * @author jzoom
	 *
	 */
	public static class NotFoundException extends StatusException {

		public NotFoundException() {
			super(404, "", "");
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = -1764963629116223554L;

	}

	/**
	 * 405 不允许的http方法
	 * 
	 * @author jzoom
	 *
	 */
	public static class NotAllowedHttpMethodException extends StatusException {

		public NotAllowedHttpMethodException() {
			super(405, NotAllowedHttpMethodException.class.getName(), "");
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = -2904678227339056623L;

	}

	/**
	 * 403 访问受限，授权过期
	 * 
	 * @author jzoom
	 *
	 */
	public static class AuthException extends StatusException {

		
		
		public AuthException( ) {
			super(403);
		}

		/**
		 * 
		 */
		private static final long serialVersionUID = 6393248758153035129L;

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8461275141232305035L;

	private int status;
	private String code;
	private String error;
	public StatusException(int status) {
		this.status = status;
	}
	public StatusException(int status, String code) {
		this.status = status;
		this.code = code;
	}
	public StatusException(int status, String code, String error) {
		this.status = status;
		this.code = code;
		this.error = error;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
