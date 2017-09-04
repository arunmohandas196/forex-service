package com.amohandas.forex.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Created by arunmohandas on 04/09/17.
 * <p>
 * a simple domain entity doubling as a DTO
 */

@Entity
@Table(name = "ForexCurrency")
public class ForexCurrency {
    @Column()
    BigDecimal rate;
    @Id
    @GeneratedValue()
    private long id;
    @Column(nullable = false)
    private String source;
    @Column(nullable = false)
    private String target;
    @Column()
    private String exchangeDate;
    @Column()
    private LocalDateTime createdTime;

    public ForexCurrency() {
    }

    public ForexCurrency(String source, String target, String exchangeDate, BigDecimal rate) {
        this.source = source;
        this.target = target;
        this.exchangeDate = exchangeDate;
        this.rate = rate;
        this.createdTime = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "ForexCurrency{" +
                "rate=" + rate +
                ", id=" + id +
                ", source='" + source + '\'' +
                ", target='" + target + '\'' +
                ", exchangeDate=" + exchangeDate +
                ", createdTime=" + createdTime +
                '}';
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getExchangeDate() {
        return exchangeDate;
    }

    public void setExchangeDate(String exchangeDate) {
        this.exchangeDate = exchangeDate;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}
