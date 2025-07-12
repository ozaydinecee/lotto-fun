package com.project.lottofun.repository;

import com.project.lottofun.model.entity.Draw;
import com.project.lottofun.model.entity.Ticket;
import com.project.lottofun.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findAllByUser(User user);

    List<Ticket> findAllByDraw(Draw draw);
    List<Ticket> findAllByUserAndDraw(User user, Draw draw);

}
