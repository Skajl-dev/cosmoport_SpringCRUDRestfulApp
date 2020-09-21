package com.space.service;


import com.space.model.Ship;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShipService {

    private final ShipRepository shipRepository;

    @Autowired
    public ShipService(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    public Ship findById(Long id) {
        return shipRepository.findById(id).get();
    }

    public List<Ship> findAll() {
        return shipRepository.findAll();
    }

    public Ship saveShip(Ship ship) {
        return shipRepository.save(ship);
    }

    public void deleteById(Long id) {
            shipRepository.deleteById(id);
    }

    public boolean existById(Long id) {
        return shipRepository.existsById(id);
    }


}
