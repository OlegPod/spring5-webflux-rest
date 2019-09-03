package com.olehpodolin.spring5webfluxrest.controllers;

import com.olehpodolin.spring5webfluxrest.domain.Vendor;
import com.olehpodolin.spring5webfluxrest.repositories.VendorRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

public class VendorControllerTest {

    WebTestClient webTestClient;
    VendorRepository vendorRepository;
    VendorController vendorController;

    @Before
    public void setUp() throws Exception {

        vendorRepository = Mockito.mock(VendorRepository.class);
        vendorController = new VendorController(vendorRepository);
        webTestClient = WebTestClient.bindToController(vendorController).build();
    }

    @Test
    public void getList() {
        BDDMockito.given(vendorRepository.findAll())
                .willReturn(Flux.just(Vendor.builder().firstName("Vendor1first").lastName("Vendor1last").build(),
                                                    Vendor.builder().firstName("Vendor2first").lastName("Vendor2last").build()));

        webTestClient.get()
                .uri("/api/v1/vendors/")
                .exchange()
                .expectBodyList(Vendor.class)
                .hasSize(2);
    }

    @Test
    public void getById() {
        BDDMockito.given(vendorRepository.findById("id"))
                .willReturn(Mono.just(Vendor.builder().firstName("VendorTest").lastName("lastForTest").build()));

        webTestClient.get()
                .uri("/api/v1/vendors/id")
                .exchange()
                .expectBody(Vendor.class);
    }

    @Test
    public void createVendor() {
        BDDMockito.given(vendorRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Vendor.builder().build()));

        Flux<Vendor> vendorToSave = Flux.just(Vendor.builder().firstName("firstname").lastName("lastname").build());

        webTestClient.post()
                .uri("/api/v1/vendors")
                .body(vendorToSave, Vendor.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    public void testUpdateVendor() {
        //given
        Mono<Vendor> vendorToUpdate = Mono.just(Vendor.builder().firstName("Vendor").lastName("To Update")
                .id("someId").build());
        Mono<Vendor> updatedVendor = Mono.just(Vendor.builder().firstName("Vendor").lastName("Updated")
                .id("someId").build());

        //when
        BDDMockito.given(vendorRepository.save(any(Vendor.class)))
                .willReturn(updatedVendor);

        webTestClient.put()
                .uri("/api/v1/vendors/someId")
                .body(vendorToUpdate, Vendor.class)
                .exchange()
                .expectBody(Vendor.class)
                .value(vendor -> vendor.getLastName().equals(updatedVendor.block().getLastName()));
    }
}