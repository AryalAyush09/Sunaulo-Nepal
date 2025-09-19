package com.project.sunauloNepal.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class LocationDTO {
	private double longitude;    // fix spelling
    private double latitude;  
    private String fullAddress;
} 
