package proj.concert.service.domain;

import java.time.LocalDateTime;
import java.util.*;

import javax.persistence.*;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.FetchMode;
import org.hibernate.engine.spi.CascadeStyle;
import proj.concert.common.jackson.LocalDateTimeDeserializer;
import proj.concert.common.jackson.LocalDateTimeSerializer;
import proj.concert.common.types.Genre;

@Entity
@Table(name = "CONCERTS")
public class Concert implements Comparable<Concert> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    @Column(name = "IMAGE_NAME")
    private String imageName;
    @Column(length = 750)
    private String blurb;
    @ElementCollection
    @Column(name = "date")
    private Set<LocalDateTime> dates = new HashSet<>();

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "concert_performer",
    joinColumns = @JoinColumn(name = "concert_id"),
    inverseJoinColumns = @JoinColumn(name = "performer_id"))
    private Set<Performer> performers = new HashSet<>();

    public Concert(Long id, String title, String imageName, String blurb) {
        this.id = id;
        this.title = title;
        this.imageName = imageName;
        this.blurb = blurb;
    }

    public Concert(String title, String imageName, String blurb) {
        this(null, title, imageName, blurb);
    }

    public Concert(){}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }
    public String getBlurb() { return blurb; }
    public void setBlurb(String blurb) { this.blurb = blurb; }
    public Set<Performer> getPerformers() { return this.performers; }
    public void setPerformers(Set<Performer> performers) { this.performers = performers; }
    @JsonSerialize(contentUsing = LocalDateTimeSerializer.class)
    @JsonDeserialize(contentUsing = LocalDateTimeDeserializer.class)
    public Set<LocalDateTime> getDates() { return dates; }
    @JsonSerialize(contentUsing = LocalDateTimeSerializer.class)
    @JsonDeserialize(contentUsing = LocalDateTimeDeserializer.class)
    public void setDates(Set<LocalDateTime> dates) { this.dates = dates; }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Concert, id: ");
        buffer.append(id);
        buffer.append(", title: ");
        buffer.append(title);

        return buffer.toString();
    }

    @Override
    public boolean equals(Object obj) {
        // Implement value-equality based on a Concert's title alone. ID isn't
        // included in the equality check because two Concert objects could
        // represent the same real-world Concert, where one is stored in the
        // database (and therefore has an ID - a primary key) and the other
        // doesn't (it exists only in memory).
        if (!(obj instanceof Concert))
            return false;
        if (obj == this)
            return true;

        Concert rhs = (Concert) obj;
        return new EqualsBuilder().
                append(title, rhs.title).
                isEquals();
    }

    @Override
    public int hashCode() {
        // Hash-code value is derived from the value of the title field. It's
        // good practice for the hash code to be generated based on a value
        // that doesn't change.
        return new HashCodeBuilder(17, 31).
                append(title).hashCode();
    }

    @Override
    public int compareTo(Concert concert) {
        return title.compareTo(concert.getTitle());
    }

}