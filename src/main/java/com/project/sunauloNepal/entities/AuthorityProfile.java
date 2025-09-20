package com.project.sunauloNepal.entities;

import com.project.sunauloNepal.ENUM.AuthorityType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.List;

@Entity
@Table(name = "authority_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorityProfile {

	@Id
	@Column(name = "user_id")
	private Long id;

    @OneToOne
    @MapsId
    private User user;

    @Enumerated(EnumType.STRING)
    private AuthorityType authorityType;

    private String fullName;
    private String photo;
    private String citizenshipFrontImage;
    private String citizenshipBackImage;
    private String authorityIdentityCardImage;
    private String phoneNumber;

    private Double latitude;
    private Double longitude;
    private String fullAddress;

    @Type(JsonBinaryType.class) // Hibernate 6+ correct usage
    @Column(columnDefinition = "jsonb")
    private List<String> deviceTokens;
    
    public AuthorityProfile(Long id) {
        this.id = id;
    }
}
