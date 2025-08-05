package proj.concert.service.mapper;

import proj.concert.common.dto.PerformerDTO;
import proj.concert.service.domain.Performer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PerformerMapper {

    public static Performer toPerformerDomainModel(PerformerDTO performerDto) {
        Performer performer = new Performer(performerDto.getId(),
                performerDto.getName(),
                performerDto.getImageName(),
                performerDto.getGenre(),
                performerDto.getBlurb());

        return performer;
    }

    public static PerformerDTO toPerformerDto(Performer performer) {
        PerformerDTO performerDto = new PerformerDTO(performer.getId(),
                performer.getName(),
                performer.getImageName(),
                performer.getGenre(),
                performer.getBlurb());

        return performerDto;
    }

    public static List<PerformerDTO> performerToDtos(Set<Performer> performers) {
        List<PerformerDTO> performersDto = new ArrayList<PerformerDTO>();
        for (Performer performer : performers) {
            performersDto.add(toPerformerDto(performer));
        }
        return performersDto;
    }

    public static Set<Performer> performersToDomain(List<PerformerDTO> performers) {
        Set<Performer> performerDomains = new HashSet<>();
        for (PerformerDTO performerDto : performers) {
            performerDomains.add(toPerformerDomainModel(performerDto));
        }
        return performerDomains;
    }
}
