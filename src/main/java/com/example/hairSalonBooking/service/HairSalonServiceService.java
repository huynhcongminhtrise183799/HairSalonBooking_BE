package com.example.hairSalonBooking.service;

import com.example.hairSalonBooking.entity.Account;
import com.example.hairSalonBooking.entity.SalonService;
import com.example.hairSalonBooking.entity.Skill;
import com.example.hairSalonBooking.enums.Role;
import com.example.hairSalonBooking.model.request.CreateServiceRequest;
import com.example.hairSalonBooking.model.request.BookingStylits;
import com.example.hairSalonBooking.model.request.SearchServiceNameRequest;
import com.example.hairSalonBooking.model.request.ServiceUpdateRequest;
import com.example.hairSalonBooking.model.response.*;
import com.example.hairSalonBooking.repository.AccountRepository;
import com.example.hairSalonBooking.repository.ServiceRepository;
import com.example.hairSalonBooking.repository.SkillRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

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
    public ServiceResponse createService(CreateServiceRequest createServiceRequest) {

        //SalonService salonService = modelMapper.map(createServiceRequest, SalonService.class);
        SalonService salonService = new SalonService();
        salonService.setPrice(createServiceRequest.getPrice());
        salonService.setDuration(createServiceRequest.getDuration());
        salonService.setDescription(createServiceRequest.getDescription());
        salonService.setImage(createServiceRequest.getImage());
        salonService.setServiceName(createServiceRequest.getServiceName());
        Skill skill = skillRepository.findSkillBySkillId(createServiceRequest.getSkillId());
        salonService.setSkill(skill);
        serviceRepository.save(salonService);
        ServiceResponse response = new ServiceResponse();
        response.setId(salonService.getServiceId());
        response.setServiceName(salonService.getServiceName());
        response.setDescription(salonService.getDescription());
        response.setPrice(salonService.getPrice());
        response.setDuration(salonService.getDuration());
        response.setImage(salonService.getImage());
        response.setSkillName(salonService.getSkill().getSkillName());
        return response;

    }
    public List<ServiceResponse> getAllServicesActive(){
        List<SalonService> services = serviceRepository.findByIsDeleteFalse();
        List<ServiceResponse> responses = new ArrayList<>();
        for(SalonService service : services){
            ServiceResponse serviceResponse = new ServiceResponse();
            serviceResponse.setId(service.getServiceId());
            serviceResponse.setServiceName(service.getServiceName());
            serviceResponse.setPrice(service.getPrice());
            serviceResponse.setImage(service.getImage());
            serviceResponse.setDuration(service.getDuration());
            serviceResponse.setDescription(service.getDescription());
            responses.add(serviceResponse);
        }
        return responses;
    }


    public List<ServiceResponse> searchServiceByName(SearchServiceNameRequest serviceName) {
        List<SalonService> services = serviceRepository.findByServiceNameContaining(serviceName.getName());
        List<ServiceResponse> responses = new ArrayList<>();
        for(SalonService service : services){
            ServiceResponse response = new ServiceResponse();
            response.setId(service.getServiceId());
            response.setServiceName(service.getServiceName());
            response.setDescription(service.getDescription());
            response.setPrice(service.getPrice());
            response.setImage(service.getImage());
            response.setDuration(service.getDuration());
            responses.add(response);
        }
        return responses;
    }
    public Optional<SalonService> searchServiceId(long serviceId) {
        return serviceRepository.findByServiceId(serviceId)
                .or(() -> {
                    throw new RuntimeException("Service not found");
                });
    }
    public void deleteService(long serviceId) {
        SalonService salonService = serviceRepository.getServiceById(serviceId);
        salonService.setDelete(true);
        serviceRepository.save(salonService);
    }
    public ServiceResponse updateService(long ServiceId, ServiceUpdateRequest request){
        SalonService service = serviceRepository.findByServiceId(ServiceId)
                .orElseThrow(() -> new RuntimeException("Service with ID '" + ServiceId + "' not found"));
        service.setServiceName(request.getServiceName());
        service.setPrice(request.getPrice());
        service.setDescription(request.getDescription());
        service.setDuration(request.getDuration());
        service.setImage(request.getImage());
        service.setDelete(request.isDelelte());
        return modelMapper.map(serviceRepository.save(service), ServiceResponse.class);
    }

    // cái này chia luồng page Service
    public ServicePageResponse getAllServicePage(int page, int size) {
        Page<SalonService> servicePage = serviceRepository.findAll(PageRequest.of(page, size));
        ServicePageResponse servicePageResponse = new ServicePageResponse();
        servicePageResponse.setPageNumber(servicePage.getNumber());
        servicePageResponse.setTotalPages(servicePage.getTotalPages());
        servicePageResponse.setTotalElements(servicePage.getTotalElements());
        servicePageResponse.setContent(servicePage.getContent());
        return servicePageResponse;
    }

    public List<ServiceResponse> getAllServices() {
        // Lấy tất cả dịch vụ từ cơ sở dữ liệu
        List<SalonService> services = serviceRepository.findAll();

        // Chuyển đổi từng đối tượng SalonService thành ServiceResponse
        List<ServiceResponse> responses = services.stream().map(service -> {
            ServiceResponse serviceResponse = new ServiceResponse();
            serviceResponse.setId(service.getServiceId());
            serviceResponse.setServiceName(service.getServiceName());
            serviceResponse.setPrice(service.getPrice());
            serviceResponse.setImage(service.getImage());
            serviceResponse.setDuration(service.getDuration());
            serviceResponse.setDescription(service.getDescription());
            return serviceResponse;
        }).collect(Collectors.toList());

        return responses;
    }
}
