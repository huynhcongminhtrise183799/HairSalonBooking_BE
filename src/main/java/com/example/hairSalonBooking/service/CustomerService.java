package com.example.hairSalonBooking.service;

import com.example.hairSalonBooking.entity.Account;
import com.example.hairSalonBooking.exception.AppException;
import com.example.hairSalonBooking.exception.ErrorCode;
import com.example.hairSalonBooking.exception.NotFoundException;
import com.example.hairSalonBooking.model.request.UpdateCustomerRequest;
import com.example.hairSalonBooking.model.response.UpdateCustomerResponse;
import com.example.hairSalonBooking.repository.CustomerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    @Autowired
    CustomerRepository customerRepository;
    //@Autowired
    //ModelMapper modelMapper;
    public UpdateCustomerRequest updateCustomer(UpdateCustomerRequest request, long AccountId){
        Account account = customerRepository.findAccountByAccountid(AccountId);
        if(account == null){
            throw new AppException(ErrorCode.ACCOUNT_Not_Found_Exception);
        }
        account.setFullname(request.getFullName());
        account.setPhone(request.getPhone());
        account.setDob(request.getDob());
        account.setEmail(request.getEmail());
        customerRepository.save(account);
        return request;

    }
}
