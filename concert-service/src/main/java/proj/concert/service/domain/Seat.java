package proj.concert.service.domain;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import proj.concert.common.dto.SeatDTO;
import proj.concert.common.jackson.LocalDateTimeDeserializer;
import proj.concert.common.jackson.LocalDateTimeSerializer;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Seat{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
    private String label;
	private boolean isBooked;
	private LocalDateTime date;
	private BigDecimal price;

	public Seat(String label, boolean isBooked, LocalDateTime date, BigDecimal price) {
		this.label = label;
		this.isBooked = isBooked;
		this.date = date;
		this.price = price;
	}	
	
	public Seat() {}
	public String getLabel() { return label; }
	public void setLabel(String label) { this.label = label; }
	public boolean getBookingStatus() { return isBooked; }
	public void setBookingStatus(boolean isBooked) { this.isBooked = isBooked; }
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	public LocalDateTime getDate() { return date; }
	@JsonSerialize(using = LocalDateTimeSerializer.class)
	@JsonDeserialize(using = LocalDateTimeDeserializer.class)
	public void setDate(LocalDateTime date) { this.date = date; }
	public BigDecimal getCost() { return price; }
	public void setCost(BigDecimal cost) { this.price = price; }
	@Override
	public String toString() {
		return label;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;

		if (o == null || getClass() != o.getClass()) return false;

		Seat seat = (Seat) o;

		return new EqualsBuilder()
				.append(label, seat.label)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(label)
				.toHashCode();
	}
}
