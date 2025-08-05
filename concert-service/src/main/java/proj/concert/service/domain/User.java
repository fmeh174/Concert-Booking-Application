package proj.concert.service.domain;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private Long version;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Booking> bookings = new HashSet<>();

    public User() {}
    public User(Long id, String username, String password, Long version) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.version = version;
    }
    public User(String username, String password, Long version) {
        this(null, username, password, version);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Long getVersion() {return version; }
    public void setVersion(Long version) { this.version = version; }
    public void addBooking(Booking booking) { this.bookings.add(booking); }
    public Set<Booking> getBookings() { return bookings; }
}
