package com.vitrina.vitrinaVirtual.domain.dto;

import java.time.LocalDateTime;

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
public class StoreNotificationDto {

    private Long id;
    private Long storeId;
    private Long reservationItemId;

    private String message;
    private boolean read;
    private LocalDateTime createdAt;
}