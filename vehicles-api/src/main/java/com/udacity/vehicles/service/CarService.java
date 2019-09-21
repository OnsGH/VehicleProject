package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.Location;
import com.udacity.vehicles.domain.car.Car;
import com.udacity.vehicles.domain.car.CarRepository;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class CarService {

    private final CarRepository repository;
    private PriceClient priceClient;
    private MapsClient mapsClient;

    public CarService(CarRepository repository,PriceClient priceClient,MapsClient mapsClient) {
        /**
         *  Add the Maps and Pricing Web Clients
         *  in `VehiclesApiApplication` as arguments and set them here.
         *
         */
        this.priceClient=priceClient;
        this.mapsClient=mapsClient;
        this.repository = repository;
    }



    /**
     * Gathers a list of all vehicles
     * @return a list of all vehicles in the CarRepository
     */
    public List<Car> list() {
        return repository.findAll();
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id) {

        boolean exist  ;
        /**
         *  Find the car by ID from the `repository` if it exists.
         *  If it does not exist, throw a CarNotFoundException
         */
        exist = false;
        exist = repository.findById(id).isPresent();
        if (!exist) {
            throw new CarNotFoundException("Car not found") ;
        }


        ModelMapper mapper = new ModelMapper();
        WebClient mapsWebClient = WebClient.create("http://localhost:9191/");

         MapsClient mapsClient = new MapsClient(mapsWebClient,mapper);

        /**
         *   Get the price based on the `id` input'
         *   Set the price of the car
         */
         WebClient pricingclient = WebClient.create("http://localhost:8762/");
         PriceClient priceClient = new PriceClient(pricingclient);
         CarService carService = new CarService(repository,priceClient,mapsClient);

         carService.repository.findById(id).get().setPrice(priceClient.getPrice(id));

        /**
         * Use the Maps Web client you create in `VehiclesApiApplication`
         * to get the address for the vehicle.
         *
         * Set the location of the vehicle, including the address information
         */

        Location location = new Location();

        location = repository.findById(id).get().getLocation();

        carService.repository.findById(id).get().getLocation().setAddress(mapsClient.getAddress(location).getAddress());
        carService.repository.findById(id).get().getLocation().setCity(mapsClient.getAddress(location).getCity());
        carService.repository.findById(id).get().getLocation().setState(mapsClient.getAddress(location).getState());
        carService.repository.findById(id).get().getLocation().setZip(mapsClient.getAddress(location).getZip());


        return carService.repository.findById(id).get();

    }

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
       if (car.getId() != null) {
            return repository.findById(car.getId())
                    .map(carToBeUpdated -> {
                        carToBeUpdated.setDetails(car.getDetails());
                        carToBeUpdated.setLocation(car.getLocation());
                        return repository.save(carToBeUpdated);

                    }).orElseThrow(CarNotFoundException::new);


        }

        return repository.save(car);
    }


    /**
     * Deletes a given car by ID
     * @param id the ID number of the car to delete
     */
    public void delete(Long id) {
        /**
         * Find the car by ID from the `repository` if it exists.
         *  If it does not exist, throw a CarNotFoundException
         */

        boolean exist =false ;
        exist = repository.findById(id).isPresent();

        if (!exist) {

            throw new CarNotFoundException("Car not found") ;

        }
         /**
          * Delete the car from the repository
          **/
         repository.deleteById(id);


    }
}
