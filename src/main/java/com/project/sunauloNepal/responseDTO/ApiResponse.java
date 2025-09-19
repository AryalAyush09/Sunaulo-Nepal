package com.project.sunauloNepal.responseDTO;


import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
}
