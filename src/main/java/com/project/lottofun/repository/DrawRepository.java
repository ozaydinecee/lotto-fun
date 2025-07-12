package com.project.lottofun.repository;

import com.project.lottofun.model.entity.Draw;
import com.project.lottofun.model.enums.DrawStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DrawRepository extends JpaRepository<Draw, Long> {

    List<Draw> findAllByOrderByDrawDateDesc();
    Optional<Draw> findByDrawNumber(Integer drawNumber);

    List<Draw> findByStatusInOrderByDrawDateDesc(List<DrawStatus> statuses);

    Optional<Draw> findTopByStatusOrderByDrawDateAsc(DrawStatus status);


    Optional<Draw> findFirstByDrawDateBeforeAndStatus(LocalDateTime now, DrawStatus drawStatus);
}
