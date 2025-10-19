package com.cineplus.cineplus.persistance.repository;

import com.cineplus.cineplus.persistance.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.time.OffsetDateTime;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {
    List<Show> findByHall_Cinema_ExternalIdAndStartTimeBetween(String cinemaExternalId, OffsetDateTime from, OffsetDateTime to);
}