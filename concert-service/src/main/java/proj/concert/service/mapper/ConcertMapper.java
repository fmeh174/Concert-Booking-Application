package proj.concert.service.mapper;

import proj.concert.common.dto.ConcertDTO;
import proj.concert.service.domain.Concert;

import java.time.LocalDateTime;
import java.util.*;

import static proj.concert.service.mapper.PerformerMapper.performerToDtos;
import static proj.concert.service.mapper.PerformerMapper.performersToDomain;

public class ConcertMapper {

    public static Concert toConcertDomainModel(ConcertDTO concertDTO) {
        Concert concert = new Concert(concertDTO.getId(),
                concertDTO.getTitle(),
                concertDTO.getImageName(),
                concertDTO.getBlurb());
        concert.setPerformers(performersToDomain(concertDTO.getPerformers()));
        Set<LocalDateTime> dates = new HashSet<>();
        for (LocalDateTime date : concertDTO.getDates()) {
            dates.add(date);
        }
        concert.setDates(dates);

        return concert;
    }

    public static ConcertDTO toConcertDto(Concert concert) {
        ConcertDTO concertDto = new ConcertDTO(concert.getId(),
                concert.getTitle(),
                concert.getImageName(),
                concert.getBlurb());
        concertDto.setPerformers(performerToDtos(concert.getPerformers()));
        List<LocalDateTime> dates = new ArrayList<LocalDateTime>();
        for (LocalDateTime date : concert.getDates()) {
            dates.add(date);
        }
        concertDto.setDates(dates);
        return concertDto;
    }
}
