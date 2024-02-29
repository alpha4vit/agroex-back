package com.vention.agroex.util.mapper;

import com.vention.agroex.dto.Bet;
import com.vention.agroex.entity.BetEntity;
import com.vention.agroex.model.BetRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BetMapper {

    BetEntity requestToEntity(BetRequest betRequest);

    BetEntity DTOToEntity(Bet bet);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "lotId", source = "lot.id")
    Bet toDTO(BetEntity betEntity);

    List<BetEntity> toEntities(List<Bet> dtos);

    List<Bet> toDTOs(List<BetEntity> bets);
}
