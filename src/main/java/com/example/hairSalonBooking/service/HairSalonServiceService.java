package com.example.hairSalonBooking.service;

import com.example.hairSalonBooking.entity.Account;
import com.example.hairSalonBooking.entity.SalonService;
import com.example.hairSalonBooking.entity.Skill;
import com.example.hairSalonBooking.model.request.CreateServiceRequest;
import com.example.hairSalonBooking.model.request.BookingStylits;
import com.example.hairSalonBooking.model.request.ServiceUpdateRequest;
import com.example.hairSalonBooking.model.response.ServiceResponse;
import com.example.hairSalonBooking.model.response.StylistForBooking;
import com.example.hairSalonBooking.repository.AccountRepository;
import com.example.hairSalonBooking.repository.ServiceRepository;
import com.example.hairSalonBooking.repository.SkillRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;

@Service
public class HairSalonServiceService {
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private ImagesService imagesService;
    @Autowired
    private SkillRepository skillRepository;
    @Autowired
    private AccountRepository accountRepository;
    public SalonService createService(CreateServiceRequest createServiceRequest) {

            SalonService salonService = modelMapper.map(createServiceRequest, SalonService.class);

            Skill skill = skillRepository.findSkillBySkillName(createServiceRequest.getSkillName());
            salonService.setSkill(skill);
            return serviceRepository.save(salonService);

    }
    public List<ServiceResponse> getAllServices(){
        List<SalonService> services = serviceRepository.findAll();
        List<ServiceResponse> responses = new ArrayList<>();
        for(SalonService service : services){
            ServiceResponse serviceResponse = new ServiceResponse();
            serviceResponse.setServiceName(service.getServiceName());
            serviceResponse.setPrice(service.getPrice());
            serviceResponse.setImage(service.getImage());
            serviceResponse.setDuration(service.getDuration());
            serviceResponse.setDescription(service.getDescription());
            responses.add(serviceResponse);
        }
        return responses;
    }
    public List<SalonService> searchServiceByName(String serviceName) {
        return serviceRepository.findByServiceNameContainingIgnoreCase(serviceName);
    }
    public Optional<SalonService> searchServiceId(long serviceId) {
        return serviceRepository.findByServiceId(serviceId)
                .or(() -> {
                    throw new RuntimeException("Service not found");
                });
    }
    public void deleteService(long serviceId) {
        serviceRepository.deleteById(serviceId);
    }
    public ServiceResponse updateService(long ServiceId, ServiceUpdateRequest request){
        SalonService service = serviceRepository.findByServiceId(ServiceId)
                .orElseThrow(() -> new RuntimeException("Service with ID '" + ServiceId + "' not found"));
        service.setServiceName(request.getServiceName());
        service.setPrice(request.getPrice());
        service.setDescription(request.getDescription());
        service.setDuration(request.getDuration());
        return modelMapper.map(serviceRepository.save(service), ServiceResponse.class);
    }



}
