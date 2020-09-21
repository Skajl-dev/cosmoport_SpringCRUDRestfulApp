package com.space.repository;

import com.google.protobuf.Message;
import com.space.model.Ship;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;

@Repository
public interface ShipRepository extends JpaRepository<Ship, Long> {


}
