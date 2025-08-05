package proj.concert.service.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import proj.concert.common.dto.ConcertInfoSubscriptionDTO;
import proj.concert.common.jackson.LocalDateTimeDeserializer;
import proj.concert.common.jackson.LocalDateTimeSerializer;

import java.time.LocalDateTime;

public class ConcertSubscription {
    private Long concertId;
    private LocalDateTime date;
    private int percentageBooked;

    public ConcertSubscription() {}

    public ConcertSubscription(Long concertId, LocalDateTime date, int percentageBooked) {
        this.concertId = concertId;
        this.date = date;
        this.percentageBooked = percentageBooked;
    }

    public Long getConcertId() { return concertId; }
    public void setConcertId(Long concertId) { this.concertId = concertId; }
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime getDate() { return date; }
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public void setDate(LocalDateTime date) {this.date = date; }
    public int getPercentageBooked() { return percentageBooked; }
    public void setPercentageBooked(int percentageBooked) { this.percentageBooked = percentageBooked; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ConcertSubscription that = (ConcertSubscription) o;

        return new EqualsBuilder()
                .append(concertId, that.concertId)
                .append(percentageBooked, that.percentageBooked)
                .append(date, that.date)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(concertId)
                .append(date)
                .append(percentageBooked)
                .toHashCode();
    }
}
