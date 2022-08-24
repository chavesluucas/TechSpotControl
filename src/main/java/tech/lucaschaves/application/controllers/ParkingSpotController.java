package tech.lucaschaves.application.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tech.lucaschaves.application.dtos.ParkingSpotDTO;
import tech.lucaschaves.application.models.ParkingSpotModel;
import tech.lucaschaves.application.services.ParkingSpotService;

@RestController
@RequestMapping("/spot")
@CrossOrigin("*")
public class ParkingSpotController {

	@Autowired
	ParkingSpotService service;

	
	@PostMapping //Botamos Object porque poderemos ter diferentes tipos de retorno com as validações
	public ResponseEntity<Object> save(@RequestBody @Valid ParkingSpotDTO dto){ //o @Valid é para realizar as validações inseridas no DTO
	
		//validação, por exemplo, se o carro já tiver uma vaga ele não pode ter outra
		if(service.existsByLicensePlateCar(dto.getLicensePlateCar())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: License Plate Car is already in use!");
		}
		
		if(service.existsByParkingSpotNumber(dto.getParkingSpotNumber())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: Parking Spot is already in use!");
		}
		
		if(service.existsByApartmentAndBlock(dto.getApartment(), dto.getBlock())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Conflict: License Plate Car is already in use");
		}

		
		/*or VAR-> */ParkingSpotModel  parkingSpotModel = new ParkingSpotModel();
		
		//A gente recebe um DTO e convertemos para Model para poder salver no banco de dados
		//o paramentro do copyProperties(*oque vai ser convertido*, *para oque vai ser convertido*)
		BeanUtils.copyProperties(dto, parkingSpotModel); 
		
		//Setando a data de registro
		parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
		
		return ResponseEntity.status(HttpStatus.CREATED).body(service.save(parkingSpotModel));
	}
	
	//Padrão como sempre
	@GetMapping
	public ResponseEntity<Page<ParkingSpotModel>> getAll(@PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable){
		return ResponseEntity.status(HttpStatus.OK).body(service.findAll(pageable));
	}
	
	@GetMapping("/{id}") //vai ser um Object pois tem retornos diferente
	public ResponseEntity<Object> getById(@PathVariable(value = "id") UUID id){
		Optional<ParkingSpotModel> parkingSpotModelOptional = service.findById(id);
		
		//Se parkingSpotModelOptional não tiver presente
		if(!parkingSpotModelOptional.isPresent()) {
			//Retorne um status code de não encontrado
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found");
		}
		//se encontrou retorne um status code OK com o objeto no corpo
		return ResponseEntity.status(HttpStatus.OK).body(parkingSpotModelOptional.get());
	}
	 
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> delete(@PathVariable(value = "id") UUID id){
		Optional<ParkingSpotModel> parkingSpotModelOptional = service.findById(id);
		if(!parkingSpotModelOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found");
		}
		service.delete(parkingSpotModelOptional.get());
		return ResponseEntity.status(HttpStatus.OK).body("Parking Spot deleted successfully");
	}
	
	//Put, geralmente a maneira que é feita (bora fazer essa, pode ser maior mas pelo menos se familhariza quando for pegar em alguma empresa)
	@PutMapping("/{id}")
	public ResponseEntity<Object> update(@PathVariable(value = "id") UUID id,
										@RequestBody @Valid ParkingSpotDTO dto){
		Optional<ParkingSpotModel> parkingSpotModelOptional = service.findById(id);
		//validação
		if(!parkingSpotModelOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking Spot not found");
		}
		
		var parkingSpotModel = new ParkingSpotModel();
		//Convertendo a DTO em model
		BeanUtils.copyProperties(dto, parkingSpotModel);
		//Outra forma de fazer isso abaixo é setar todos os dados novamente, menos ID e Data de Registro, exemplo
		//parkingSpotModel.setParkingSpotNumber(dto.getParkingSpotNumber());
		//parkingSpotModel.setLicensePlateCar(dto.getPlateCar());
		//Usei dois exemplo, mas teria que fazer com todos
		
		//Aqui estamos fazendo o contrario
		//setando o ID para ficar igual
		parkingSpotModel.setId(parkingSpotModelOptional.get().getId());
		//Setando o ResistrationDate para ficar igual
		parkingSpotModel.setRegistrationDate(parkingSpotModelOptional.get().getRegistrationDate());
		
		return ResponseEntity.status(HttpStatus.OK).body(service.save(parkingSpotModel));
	}
}
