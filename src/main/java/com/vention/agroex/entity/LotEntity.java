package com.vention.agroex.entity;

import com.vention.agroex.exception.InvalidArgumentException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;

import java.time.Instant;
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

    @Column(name = "min_price")
    private float minPrice;

    @Column(name = "original_price")
    private float originalPrice;

    @Column(name = "original_currency")
    private String originalCurrency;

    @Column(name = "admin_status")
    private String adminStatus;

    @Column(name = "user_status")
    private String userStatus;

    @Column(name = "status")
    private String status;

    @Column(name = "duration")
    private Long duration;

    @CreationTimestamp
    @Column(name = "creation_date")
    private Instant creationDate;

    @Column(name = "expiration_date")
    private Instant expirationDate;

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

    @Transient
    private float price;

    public void updatePrice(String targetCurrency, List<CurrencyRateEntity> currencies) {
        var sourceCurrency = this.getOriginalCurrency();
        if (!sourceCurrency.equals(targetCurrency)) {
            var rateList = currencies.stream()
                    .filter(el ->
                            el.getTargetCurrency().equals(targetCurrency) && el.getSourceCurrency().equals(sourceCurrency))
                    .toList();
            if (rateList.isEmpty())
                throw new InvalidArgumentException("Currency with this name is not supported!");
            this.setPrice(this.getOriginalPrice() * rateList.getFirst().getRate());
        } else
            this.setPrice(this.getOriginalPrice());
        this.setCurrency(targetCurrency);
    }
}