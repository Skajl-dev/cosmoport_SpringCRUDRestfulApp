package com.space.controller;


import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/rest")
public class ShipController {

    private final ShipService shipService;

    @Autowired
    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }


    @GetMapping(value = "/ships/{id}")
    public ResponseEntity<Ship> getShip(@PathVariable(value = "id") Long id) {
        List<Ship> ships = shipService.findAll();
        if (id < 1)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (id >= ships.size())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        if (shipService.existById(id))
            return new ResponseEntity<>(shipService.findById(id), HttpStatus.OK);

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/ships/{id}")
    public ResponseEntity<Ship> updateShip(@RequestBody Ship shipValues, @PathVariable(value = "id") Long id) {
        if (id < 1)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (!shipService.existById(id))
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        Ship shipToUpdate = shipService.findById(id);

        if (shipValues == null)
            return new ResponseEntity<>(shipToUpdate, HttpStatus.OK);

        Ship updatedShip = shipUpdater(shipToUpdate, shipValues);

        if (updatedShip == null)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        shipService.saveShip(updatedShip);
        return new ResponseEntity<>(updatedShip, HttpStatus.OK);
    }

    private Ship shipUpdater(Ship shipToUpdate, Ship values) {
        Ship retardedOne = null;
        if (values.getName() == null && values.getPlanet() == null && values.getShipType() == null &&
        values.getProdDate() == null && values.getSpeed() == null && values.getCrewSize() == null)
            return shipToUpdate;

        if (values.getName() != null) {
            if (values.getName().length() < 50 && !values.getName().equals(""))
                shipToUpdate.setName(values.getName());
            else
                return retardedOne;
        }

        if (values.getPlanet() != null) {
            if (values.getPlanet().length() < 50 && !values.getPlanet().equals(""))
                shipToUpdate.setPlanet(values.getPlanet());
            else
                return retardedOne;
        }

        if (values.getShipType() != null)
            shipToUpdate.setShipType(values.getShipType());

        if (values.getProdDate() != null) {
            if (values.getProdDate().getTime() > 26189470800000L && values.getProdDate().getTime() < 33100434000000L)
                shipToUpdate.setProdDate(values.getProdDate());
            else
                return retardedOne;
        }

        if (values.isUsed() != null) {
            shipToUpdate.setUsed(values.isUsed());

        }

        if (values.getSpeed() != null) {
            if (values.getSpeed() >= 0.1d && values.getSpeed() <= 0.99d) {
                double speed = Math.round(values.getSpeed() * 100.0) / 100.0;
                shipToUpdate.setSpeed(speed);
            } else
                return retardedOne;
        }
        if (values.getCrewSize() != null) {
            if (values.getCrewSize() > 0 && values.getCrewSize() < 10000)
                shipToUpdate.setCrewSize(values.getCrewSize());
            else
                return retardedOne;
        }

        int currentYear = 3019;
        int productionYear = shipToUpdate.getProdDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();
        double coef = shipToUpdate.isUsed() == true ? 0.5d : 1.0d;
        double rating = (80 * shipToUpdate.getSpeed() * coef) / (double) (currentYear - productionYear + 1);
        rating = Math.round(rating * 100.0) / 100.0;
        shipToUpdate.setRating(rating);

        return shipToUpdate;
    }


    @DeleteMapping("/ships/{id}")
    public ResponseEntity<?> deleteShip(@PathVariable(value = "id") Long id) {
        List<Ship> ships = shipService.findAll();
        if (id < 1) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (id >= ships.size()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        if (shipService.existById(id)) {
            shipService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/ships")
    public ResponseEntity<Ship> addShip(@RequestBody Ship ship) {

        if (!checkShipCreationParams(ship))
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        if (ship.isUsed() == null)
            ship.setUsed(false);
        ship.setSpeed(Math.round(ship.getSpeed() * 100.0) / 100.0);


        int currentYear = 3019;
        int productionYear = ship.getProdDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();
        double coef = ship.isUsed() == true ? 0.5d : 1.0d;
        double rating = (80 * ship.getSpeed() * coef) / (double) (currentYear - productionYear + 1);
        rating = Math.round(rating * 100.0) / 100.0;
        ship.setRating(rating);

        shipService.saveShip(ship);
        return new ResponseEntity<>(ship, HttpStatus.OK);

    }

    @GetMapping("/ships")
    public ResponseEntity<List<Ship>> findAllShips(@RequestParam(value = "name", required = false) String name,
                                                   @RequestParam(value = "planet", required = false) String planet,
                                                   @RequestParam(value = "shipType", required = false) ShipType shipType,
                                                   @RequestParam(value = "after", defaultValue = "0", required = false) Long after,
                                                   @RequestParam(value = "before", defaultValue = "922337203685477580", required = false) Long before,
                                                   @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                                   @RequestParam(value = "minSpeed", defaultValue = "0.01", required = false) Double minSpeed,
                                                   @RequestParam(value = "maxSpeed", defaultValue = "0.99", required = false) Double maxSpeed,
                                                   @RequestParam(value = "minCrewSize", defaultValue = "1", required = false) Integer minCrewSize,
                                                   @RequestParam(value = "maxCrewSize", defaultValue = "9999", required = false) Integer maxCrewSize,
                                                   @RequestParam(value = "minRating", defaultValue = "0.0", required = false) Double minRating,
                                                   @RequestParam(value = "maxRating", defaultValue = "100.0", required = false) Double maxRating,
                                                   @RequestParam(value = "order", required = false) ShipOrder shipOrder,
                                                   @RequestParam(value = "pageNumber", defaultValue = "0", required = false) Integer pageNumber,
                                                   @RequestParam(value = "pageSize", defaultValue = "3", required = false) Integer pageSize
    ) {
        List<Ship> ships = shipsFilter(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize
                , maxCrewSize, minRating, maxRating);

        return new ResponseEntity<>(prepareFilteredShips(ships, shipOrder, pageNumber, pageSize), HttpStatus.OK);
    }

    @GetMapping(value = "/ships/count")
    public ResponseEntity<Integer> shipsCount(@RequestParam(value = "name", required = false) String name,
                                              @RequestParam(value = "planet", required = false) String planet,
                                              @RequestParam(value = "shipType", required = false) ShipType shipType,
                                              @RequestParam(value = "after", defaultValue = "0", required = false) Long after,
                                              @RequestParam(value = "before", defaultValue = "922337203685477580", required = false) Long before,
                                              @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                              @RequestParam(value = "minSpeed", defaultValue = "0.01", required = false) Double minSpeed,
                                              @RequestParam(value = "maxSpeed", defaultValue = "0.99", required = false) Double maxSpeed,
                                              @RequestParam(value = "minCrewSize", defaultValue = "1", required = false) Integer minCrewSize,
                                              @RequestParam(value = "maxCrewSize", defaultValue = "9999", required = false) Integer maxCrewSize,
                                              @RequestParam(value = "minRating", defaultValue = "0.0", required = false) Double minRating,
                                              @RequestParam(value = "maxRating", defaultValue = "100.0", required = false) Double maxRating
    ) {
        List<Ship> ships = shipsFilter(name, planet, shipType, after, before, isUsed, minSpeed, maxSpeed, minCrewSize
                , maxCrewSize, minRating, maxRating);
        Integer countOfShips = ships.size();
        return new ResponseEntity<>(countOfShips, HttpStatus.OK);
    }


    private List<Ship> shipsFilter(String name, String planet, ShipType shipType, Long after, Long before,
                                   Boolean isUsed, Double minSpeed, Double maxSpeed, Integer minCrewSize,
                                   Integer maxCrewSize, Double minRating, Double maxRating) {
        List<Ship> ships = shipService.findAll();

        if (planet != null)
            ships = ships.stream().filter(ship -> ship.getPlanet().contains(planet)).collect(Collectors.toList());

        if (shipType != null)
            ships = ships.stream().filter(ship -> ship.getShipType() == shipType).collect(Collectors.toList());

        if (name != null)
            ships = ships.stream().filter(ship -> ship.getName().contains(name)).collect(Collectors.toList());

        if (isUsed != null)
            ships = ships.stream().filter(ship -> ship.isUsed() == isUsed).collect(Collectors.toList());

        ships = ships.stream().filter(ship -> ship.getProdDate().getTime() >= after && ship.getProdDate().getTime() <= before).
                filter(ship -> ship.getSpeed() >= minSpeed && ship.getSpeed() <= maxSpeed).
                filter(ship -> ship.getCrewSize() >= minCrewSize && ship.getCrewSize() <= maxCrewSize).
                filter(ship -> ship.getRating() > minRating && ship.getRating() < maxRating).collect(Collectors.toList());

        return ships;
    }

    private List<Ship> prepareFilteredShips(final List<Ship> filteredShips, ShipOrder shipOrder, Integer pageNumber, Integer pageSize) {
        return filteredShips.stream().sorted(getComparator(shipOrder)).skip(pageNumber * pageSize).
                limit(pageSize).collect(Collectors.toList());
    }

    private Comparator<Ship> getComparator(ShipOrder shipOrder) {
        Comparator<Ship> comparator = null;

        if (shipOrder == ShipOrder.ID)
            comparator = Comparator.comparing(ship -> ship.getId());

        else if (shipOrder == ShipOrder.SPEED)
            comparator = Comparator.comparing(ship -> ship.getSpeed());

        else if (shipOrder == ShipOrder.DATE)
            comparator = Comparator.comparing(ship -> ship.getProdDate().getTime());

        else if (shipOrder == ShipOrder.RATING)
            comparator = Comparator.comparing(ship -> ship.getRating());

        else
            return Comparator.comparing(ship -> ship.getId());

        return comparator;
    }

    private boolean checkShipCreationParams(final Ship ship) {
        if (ship == null || ship.getName() == null || ship.getPlanet() == null || ship.getShipType() == null ||
                ship.getProdDate() == null || ship.getSpeed() == null || ship.getCrewSize() == null ||
                ship.getName().length() > 50 || ship.getPlanet().length() > 50 || ship.getProdDate().getTime() < 26189470800000L ||
                ship.getProdDate().getTime() > 33100434000000L || ship.getSpeed() < 0.01d || ship.getSpeed() > 0.99d || ship.getCrewSize() < 1 ||
                ship.getCrewSize() > 9999 || ship.getPlanet().equals("") || ship.getName().equals(""))
            return false;


        return true;
    }


}
