package com.fastcampus.pass.repository.pass;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PassRepository extends JpaRepository<Pass, Integer> {

    @Modifying
    @Query(value = "UPDATE Pass p SET p.remainingCount = :remainingCount WHERE p.passSeq = :passSeq")
    int updateRemainingCount(Integer passSeq, Integer remainingCount);
}
