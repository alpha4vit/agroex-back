package com.vention.agroex.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "lot")
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

    @Column(name = "original_currency")
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

    @ManyToOne(cascade = CascadeType.ALL)
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

    @OneToMany(mappedBy = "lot", cascade = CascadeType.ALL)
    private List<BetEntity> bets;

    @Formula("(SELECT ls.search_string FROM lot_search_view ls WHERE ls.id = id)")
    private String searchString;

    @Transient
    private String currency;

    @Formula("(SELECT l.original_price * cr.rate FROM lot l LEFT JOIN currency_rates cr ON" +
            " cr.source_currency = l.original_currency AND cr.target_currency = 'USD' WHERE l.id = id)")
    private BigDecimal calculatedPrice;

    @Transient
    private BigDecimal price;

    @Transient
    private BigDecimal minPrice;

    @Column(name = "admin_comment")
    private String adminComment;

    @PostLoad
    private void init() {
        this.currency = this.getOriginalCurrency();
        this.price = this.getOriginalPrice();
    }

    public void updatePrice(CurrencyRateEntity currencyRate) {
        var sourceCurrency = this.getOriginalCurrency();
        if (!sourceCurrency.equals(currencyRate.getTargetCurrency())) {
            this.setPrice(this.getOriginalPrice().multiply(currencyRate.getRate()).setScale(4, RoundingMode.HALF_UP));
            this.setMinPrice(this.getOriginalMinPrice().multiply(currencyRate.getRate()).setScale(4, RoundingMode.HALF_UP));
        } else {
            this.setPrice(this.getOriginalPrice());
            this.setMinPrice(this.getOriginalMinPrice());
        }
        this.setCurrency(currencyRate.getTargetCurrency());
    }
}