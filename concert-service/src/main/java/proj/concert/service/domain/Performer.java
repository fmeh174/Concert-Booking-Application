package proj.concert.service.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import proj.concert.common.types.Genre;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "Performers")
public class Performer {
    @Id
    private Long id;
    private String name;
    @Column(name = "image_name")
    private String imageName;
    @Enumerated(EnumType.STRING)
    private Genre genre;
    @Column(length = 1000)
    private String blurb;
    @ManyToMany(mappedBy = "performers")
    private Set<Concert> concerts;

    public Performer() {}

    public Performer(Long id, String name, String imageName, Genre genre, String blurb) {
        this.id = id;
        this.name = name;
        this.imageName = imageName;
        this.genre = genre;
        this.blurb = blurb;
    }

    public Performer(String name, String imageName, Genre genre, String blurb) {
        this(null, name, imageName, genre, blurb);
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }
    public Genre getGenre() { return genre; }
    public void setGenre(Genre genre) { this.genre = genre; }
    public String getBlurb() { return blurb; }
    public void setBlurb(String blurb) { this.blurb = blurb; }
    public Set<Concert> getConcerts() { return concerts; }
    public void addConcert(Concert concert) { concerts.add(concert); }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("Performer, id: ");
        buffer.append(id);
        buffer.append(", name: ");
        buffer.append(name);
        buffer.append(", s3 image: ");
        buffer.append(imageName);
        buffer.append(", genre: ");
        buffer.append(genre.toString());

        return buffer.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Performer))
            return false;
        if (obj == this)
            return true;

        Performer rhs = (Performer) obj;
        return new EqualsBuilder().
                append(name, rhs.name).
                isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(name).hashCode();
    }

}
