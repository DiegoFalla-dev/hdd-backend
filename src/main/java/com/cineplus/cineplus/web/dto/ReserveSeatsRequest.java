package com.cineplus.cineplus.web.dto;

import lombok.*;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class ReserveSeatsRequest {
    private Long showId;
    private List<Long> seatIds;
    private String holderId; // local user/session id
    private Integer holdSeconds = 300; // default 5min
}