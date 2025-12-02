package com.vitrina.vitrinaVirtual.domain.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDto {

    private Long id;
    private Long clientId;
    private Long storeId;  // ✅ Agregado
    private LocalDateTime createdAt;
    private String status;
    private Double totalPrice;

    private List<ReservationItemDto> items;
}