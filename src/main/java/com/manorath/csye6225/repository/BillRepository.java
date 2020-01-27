package com.manorath.csye6225.repository;

import com.manorath.csye6225.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill,String> {
    @Query(value = "select t from Bill t where t.ownerID = ?1")
    List<Bill> findAllByOwnerID(String ownerId);
}
