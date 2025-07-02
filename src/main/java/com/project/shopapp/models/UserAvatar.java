package com.project.shopapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_avatars")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAvatar {
    public static final int MAXIMUM_IMAGES_PER_USER = 1;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(name = "image_url", length = 300)
    private String imageUrl;

}
