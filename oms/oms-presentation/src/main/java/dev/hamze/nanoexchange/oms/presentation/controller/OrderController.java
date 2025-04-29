package dev.hamze.nanoexchange.oms.presentation.controller;

import dev.hamze.nanoexchange.oms.presentation.api.data.order.create.CreateOrderEdgeRequestDTO;
import dev.hamze.nanoexchange.oms.presentation.api.data.order.create.CreateOrderEdgeResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/orders")
public class OrderController {

    @PostMapping
    public ResponseEntity<CreateOrderEdgeResponseDTO> createOrder(@RequestBody CreateOrderEdgeRequestDTO request) {

        return ResponseEntity.ok(new CreateOrderEdgeResponseDTO());
    }
}
