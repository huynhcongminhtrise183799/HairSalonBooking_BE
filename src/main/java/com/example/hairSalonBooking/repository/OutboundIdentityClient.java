package com.example.hairSalonBooking.repository;

import com.example.hairSalonBooking.model.request.ExchangeTokenRequest;
import com.example.hairSalonBooking.model.response.ExchangeTokenResponse;
import feign.QueryMap;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "outbound-identity", url ="https://accounts.google.com/o/oauth2/token")
public interface OutboundIdentityClient {
   @PostMapping(value = "/login", produces = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
   ExchangeTokenResponse exchangeToken(@QueryMap ExchangeTokenRequest  request) ;


}
