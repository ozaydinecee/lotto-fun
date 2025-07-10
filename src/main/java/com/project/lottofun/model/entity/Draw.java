package com.project.lottofun.model.entity;

import com.project.lottofun.model.enums.DrawStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Draw {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Integer drawNumber;

    @Column(nullable = false)
    private LocalDateTime drawDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DrawStatus status = DrawStatus.DRAW_OPEN;

    @ElementCollection
    private Set<Integer> winningNumbers;

    @OneToMany(mappedBy = "draw", cascade = CascadeType.ALL)
    private List<Ticket> tickets;
}