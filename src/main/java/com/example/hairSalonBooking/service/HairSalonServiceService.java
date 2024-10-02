package com.example.hairSalonBooking.service;

import com.example.hairSalonBooking.entity.SalonService;
import com.example.hairSalonBooking.entity.Skill;
import com.example.hairSalonBooking.exception.AppException;
import com.example.hairSalonBooking.exception.ErrorCode;
import com.example.hairSalonBooking.model.request.CreateServiceRequest;
import com.example.hairSalonBooking.model.request.ServiceUpdateRequest;
import com.example.hairSalonBooking.model.response.ServiceResponse;
import com.example.hairSalonBooking.repository.ServiceRepository;
import com.example.hairSalonBooking.repository.SkillRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
    public SalonService createService(MultipartFile file, CreateServiceRequest createServiceRequest) {
        try {
            SalonService salonService = modelMapper.map(createServiceRequest, SalonService.class);
            salonService.setImage(imagesService.uploadImage(file));
            Skill skill = skillRepository.findSkillBySkillName(createServiceRequest.getSkillName());
            salonService.setSkill(skill);
            return serviceRepository.save(salonService);
        }catch (IOException e){
            throw new AppException(ErrorCode.CAN_NOT_UPLOAD_IMAGE);
        }
    }
    public List<SalonService> getAllServices(){
        return serviceRepository.findAll();
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
