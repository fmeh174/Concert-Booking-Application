package proj.concert.service.domain;

public class ConcertInfoNotification {
    private int numSeatsRemaining;

    public ConcertInfoNotification() {}

    public ConcertInfoNotification(int numSeatsRemaining) { this.numSeatsRemaining = numSeatsRemaining; }

    public int getNumSeatsRemaining() { return numSeatsRemaining; }

    public void setNumSeatsRemaining(int numSeatsRemaining) { this.numSeatsRemaining = numSeatsRemaining; }
}
