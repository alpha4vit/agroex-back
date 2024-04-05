package com.vention.agroex.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

import static jakarta.persistence.FetchType.EAGER;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lot")
public class LotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "variety")
    private String variety;

    @Column(name = "size")
    private String size;

    @Column(name = "packaging")
    private String packaging;

    @Column(name = "enabled_by_admin")
    private Boolean enabledByAdmin;

    @Column(name = "quantity")
    private float quantity;

    @Column(name = "original_min_price")
    private BigDecimal originalMinPrice;

    @Column(name = "original_price")
    private BigDecimal originalPrice;

    @Column(name = "currency")
    private String originalCurrency;

    @Column(name = "inner_status")
    private String innerStatus;

    @Column(name = "user_status")
    private String userStatus;

    @Column(name = "status")
    private String status;

    @Column(name = "duration")
    private Long duration;

    @CreationTimestamp
    @Column(name = "creation_date", columnDefinition = "timestamp with time zone")
    private ZonedDateTime creationDate;

    @Column(name = "expiration_date", columnDefinition = "timestamp with time zone")
    private ZonedDateTime expirationDate;

    @Column(name = "actual_start_date", columnDefinition = "timestamp with time zone")
    private ZonedDateTime actualStartDate;

    @ManyToOne
    @JoinColumn(name = "product_category_id", referencedColumnName = "id")
    private ProductCategoryEntity productCategory;

    @Column(name = "lot_type")
    private String lotType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private LocationEntity location;

    @ManyToMany
    @JoinTable(
            name = "lot_tag",
            joinColumns = @JoinColumn(name = "lot_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"lot_id", "tag_id"}))
    private List<TagEntity> tags;

    @OneToMany(mappedBy = "lot", cascade = CascadeType.REMOVE)
    private List<ImageEntity> images;

    @OneToMany(mappedBy = "lot", cascade = CascadeType.ALL, fetch = EAGER)
    private List<BetEntity> bets;

    @ManyToMany
    @JoinTable(
            name = "lot_currency_rates",
            joinColumns = @JoinColumn(name = "lot_currency"),
            inverseJoinColumns = @JoinColumn(name = "currency_rates_id")
    )
    private List<CurrencyRateEntity> currencyRates;

    @Transient
    private String currency;

    @Transient
    private BigDecimal price;

    @Transient
    private BigDecimal minPrice;

    @Column(name = "admin_comment")
    private String adminComment;

    @Transient
    private BetEntity lastBet;

    @OneToMany(mappedBy = "lot", cascade = CascadeType.REMOVE)
    private List<NotificationEntity> notifications;

    public BigDecimal getMinPrice() {
        if (originalMinPrice == null) {
            return null;
        }
        if (currency.equals(originalCurrency)) {
            minPrice = originalMinPrice;
        } else {
            minPrice = currencyRates.stream()
                    .filter(rateEntity -> rateEntity.getTargetCurrency().equals(currency))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException(
                            String.format("There is no currency with name: %s", currency)))
                    .getRate().multiply(originalMinPrice);
        }
        return minPrice;
    }

    public BigDecimal getPrice() {
        if (currency.equals(originalCurrency)) {
            price = originalPrice;
        } else {
            price = currencyRates.stream()
                    .filter(rateEntity -> rateEntity.getTargetCurrency().equals(currency))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException(
                            String.format("There is no currency with name: %s", currency)))
                    .getRate().multiply(originalPrice);
        }
        return price;
    }

    @PostLoad
    private void init() {
        this.currency = this.getOriginalCurrency();
        this.lastBet = this.bets.stream()
                .max(Comparator.comparing(BetEntity::getAmount)
                        .thenComparing(BetEntity::getBetTime))
                .orElse(null);
    }
}