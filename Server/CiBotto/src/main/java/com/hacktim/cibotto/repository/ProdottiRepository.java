package com.hacktim.cibotto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdottiRepository extends JpaRepository<ProdottiDao, String> {

    List<ProdottiDao> findByProdOrderByQty(String prod);

    List<ProdottiDao> findByTypOrderByQty(String typ);

}
