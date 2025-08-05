package proj.concert.service.services;

import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import proj.concert.common.dto.*;
import proj.concert.common.types.BookingStatus;
import proj.concert.service.domain.*;


import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.NewCookie;
import java.awt.print.Book;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import java.util.concurrent.CopyOnWriteArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;


import static proj.concert.service.mapper.ConcertMapper.toConcertDomainModel;
import static proj.concert.service.mapper.ConcertMapper.toConcertDto;
import static proj.concert.service.mapper.PerformerMapper.toPerformerDomainModel;
import static proj.concert.service.mapper.PerformerMapper.toPerformerDto;
import static proj.concert.service.mapper.ConcertSummaryMapper.toConcertSummaryDTO;

@Path("/concert-service")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class ConcertResource {

    private static Logger LOGGER = LoggerFactory.getLogger(ConcertResource.class);

    private static class SubscriptionHolder {
        final ConcertSubscription subscription;
        final AsyncResponse asyncResponse;
        final String username;
        SubscriptionHolder(ConcertSubscription subscription, AsyncResponse asyncResponse, String username) {
            this.subscription = subscription;
            this.asyncResponse = asyncResponse;
            this.username = username;
        }
    }
    private static final List<SubscriptionHolder> subscriptions = new CopyOnWriteArrayList<>();

    /**
     * Retrieves a concert by its ID, including its performers (eagerly loaded) and dates (initialized).
     * If the concert does not exist, responds with NOT_FOUND.
     * Returns a ConcertDTO as a JSON response.
     */
    @GET
    @Path("/concerts/{id}")
    public Response retrieveConcert(@PathParam("id") long id) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            TypedQuery<Concert> query = em.createQuery(
                    "SELECT c FROM Concert c LEFT JOIN FETCH c.performers LEFT JOIN FETCH c.dates WHERE c.id = :id", Concert.class);
            query.setParameter("id", id);
            List<Concert> concerts = query.getResultList();

            if (concerts.isEmpty()) {
                em.close();
                return Response.status(Response.Status.NOT_FOUND).build();
            }

            Concert concert = concerts.get(0);
            ConcertDTO concertDTO = toConcertDto(concert);
            return Response.ok(concertDTO).build();
        } finally {
            em.close();
        }
    }

    /**
     * Retrieves all concerts from the database, including their associated performers and dates.
     * Uses a single optimized query with LEFT JOIN FETCH to avoid the N+1 select problem and improve performance.
     * Returns a list of ConcertDTOs as a JSON response.
     */
    @GET
    @Path("/concerts")
    public Response getAllConcerts() {
        List<ConcertDTO> concertDTOS = new ArrayList<>();
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            // Use LEFT JOIN FETCH to load performers and dates for each concert in a single query
            // DISTINCT is used to avoid duplicate Concerts in the result due to the joins
            TypedQuery<Concert> concertQuery = em.createQuery(
                "SELECT DISTINCT c FROM Concert c LEFT JOIN FETCH c.performers LEFT JOIN FETCH c.dates", Concert.class);
            List<Concert> concerts = concertQuery.getResultList();
            for (Concert c : concerts) {
                concertDTOS.add(toConcertDto(c));
            }
        } finally {
            em.close();
        }
        return Response.ok(concertDTOS).build();
    }

    /**
     * Retrieves summary information for all concerts, including their performers and dates.
     * Uses a JOIN FETCH query to efficiently load concerts, performers, and dates in a single call.
     * Returns a list of ConcertSummaryDTOs as a JSON response.
     */
    @GET
    @Path("/concerts/summaries")
    public Response getSummaries() {
        List<ConcertSummaryDTO> concertSummaryDTOS = new ArrayList<>();
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            // Use JOIN FETCH to eagerly load performers and dates for each concert in a single query
            TypedQuery<Concert> concertQuery = em.createQuery(
                "SELECT DISTINCT c FROM Concert c LEFT JOIN FETCH c.performers LEFT JOIN FETCH c.dates", Concert.class);
            List<Concert> concerts = concertQuery.getResultList();
            for (Concert c : concerts) {
                concertSummaryDTOS.add(toConcertSummaryDTO(c));
            }
        } finally {
            em.close();
        }
        return Response.ok(concertSummaryDTOS).build();
    }

    /**
     * Retrieves a performer by their ID, including all concerts they are associated with.
     * Uses a JOIN FETCH query to efficiently load the performer and their concerts in a single call.
     * If the performer does not exist, responds with NOT_FOUND.
     * Returns a PerformerDTO as a JSON response.
     */
    @GET
    @Path("/performers/{id}")
    public Response getPerformer(@PathParam("id") Long id) {
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            // Use JOIN FETCH to eagerly load the performer and their concerts in a single query
            TypedQuery<Performer> query = em.createQuery(
                "SELECT DISTINCT p FROM Performer p WHERE p.id = :id", Performer.class);
            query.setParameter("id", id);
            List<Performer> performers = query.getResultList();
            Performer performer = performers.isEmpty() ? null : performers.get(0);
            if (performer == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            PerformerDTO performerDTO = toPerformerDto(performer);
            return Response.ok(performerDTO).build();
        } finally {
            em.close();
        }
    }

    /**
     * Retrieves all performers from the database, including all concerts they are associated with.
     * Uses a JOIN FETCH query to efficiently load performers and their concerts in a single call.
     * Returns a list of PerformerDTOs as a JSON response.
     */
    @GET
    @Path("/performers")
    public Response getAllPerformers() {
        List<PerformerDTO> performerDTOS = new ArrayList<>();
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            // Use JOIN FETCH to eagerly load all performers and their concerts in a single query
            TypedQuery<Performer> performerQuery = em.createQuery(
                "SELECT DISTINCT p FROM Performer p", Performer.class);
            List<Performer> performers = performerQuery.getResultList();
            for (Performer p : performers) {
                performerDTOS.add(toPerformerDto(p));
            }
        } finally {
            em.close();
        }
        return Response.ok(performerDTOS).build();
    }

    /**
     * Handles user login by verifying credentials against the database.
     * 
     * - Accepts a UserDTO containing username and password.
     * - Checks if a user with the given username exists.
     * - If the user exists and the password matches, returns a successful response with an authentication cookie.
     * - If the user does not exist or the password is incorrect, returns an unauthorized response.
     * - Ensures the EntityManager is always closed after the operation.
     */
    @POST
    @Path("/login")
    public Response login(UserDTO userCreds) {
        // Create a new EntityManager to interact with the database
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            // Query the database for a user with the provided username
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", userCreds.getUsername());
            User user = query.getSingleResult();
            // Check if the provided password matches the user's password in the database
            if (user.getPassword().equals(userCreds.getPassword())) {
                // If credentials are correct, create an authentication cookie
                NewCookie authCookie = new NewCookie("auth", user.getUsername());
                // Return a successful response with the authentication cookie
                return Response.ok().cookie(authCookie).build();
            } else {
                // If the password does not match, return an unauthorized response
                return Response.status(Response.Status.UNAUTHORIZED).build();
            }
        } catch (NoResultException e) {
            // If no user is found with the provided username, return an unauthorized response
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } finally {
            em.close();
        }
    }

    /**
     * Subscribes a user to concert info notifications for a specific concert and date.
     * Uses a JOIN FETCH query to efficiently load the concert and its dates in a single call.
     * If the concert or date is invalid, responds with BAD_REQUEST.
     * Otherwise, adds the subscription for later notification.
     */
    @POST
    @Path("/subscribe/concertInfo")
    @Consumes(MediaType.APPLICATION_JSON)
    public void subscribeConcertInfo(ConcertInfoSubscriptionDTO subDto,
                                     @CookieParam("auth") Cookie cookie,
                                     @Suspended AsyncResponse asyncResponse){
        if (cookie == null) {
            asyncResponse.resume(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }

        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            // Use JOIN FETCH to eagerly load the concert and its dates in a single query
            TypedQuery<Concert> query = em.createQuery(
                "SELECT c FROM Concert c LEFT JOIN FETCH c.dates WHERE c.id = :id", Concert.class);
            query.setParameter("id", subDto.getConcertId());
            List<Concert> concerts = query.getResultList();
            Concert concert = concerts.isEmpty() ? null : concerts.get(0);
            // Check if the concert exists and the requested date is valid for this concert
            boolean dateMatch = concert != null && concert.getDates().stream()
                    .anyMatch(d -> d.isEqual(subDto.getDate()));
            if (concert == null || !dateMatch) {
                asyncResponse.resume(Response.status(Response.Status.BAD_REQUEST).build());
                return;
            }
            // Add the subscription for later notification
            ConcertSubscription subscription = new ConcertSubscription(
                    subDto.getConcertId(), subDto.getDate(), subDto.getPercentageBooked()
            );
            subscriptions.add(new SubscriptionHolder(subscription, asyncResponse, cookie.getValue()));
        } finally {
            em.close();
        }
    }

    /**
     * Attempts to book seats for a concert on a given date for the authenticated user.
     * Validates the concert and requested seats, books the seats, creates a booking, and notifies subscriptions.
     * Rolls back the transaction and returns appropriate HTTP status if any step fails.
     * Returns 201 Created with the booking URI if successful.
     */
    @POST
    @Path("/bookings")
    public Response attemptBooking(BookingRequestDTO bookingReqDto, @CookieParam("auth") Cookie cookie) {
        // Check for authentication
        if (cookie == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            em.getTransaction().begin();
            // Validate the concert and date, rollback if transaction fails
            if (!isValidConcertAndDate(em, bookingReqDto)) {
                em.getTransaction().rollback();
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            // Check seat availability and book, rollback if transaction fails/all seats are not available
            Set<Seat> seats = findAndBookAvailableSeats(em, bookingReqDto);
            if (seats == null) {
                em.getTransaction().rollback();
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            // Retrieve the user from the database
            User user = findUserByCookie(em, cookie);
            // Create and persist the booking, commit transaction
            Booking booking = createAndPersistBooking(em, bookingReqDto, seats, user);
            // Notify any subscriptions that may be affected by this booking
            notifyRelevantSubscriptions(bookingReqDto.getConcertId(), bookingReqDto.getDate(), em);
            // Build the URI for the created booking
            URI createdURI = URI.create("concert-service/bookings/" + booking.getId());
            return Response.created(createdURI).build();
        } finally {
            em.close();
        }
    }

    /**
     * Retrieves a booking by its ID, including all associated seats.
     * Uses a JOIN FETCH query to efficiently load the booking and its seats in a single call.
     * If the booking does not exist or does not belong to the authenticated user, responds with FORBIDDEN.
     * Returns a BookingDTO as a JSON response.
     */
    @GET
    @Path("/bookings/{id}")
    public Response getBooking(@PathParam("id") long id, @CookieParam("auth") Cookie cookie) {
        if (cookie == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            // Use JOIN FETCH to eagerly load the booking and its seats in a single query
            TypedQuery<Booking> query = em.createQuery(
                "SELECT b FROM Booking b LEFT JOIN FETCH b.seats WHERE b.id = :id", Booking.class);
            query.setParameter("id", id);
            List<Booking> bookings = query.getResultList();
            Booking booking = bookings.isEmpty() ? null : bookings.get(0);
            // Check if the booking exists and belongs to the authenticated user
            if (booking == null || !cookie.getValue().equals(booking.getUser().getUsername())) {
                return Response.status(Response.Status.FORBIDDEN).build();
            }
            // Convert the seats to DTOs for the response
            List<SeatDTO> seats = new ArrayList<>();
            for (Seat s : booking.getSeats()) {
                SeatDTO sDTO = new SeatDTO(s.getLabel(), s.getCost());
                seats.add(sDTO);
            }
            // Build and return the BookingDTO as a JSON response
            BookingDTO bookingDto = new BookingDTO(booking.getConcertId(), booking.getDate(), seats);
            return Response.ok(bookingDto).build();
        } finally {
            em.close();
        }
    }

    /**
     * Retrieves all bookings for the authenticated user, including all associated seats.
     * Uses a JOIN FETCH query to efficiently load bookings and their seats in a single call.
     * Returns a list of BookingDTOs as a JSON response.
     */
    @GET
    @Path("/bookings")
    public Response getAllBookings(@CookieParam("auth") Cookie cookie) {
        if (cookie == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            // Use JOIN FETCH to eagerly load all bookings and their seats for the user in a single query
            TypedQuery<Booking> query = em.createQuery(
                "SELECT DISTINCT b FROM Booking b LEFT JOIN FETCH b.seats WHERE b.user.username = :username", Booking.class);
            query.setParameter("username", cookie.getValue());
            List<Booking> bookings = query.getResultList();
            List<BookingDTO> bookingDTOS = new ArrayList<>();
            for (Booking b : bookings) {
                // Convert the seats to DTOs for the response
                List<SeatDTO> seats = new ArrayList<>();
                for (Seat s : b.getSeats()) {
                    SeatDTO sDTO = new SeatDTO(s.getLabel(), s.getCost());
                    seats.add(sDTO);
                }
                // Build the BookingDTO and add to the result list
                BookingDTO bookingDto = new BookingDTO(b.getConcertId(), b.getDate(), seats);
                bookingDTOS.add(bookingDto);
            }
            return Response.ok(bookingDTOS).build();
        } finally {
            em.close();
        }
    }

    /**
     * Retrieves seats for a given concert date, filtered by booking status if specified.
     * If status is 'Any', returns all seats for the date.
     * If status is 'Booked' or 'Unbooked', returns only seats matching that booking status.
     * Returns a list of SeatDTOs as a JSON response.
     */
    @GET
    @Path("/seats/{date}")
    public Response getSeatsForDate(@PathParam("date") String dateStr,
                                    @QueryParam("status") String status) {
        LocalDateTime date;
        try {
            // Parse the date string from the path parameter
            date = LocalDateTime.parse(dateStr);
        } catch (DateTimeParseException e) {
            // If the date is invalid, return a 400 Bad Request
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {
            // If status is 'Any', return all seats for the date
            if (BookingStatus.valueOf(status).equals(BookingStatus.Any)) {
                TypedQuery<Seat> query = em.createQuery(
                        "SELECT s FROM Seat s WHERE s.date = :date", Seat.class);
                query.setParameter("date", date);
                List<SeatDTO> results = query.getResultList().stream()
                        .map(seat -> new SeatDTO(seat.getLabel(), seat.getCost()))
                        .collect(Collectors.toList());

                return Response.ok(results).build();
            } else {
                // Otherwise, filter by booking status (Booked or Unbooked)
                boolean fetchBooked = "Booked".equalsIgnoreCase(status);
                TypedQuery<Seat> query = em.createQuery(
                        "SELECT s FROM Seat s WHERE s.date = :date AND s.isBooked = :booked", Seat.class);
                query.setParameter("date", date);
                query.setParameter("booked", fetchBooked);

                List<SeatDTO> results = query.getResultList().stream()
                        .map(seat -> new SeatDTO(seat.getLabel(), seat.getCost()))
                        .collect(Collectors.toList());

                return Response.ok(results).build();
            }
        } finally {
            em.close();
        }
    }

    // =====================
    // Helper Methods
    // =====================

    /**
     * Checks if the concert and date in the booking request are valid.
     * @param em EntityManager for DB access
     * @param bookingReqDto Booking request DTO
     * @return true if the concert exists and the date is valid for the concert, false otherwise
     */
    private boolean isValidConcertAndDate(EntityManager em, BookingRequestDTO bookingReqDto) {
        Concert concert = em.find(Concert.class, bookingReqDto.getConcertId());
        return concert != null && concert.getDates().contains(bookingReqDto.getDate());
    }

    /**
     * Retrieves available seats for the given booking request.
     * Returns null if any requested seat is already booked or does not exist.
     * @param em EntityManager for DB access
     * @param bookingReqDto Booking request DTO
     * @return Set of available Seat entities, or null if any seat is unavailable
     */
    private Set<Seat> findAndBookAvailableSeats(EntityManager em, BookingRequestDTO bookingReqDto) {
        Set<Seat> seats = new HashSet<>();
        for (String seatLabel : bookingReqDto.getSeatLabels()) {
            try {
                TypedQuery<Seat> query = em.createQuery(
                        "SELECT s FROM Seat s WHERE s.label = :label AND s.date = :date", Seat.class);
                query.setParameter("label", seatLabel);
                query.setParameter("date", bookingReqDto.getDate());
                Seat seat = query.getSingleResult();
                if (seat.getBookingStatus()) {
                    return null; // Seat already booked
                }
                seat.setBookingStatus(true);
                em.merge(seat);
                seats.add(seat);
            } catch (NoResultException e) {
                return null; // Seat does not exist
            }
        }
        return seats;
    }

    /**
     * Retrieves a User entity from the database using the username from the auth cookie.
     * @param em EntityManager for DB access
     * @param cookie Auth cookie containing the username
     * @return User entity, or null if not found
     */
    private User findUserByCookie(EntityManager em, Cookie cookie) {
        try {
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", cookie.getValue());
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Creates and persists a new Booking entity, commits the transaction, and returns the booking.
     * @param em EntityManager for DB access
     * @param bookingReqDto Booking request DTO
     * @param seats Set of booked Seat entities
     * @param user User entity making the booking
     * @return The persisted Booking entity
     */
    private Booking createAndPersistBooking(EntityManager em, BookingRequestDTO bookingReqDto, Set<Seat> seats, User user) {
        Booking booking = new Booking(bookingReqDto.getConcertId(), bookingReqDto.getDate(), seats, user);
        user.addBooking(booking);
        em.getTransaction().commit();
        return booking;
    }

    /**
     * Notifies all relevant subscriptions if the percentage of booked seats for a concert/date meets or exceeds the threshold.
     * @param concertId Concert ID
     * @param date Concert date
     * @param em EntityManager for DB access
     */
    private void notifyRelevantSubscriptions(long concertId, LocalDateTime date, EntityManager em) {
        TypedQuery<Seat> allSeatsQuery = em.createQuery(
                "SELECT s FROM Seat s WHERE s.date = :date", Seat.class);
        allSeatsQuery.setParameter("date", date);
        List<Seat> allSeats = allSeatsQuery.getResultList();

        int totalSeats = allSeats.size();
        int availableSeats = (int) allSeats.stream().filter(s -> !s.getBookingStatus()).count();
        int percentBooked = totalSeats == 0 ? 0 : (int) (((totalSeats - availableSeats) * 100.0) / totalSeats);

        for (SubscriptionHolder holder : new ArrayList<>(subscriptions)) {
            ConcertSubscription sub = holder.subscription;
            if (sub.getConcertId() == concertId
                    && sub.getDate().equals(date)
                    && percentBooked >= sub.getPercentageBooked()) {
                holder.asyncResponse.resume(new ConcertInfoNotificationDTO(availableSeats));
                subscriptions.remove(holder);
            }
        }
    }
}
