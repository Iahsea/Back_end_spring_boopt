package com.project.shopapp.dtos;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data // toString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class TopSellingProductDTO {
    @JsonProperty("product_id")
    private Long productId;

    @JsonProperty("total_sales")
    private Long totalSales;

}
