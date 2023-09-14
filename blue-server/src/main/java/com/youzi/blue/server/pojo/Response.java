package com.youzi.blue.server.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {
    public boolean result;
    public String message;
    public T data;

    public Response(T data) {
        this.result = true;
        this.message = "success";
        this.data = data;
    }

}
