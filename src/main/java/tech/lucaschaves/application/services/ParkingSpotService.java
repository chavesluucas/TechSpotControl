package tech.lucaschaves.application.services;

import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import tech.lucaschaves.application.models.ParkingSpotModel;
import tech.lucaschaves.application.repositories.ParkingSpotRepository;

@Service
public class ParkingSpotService {

	@Autowired
	ParkingSpotRepository repository;


	@Transactional //interesante utilizar essa anotation
	public ParkingSpotModel save(ParkingSpotModel parkingSpotModel) {
		return repository.save(parkingSpotModel);
	}


	//Verificação se a placa do carro já está cadastrada
	public boolean existsByLicensePlateCar(String licensePlateCar) {
		return repository.existsByLicensePlateCar(licensePlateCar);
	}

	//Verificação se a numero da vaga já está preenchida
	public boolean existsByParkingSpotNumber(String parkingSpotNumber) {
		return repository.existsByParkingSpotNumber(parkingSpotNumber);
	}


	//Verificação para verificar se o apartamento e bloco já possui uma vaga
	public boolean existsByApartmentAndBlock(String apartment, String block) {
		return repository.existsByApartmentAndBlock(apartment, block);
	}


	public Page<ParkingSpotModel> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}
	
	public Optional<ParkingSpotModel> findById(UUID id){
		return repository.findById(id);
	}

	@Transactional //utilizar por ser um metodo destrutivo
	public void delete(ParkingSpotModel parkingSpotModel) {
		repository.delete(parkingSpotModel);
	}
}
