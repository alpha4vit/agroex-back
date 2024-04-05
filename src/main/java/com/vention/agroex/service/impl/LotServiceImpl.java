package com.vention.agroex.service.impl;

import com.vention.agroex.dto.Notification;
import com.vention.agroex.entity.BetEntity;
import com.vention.agroex.entity.LotEntity;
import com.vention.agroex.entity.ProductCategoryEntity;
import com.vention.agroex.exception.ImageLotException;
import com.vention.agroex.exception.InvalidArgumentException;
import com.vention.agroex.exception.InvalidBetException;
import com.vention.agroex.exception.LotEditException;
import com.vention.agroex.filter.FilterService;
import com.vention.agroex.model.LotStatusResponse;
import com.vention.agroex.repository.CurrencyRateRepository;
import com.vention.agroex.repository.LocationRepository;
import com.vention.agroex.repository.LotRepository;
import com.vention.agroex.service.*;
import com.vention.agroex.util.constant.LotTypeConstants;
import com.vention.agroex.util.constant.NotificationReadStatusConstants;
import com.vention.agroex.util.constant.Role;
import com.vention.agroex.util.constant.StatusConstants;
import com.vention.agroex.util.mapper.LotMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LotServiceImpl implements LotService {

    private final LotMapper lotMapper;
    private final TagService tagService;
    private final UserService userService;
    private final ImageService imageService;
    private final FilterService filterService;
    private final LotRepository lotRepository;
    private final CountryService countryService;
    private final NotificationService notificationService;
    private final ProductCategoryService productCategoryService;
    private final LocationRepository locationRepository;
    private final CurrencyRateRepository currencyRateRepository;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public LotEntity save(LotEntity lotEntity, MultipartFile[] files) {
        validateFiles(files);


        var productCategoryEntity = switch (lotEntity.getProductCategory()) {
            case ProductCategoryEntity e when e.getId() != null ->
                    productCategoryService.getById(lotEntity.getProductCategory().getId());
            case ProductCategoryEntity e when e.getTitle() != null -> {
                if (productCategoryService.existsByTitle(e.getTitle()))
                    yield productCategoryService.getByTitle(e.getTitle());
                yield productCategoryService.save(lotEntity.getProductCategory(), null);
            }
            case null, default ->
                    throw new InvalidArgumentException(Map.of("productCategory", "Provide product category id or title"), "Invalid arguments");
        };

        if (locationRepository.findByCountryIdAndRegion(lotEntity.getLocation().getCountry().getId(), lotEntity.getLocation().getRegion()).isPresent()) {
            lotEntity.setLocation(
                    locationRepository.findByCountryIdAndRegion(
                            lotEntity.getLocation().getCountry().getId(),
                            lotEntity.getLocation().getRegion()).get());
        } else {
            lotEntity.getLocation()
                    .setCountry(countryService.getById(lotEntity.getLocation().getCountry().getId()));
        }

        if (lotEntity.getLotType().equals(LotTypeConstants.SELL) || lotEntity.getLotType().equals(LotTypeConstants.BUY))
            lotEntity.setDuration(0L);

        lotEntity.setUser(userService.getAuthenticatedUser());
        lotEntity.setProductCategory(productCategoryEntity);
        lotEntity.setTags(lotEntity.getTags()
                .stream().map(tagService::save)
                .toList());

        if (lotEntity.getLotType().equals(LotTypeConstants.AUCTION_SELL)) {
            lotEntity.setInnerStatus(StatusConstants.NEW);
            lotEntity.setStatus(StatusConstants.INACTIVE);
        }

        if (lotEntity.getExpirationDate() != null) {
            lotEntity.setExpirationDate(lotEntity.getExpirationDate());
        }

        var saved = lotRepository.save(lotEntity);
        if (saved.getLotType().equals(LotTypeConstants.AUCTION_SELL)) {
            notificationService.save(new Notification(
                    UUID.randomUUID(),
                    saved.getUser().getId(),
                    saved.getId(),
                    "ADMIN_MODERATION",
                    "You have new lot to moderate",
                    String.format("You have new lot to moderate. Lot name: %s", saved.getTitle()),
                    NotificationReadStatusConstants.UNREAD,
                    Instant.now(),
                    Role.ADMIN
            ));
        }
        saved.setImages(imageService.uploadImages(saved, files));
        return saved;
    }

    @Override
    @Transactional(rollbackOn = ImageLotException.class)
    public LotEntity save(LotEntity lotEntity, MultipartFile[] files, String currency) {
        var saved = save(lotEntity, files);
        return updateCurrency(saved, currency);
    }

    private void validateFiles(MultipartFile[] files) {
        if (files == null || files.length < 1 || files.length > 6)
            throw new InvalidArgumentException(Map.of("images", "Incorrect quantity of images must be from 1 to 6!"), "Invalid arguments!");
    }

    @Override
    public LotEntity getById(Long id) {
        return lotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no such lot with id: " + id));
    }

    @Override
    public LotEntity getById(Long id, String currency) {
        return updateCurrency(getById(id), currency);
    }

    @Override
    public void deleteById(Long id) {
        var lotToDelete = getById(id);
        if (!lotToDelete.getBets().isEmpty()) {
            throw new LotEditException("You can`t delete this lot, it already has customers");
        }
        if (lotToDelete.getStatus().equals(StatusConstants.ACTIVE)) {
            throw new LotEditException("You can`t delete this lot, deactivate it firstly");
        }
        lotRepository.deleteById(id);
    }

    @Override
    public void delete(LotEntity lot) {
        lotRepository.delete(lot);
    }

    @Override
    public List<LotEntity> getAll() {
        return lotRepository.findAll();
    }

    @Override
    public List<LotEntity> getAll(String currency) {
        return null;
    }

    @Override
    @Transactional(rollbackOn = ImageLotException.class)
    public LotEntity update(Long id, LotEntity lotEntityUpdatedFields, MultipartFile[] files, String currency) {
        var existedLotToUpdate = getById(id, currency);

        if (existedLotToUpdate.getInnerStatus().equals(StatusConstants.ON_MODERATION)) {
            throw new LotEditException("You can`t edit this lot while moderation");
        }
        if (existedLotToUpdate.getStatus().equals(StatusConstants.FINISHED)) {
            throw new LotEditException("This auction has already ended");
        }
        if (!existedLotToUpdate.getBets().isEmpty()) {
            throw new LotEditException("This lot already has customers");
        }
        var mappedAfterUpdateLot = lotMapper.update(existedLotToUpdate, lotEntityUpdatedFields);
        imageService.updateImagesForLot(existedLotToUpdate, mappedAfterUpdateLot, files);

        var isLotTypeChanged = !lotEntityUpdatedFields.getLotType().equals(existedLotToUpdate.getLotType());
        var newLotType = lotEntityUpdatedFields.getLotType();
        var newInnerStatus = lotEntityUpdatedFields.getInnerStatus();
        var newLotStatus = lotEntityUpdatedFields.getStatus();

        if (isLotTypeChanged) {
            if (lotEntityUpdatedFields.getLotType().equals(LotTypeConstants.AUCTION_SELL)) {
                newLotType = LotTypeConstants.AUCTION_SELL;
                newInnerStatus = StatusConstants.NEW;
                newLotStatus = StatusConstants.INACTIVE;
                mappedAfterUpdateLot.setExpirationDate(null);
            } else {
                if (existedLotToUpdate.getLotType().equals(LotTypeConstants.AUCTION_SELL)
                        && !(existedLotToUpdate.getInnerStatus().equals(StatusConstants.NEW) ||
                        existedLotToUpdate.getInnerStatus().equals(StatusConstants.REJECTED_BY_ADMIN))) {
                    throw new InvalidArgumentException(Map.of("lotType", "Lot type cant be changed to auction sell on this active lot"), "Invalid operation");
                }
                newLotType = lotEntityUpdatedFields.getLotType();
                newInnerStatus = StatusConstants.ACTIVE;
            }
            if (lotEntityUpdatedFields.getLotType().equals(LotTypeConstants.BUY) || lotEntityUpdatedFields.getLotType().equals(LotTypeConstants.SELL)) {
                if (!lotEntityUpdatedFields.getInnerStatus().equals(StatusConstants.REJECTED_BY_ADMIN)) {
                    newInnerStatus = StatusConstants.ACTIVE;
                    newLotStatus = StatusConstants.ACTIVE;
                }

            }
        }

        mappedAfterUpdateLot.setLotType(newLotType);
        mappedAfterUpdateLot.setInnerStatus(newInnerStatus);
        mappedAfterUpdateLot.setStatus(newLotStatus);

        var productCategoryEntity = switch (lotEntityUpdatedFields.getProductCategory()) {
            case ProductCategoryEntity e when e.getId() != null ->
                    productCategoryService.getById(lotEntityUpdatedFields.getProductCategory().getId());
            case ProductCategoryEntity e when e.getTitle() != null ->
                    productCategoryService.save(lotEntityUpdatedFields.getProductCategory(), null);
            case null, default ->
                    throw new InvalidArgumentException("Provide productCategory.id or productCategory.title");
        };

        if (!existedLotToUpdate.getInnerStatus().equals(StatusConstants.APPROVED)) {
            mappedAfterUpdateLot.setInnerStatus(StatusConstants.NEW);
        }
        if (existedLotToUpdate.getLotType().equals(LotTypeConstants.AUCTION_SELL)) {
            mappedAfterUpdateLot.setInnerStatus(StatusConstants.NEW);
            mappedAfterUpdateLot.setStatus(StatusConstants.INACTIVE);
        }
        mappedAfterUpdateLot.setUser(userService.getAuthenticatedUser());
        mappedAfterUpdateLot.setProductCategory(productCategoryEntity);
        mappedAfterUpdateLot.setTags(mappedAfterUpdateLot.getTags()
                .stream().map(tagService::save)
                .toList());

        if (locationRepository.findByCountryIdAndRegion(mappedAfterUpdateLot.getLocation().getCountry().getId(), mappedAfterUpdateLot.getLocation().getRegion()).isPresent()) {
            mappedAfterUpdateLot.setLocation(
                    locationRepository.findByCountryIdAndRegion(
                            mappedAfterUpdateLot.getLocation().getCountry().getId(),
                            mappedAfterUpdateLot.getLocation().getRegion()).get());
        } else {
            mappedAfterUpdateLot.getLocation()
                    .setCountry(countryService.getById(mappedAfterUpdateLot.getLocation().getCountry().getId()));
        }
        notificationService.save(new Notification(
                UUID.randomUUID(),
                mappedAfterUpdateLot.getUser().getId(),
                mappedAfterUpdateLot.getId(),
                "ADMIN_MODERATION",
                "You have new lot to moderate",
                String.format("You have new lot to moderate. Lot name: %s", mappedAfterUpdateLot.getTitle()),
                NotificationReadStatusConstants.UNREAD,
                Instant.now(),
                Role.ADMIN
        ));

        return updateCurrency(lotRepository.save(mappedAfterUpdateLot), currency);
    }

    @Override
    public LotEntity update(Long id, LotEntity entity) {
        var result = lotMapper.update(getById(id), entity);

        var userEntity = userService.getById(result.getUser().getId());
        var countryEntity = countryService.getById(result.getLocation().getCountry().getId());
        var productCategoryEntity = productCategoryService.getById(result.getProductCategory().getId());

        result.setUser(userEntity);
        result.getLocation().setCountry(countryEntity);
        result.setProductCategory(productCategoryEntity);

        return lotRepository.save(result);
    }

    @Override
    public LotStatusResponse getLotStatus(Long id) {
        var lot = getById(id);
        if (!lot.getLotType().equals(LotTypeConstants.AUCTION_SELL)) {
            throw new InvalidArgumentException("This lot is not an auction lot");
        }
        var bets = lot.getBets();
        var statusBuilder = LotStatusResponse.builder()
                .lotId(id)
                .userid(lot.getUser().getId())
                .status(lot.getStatus());
        if (!bets.isEmpty()) {
            statusBuilder
                    .betAmount(bets.getFirst().getAmount())
                    .betTime(bets.getFirst().getBetTime());
        }
        return statusBuilder.build();
    }

    private LotEntity moderateLot(Long id, String adminComment) {
        var lot = getById(id);
        if (!lot.getUser().getEnabled())
            throw new InvalidArgumentException("Lot owner is disabled!");
        if (adminComment != null) {
            lot.setAdminComment(adminComment);
        }
        lot.setInnerStatus(StatusConstants.ON_MODERATION);
        lot.setStatus(StatusConstants.INACTIVE);
        return update(id, lot);
    }

    @Override
    public LotEntity putOnModeration(Long lotId, String currency, String adminComment) {
        var lot = moderateLot(lotId, adminComment);
        return updateCurrency(lot, currency);
    }


    private LotEntity approveLot(Long id, String adminComment) {
        var lot = getById(id);
        if (!lot.getUser().getEnabled())
            throw new InvalidArgumentException("Lot owner is disabled!");

        lot.setExpirationDate(Instant.now().plusMillis(lot.getDuration()).atZone(lot.getUser().getZoneinfo() != null ? lot.getUser().getZoneinfo() : ZoneId.systemDefault()));
        lot.setActualStartDate(ZonedDateTime.now(lot.getUser().getZoneinfo() != null ? lot.getUser().getZoneinfo() : ZoneId.systemDefault()));
        lot.setInnerStatus(StatusConstants.APPROVED);
        if (lot.getUserStatus().equals(StatusConstants.ACTIVE)) {
            lot.setStatus(StatusConstants.ACTIVE);
        }
        if (adminComment != null) {
            lot.setAdminComment(adminComment);
        }
        lot.setStatus(StatusConstants.ACTIVE);
        return update(id, lot);
    }

    @Override
    public LotEntity approve(Long lotId, String currency, String adminComment) {
        var lot = approveLot(lotId, adminComment);
        lot.setAdminComment(adminComment);
        notificationService.save(new Notification(
                UUID.randomUUID(),
                lot.getUser().getId(),
                lot.getId(),
                "LOT_APPROVED",
                "Your lot was approved",
                String.format("Your lot was approved by admin. Lot name: %s", lot.getTitle()),
                NotificationReadStatusConstants.UNREAD,
                Instant.now(),
                Role.USER
        ));
        return updateCurrency(lot, currency);
    }

    @Override
    public void finishAuction(LotEntity lot) {
        lot.setStatus(StatusConstants.FINISHED);
        update(lot.getId(), lot);

        var bets = lot.getBets();
        if (bets != null && !bets.isEmpty()) {
            var winnerBet = bets.getFirst();
            if (winnerBet != null) {
                notificationService.save(new Notification(
                        UUID.randomUUID(),
                        lot.getUser().getId(),
                        lot.getId(),
                        "LOT_FINISHED",
                        "Your auction finished",
                        String.format("Your auction finished. Auction name: %s \nLast bet amount: %.2f %s", lot.getTitle(), winnerBet.getAmount(), lot.getOriginalCurrency()),
                        NotificationReadStatusConstants.UNREAD,
                        Instant.now(),
                        Role.USER
                ));
                notificationService.save(new Notification(
                        UUID.randomUUID(),
                        winnerBet.getUser().getId(),
                        lot.getId(),
                        "BET_WIN",
                        "You won in auction",
                        String.format("You won in auction. Auction name: %s", winnerBet.getLot().getTitle()),
                        NotificationReadStatusConstants.UNREAD,
                        Instant.now(),
                        Role.USER
                ));
                bets.stream().filter(betEntity -> !betEntity.getUser().getId()
                                .equals(winnerBet.getUser().getId()))
                        .collect(Collectors.groupingBy(BetEntity::getUser))
                        .values()
                        .stream()
                        .map(List::getFirst)
                        .toList()
                        .forEach(betEntity ->
                                notificationService.save(new Notification(
                                        UUID.randomUUID(),
                                        betEntity.getUser().getId(),
                                        lot.getId(),
                                        "AUCTION_LOST",
                                        "You lost in auction",
                                        String.format("Auction with your bet finished. You lost. Auction name: %s", betEntity.getLot().getTitle()),
                                        NotificationReadStatusConstants.UNREAD,
                                        Instant.now(),
                                        Role.USER
                                )));
            }
        } else {
            notificationService.save(new Notification(
                    UUID.randomUUID(),
                    lot.getUser().getId(),
                    lot.getId(),
                    "LOT_FINISHED",
                    "Your auction finished",
                    String.format("Your auction finished. Auction name: %s\nResult: no bets", lot.getTitle()),
                    NotificationReadStatusConstants.UNREAD,
                    Instant.now(),
                    Role.USER
            ));
        }
    }

    @Override
    public LotEntity makeDeal(Long lotId, UUID userId, String currency) {
        var lot = getById(lotId);
        validateDeal(lot, userId);
        var bets = lot.getBets();
        var newBetEntity = BetEntity.builder()
                .amount(lot.getOriginalPrice())
                .betTime(Instant.now())
                .user(userService.getById(userId))
                .lot(getById(lotId))
                .build();

        bets.add(newBetEntity);
        lot.setBets(bets);
        notificationService.save(new Notification(
                UUID.randomUUID(),
                lot.getUser().getId(),
                lot.getId(),
                "LOT_DEAL",
                "Your lot got an order",
                String.format("Your lot got an order. Lot name: %s", lot.getTitle()),
                NotificationReadStatusConstants.UNREAD,
                Instant.now(),
                Role.USER
        ));
        lot.setStatus(StatusConstants.FINISHED);
        var updated = update(lot.getId(), lot);
        lot.setLastBet(newBetEntity);
        return updateCurrency(updated, currency);
    }

    private void validateDeal(LotEntity lot, UUID userId) {
        if (lot.getLotType().equals(StatusConstants.AUCTION_SELL)) {
            throw new InvalidBetException("This lot is an auction lot, you can`t buy it");
        }
        if (lot.getUser().getId() == userId) {
            throw new InvalidBetException("You can't buy/sell your own lot");
        }
        if (!lot.getStatus().equals(StatusConstants.ACTIVE)) {
            throw new InvalidBetException("Sorry, this lot is no longer available.\\nPlease consider exploring other available lots");
        }
    }

    @Override
    public LotEntity reject(Long lotId, String adminComment) {
        if (adminComment.isEmpty()) {
            throw new LotEditException("You need to give comment about rejection");
        }
        var lot = getById(lotId);
        lot.setInnerStatus(StatusConstants.REJECTED_BY_ADMIN);
        lot.setStatus(StatusConstants.INACTIVE);
        lot.setAdminComment(adminComment);

        notificationService.save(new Notification(
                UUID.randomUUID(),
                lot.getUser().getId(),
                lot.getId(),
                "LOT_REJECT",
                "Your lot was rejected",
                String.format("Your lot was rejected by admin. Lot name: %s\n Reject message: %s", lot.getTitle(), lot.getAdminComment()),
                NotificationReadStatusConstants.UNREAD,
                Instant.now(),
                Role.USER
        ));
        return update(lotId, lot);
    }

    @Override
    public LotEntity changeUserStatus(Long id, boolean status) {
        var lot = getById(id);
        validateUserStatusChangeRequest(lot);
        var userStatus = status ? StatusConstants.ACTIVE : StatusConstants.INACTIVE;
        var lotStatus = lot.getStatus();
        if (status && lot.getInnerStatus().equals(StatusConstants.APPROVED)) {
            lotStatus = StatusConstants.ACTIVE;
        }
        if (lot.getLotType().equals(StatusConstants.AUCTION_SELL)) {
            lot.setInnerStatus(StatusConstants.NEW);
            lotStatus = StatusConstants.INACTIVE;
        }
        if (!status && !lot.getLotType().equals(StatusConstants.AUCTION_SELL)) {
            lotStatus = StatusConstants.INACTIVE;
        }
        lot.setUserStatus(userStatus);
        lot.setStatus(lotStatus);
        return update(id, lot);
    }

    private void validateUserStatusChangeRequest(LotEntity lot) {
        if (!lot.getBets().isEmpty()) {
            throw new LotEditException("It`s not possible to change lot userStatus. It already has customers");
        }
    }

    @Override
    public List<LotEntity> getWithCriteria(Map<String, String> filters, int pageNumber, int pageSize, String currency) {
        var searchCriteria = filterService.getCriteria(filters, currency);
        var lots = lotRepository.findAll(searchCriteria, PageRequest.of(pageNumber, pageSize)).toList();
        return new ArrayList<>(lots.stream().map(lot -> updateCurrency(lot, currency)).toList());
    }


    private LotEntity updateCurrency(LotEntity lotEntity, String currency) {
        var currencyRates = currencyRateRepository.findBySourceCurrency(lotEntity.getOriginalCurrency())
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "There is no currency with name %s", lotEntity.getCurrency())));
        lotEntity.setCurrency(currency);
        lotEntity.setCurrencyRates(currencyRates);
        return lotEntity;
    }

    @Override
    public List<LotEntity> getUserActivityById(UUID id, String currency) {
        var user = userService.getById(id);
        return lotRepository.findByBetsUserId(user.getId()).stream()
                .map(lot -> updateCurrency(lot, currency))
                .toList();
    }
}
