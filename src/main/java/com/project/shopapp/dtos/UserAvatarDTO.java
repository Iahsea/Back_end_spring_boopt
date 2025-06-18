package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data // toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAvatarDTO {
    @JsonProperty("user_id")
    @Min(value = 1, message = "Product's ID must be > 0")
    private Long userId;

    @Size(min = 5, max = 200, message = "Avatar's name")
    @JsonProperty("image_url")
    private String imageUrl;
}
