package com.cineplus.cineplus.domain.repository;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DailyTotalProjection {
    LocalDate getDay();
    BigDecimal getTotal();
}
