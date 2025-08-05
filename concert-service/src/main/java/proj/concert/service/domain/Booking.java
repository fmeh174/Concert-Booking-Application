package proj.concert.service.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import proj.concert.common.jackson.LocalDateTimeDeserializer;
import proj.concert.common.jackson.LocalDateTimeSerializer;
import proj.concert.common.types.Genre;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long concertId;
    private LocalDateTime date;
    @OneToMany
    private Set<Seat> seats = new HashSet<>();
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    public Booking() {}

    public Booking(Long id, Long concertId, LocalDateTime date, Set<Seat> seats, User user) {
        this.id = id;
        this.concertId = concertId;
        this.date = date;
        this.seats = seats;
        this.user = user;
    }

    public Booking(Long concertId, LocalDateTime date, Set<Seat> seats, User user) {
        this(null, concertId, date, seats, user);
    }
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getConcertId() {
        return concertId;
    }

    public void setConcertId(long concertId) {
        this.concertId = concertId;
    }
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime getDate() {
        return date;
    }
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Set<Seat> getSeats() { return seats; }

    public void setSeats(Set<Seat> seats) { this.seats = seats; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String toString() {
        return ("Booking: " + this.concertId + this.date);
    }

}
