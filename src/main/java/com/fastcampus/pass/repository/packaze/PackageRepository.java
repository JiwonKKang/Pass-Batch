package com.fastcampus.pass.repository.packaze;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface PackageRepository extends JpaRepository<Package, Integer> {

    Page<Package> findByCreatedAtAfter(LocalDateTime dateTime, Pageable pageable);

}
