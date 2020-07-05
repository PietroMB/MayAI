package com.hacktim.cibotto.repository;

import com.google.api.services.dialogflow.v2.model.GoogleCloudDialogflowV2IntentMessageImage;

import javax.persistence.*;

@Entity
@Table(name = "PRODOTTI", uniqueConstraints={@UniqueConstraint(columnNames = {"ID", "PROD", "LOC", "TYP", "PROD_IMG", "LOC_IMG", "QTY"})})
public class ProdottiDao {

    @Id
    private Long id;

    @Column(name = "PROD", nullable = false, length = 80)
    private String prod;

    @Column(name = "LOC", nullable = false, length = 80)
    private String loc;

    @Column(name = "TYP", nullable = false, length = 80)
    private String typ;

    @Column(name = "PROD_IMG", nullable = true, length = 80)
    private String prodImg;

    @Column(name = "LOC_IMG", nullable = true, length = 80)
    private String locImg;

    @Column(name = "QTY", nullable = false,  length = 2)
    @org.hibernate.annotations.ColumnDefault("0")
    private Integer qty;


    public ProdottiDao() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProd() {
        return prod;
    }

    public void setProd(String prod) {
        this.prod = prod;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getTyp() {
        return typ;
    }

    public void setTyp(String typ) {
        this.typ = typ;
    }

    public String getProdImg() {
        return prodImg;
    }

    public void setProdImg(String prodImg) {
        this.prodImg = prodImg;
    }

    public String getLocImg() {
        return locImg;
    }

    public void setLocImg(String locImg) {
        this.locImg = locImg;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
}
