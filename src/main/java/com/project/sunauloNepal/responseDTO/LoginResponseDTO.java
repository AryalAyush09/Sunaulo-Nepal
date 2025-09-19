package com.project.sunauloNepal.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class LoginResponseDTO {
  private String token;
  private UserResponseDTO user;
  private LocationDTO location;
}
