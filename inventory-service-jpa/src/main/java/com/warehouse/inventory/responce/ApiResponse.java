package com.warehouse.inventory.responce;

import java.time.LocalDateTime;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {

	private String status;
	private String message;
	private T data;
	private int statusCode;
	private LocalDateTime timestamp;
	
	public ApiResponse(Builder<T> builder) {
		super();
		this.status = builder.status;
		this.message = builder.message;
		this.data = builder.data;
		this.statusCode = builder.statusCode;
		this.timestamp = builder.timestamp;
	}
	
	private static <T> Builder<T> builder(){
		return new Builder<>();
	}
	
	public static <T> ApiResponse<T> of(String status, String message, T data, int statusCode, LocalDateTime timestamp){
		return ApiResponse.<T>builder()
				.setMessage(message)
				.setStatusCode(statusCode)
				.setTimestamp(timestamp)
				.setData(data)
				.setStatus(status).build();
	}
	
	public static <T> ApiResponse<T> success(String message, T data, int statusCode) {
		return of("Success", message, data, statusCode, LocalDateTime.now());
	}
	
	public static <T> ApiResponse<T> error(String message, T data, int statusCode) {
		return of("error", message, data, statusCode, LocalDateTime.now());
	}
	
	public static class Builder<T> {
		private String status;
		private String message;
		private T data;
		private int statusCode;
		private LocalDateTime timestamp;
		
		public Builder<T> setStatus(String status) {
			this.status = status;
			return this;
		}
		
		public Builder<T> setMessage(String message) {
			this.message = message;
			return this;
		}
		
		public Builder<T> setData(T data) {
			this.data = data;
			return this;
		}
		
		public Builder<T> setStatusCode(int statusCode) {
			this.statusCode = statusCode;
			return this;
		}
		
		public Builder<T> setTimestamp(LocalDateTime timestamp) {
			this.timestamp = timestamp;
			return this;
		}
		
		public ApiResponse<T> build() {
			return new ApiResponse<T>(this);
		}

		@Override
		public String toString() {
			return "Builder [status=" + status + ", message=" + message + ", data=" + data + ", statusCode="
					+ statusCode + ", timestamp=" + timestamp + "]";
		}
		
		
	}
}
