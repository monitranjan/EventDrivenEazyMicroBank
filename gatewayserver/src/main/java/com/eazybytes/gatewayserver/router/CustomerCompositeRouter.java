package com.eazybytes.gatewayserver.router;

import com.eazybytes.gatewayserver.handler.CustomerCompositeHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration(proxyBeanMethods = false)
public class CustomerCompositeRouter {

    @Bean
    public RouterFunction<ServerResponse> route(CustomerCompositeHandler customerCompositeHandler) {
        return RouterFunctions.route(RequestPredicates.GET("/api/composite/fetchCustomerSummary")
                        .and(RequestPredicates.accept(MediaType.APPLICATION_JSON))
                        .and(RequestPredicates.queryParam("mobileNumber", param -> true)),
                customerCompositeHandler::fetchCustomerSummary);
    }
}