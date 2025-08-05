package proj.concert.service.mapper;

import proj.concert.common.dto.ConcertSummaryDTO;
import proj.concert.service.domain.Concert;

public class ConcertSummaryMapper {

    public static ConcertSummaryDTO toConcertSummaryDTO(Concert concert) {
        ConcertSummaryDTO concertSummary = new ConcertSummaryDTO(concert.getId(),
                concert.getTitle(),
                concert.getImageName());
        return concertSummary;
    }
}
