package com.olehpodolin.spring5webfluxrest.controllers;

import com.olehpodolin.spring5webfluxrest.domain.Vendor;
import com.olehpodolin.spring5webfluxrest.repositories.VendorRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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
        given(vendorRepository.findAll())
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
        given(vendorRepository.findById("id"))
                .willReturn(Mono.just(Vendor.builder().firstName("VendorTest").lastName("lastForTest").build()));

        webTestClient.get()
                .uri("/api/v1/vendors/id")
                .exchange()
                .expectBody(Vendor.class);
    }

    @Test
    public void createVendor() {
        given(vendorRepository.saveAll(any(Publisher.class)))
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
        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(updatedVendor);

        webTestClient.put()
                .uri("/api/v1/vendors/someId")
                .body(vendorToUpdate, Vendor.class)
                .exchange()
                .expectBody(Vendor.class)
                .value(vendor -> vendor.getLastName().equals(updatedVendor.block().getLastName()));
    }

    @Test
    public void testPatchVendorWithChanges() {
        //given
        Mono<Vendor> vendorMonoToPatch = Mono.just(Vendor.builder().id("testId").lastName("Test Patch").build());
        Mono<Vendor> vendorMonoPatched = Mono.just(Vendor.builder().id("testId").lastName("Patched lastName").build());

        //when
        given(vendorRepository.findById(anyString()))
                .willReturn(vendorMonoToPatch);
        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(vendorMonoPatched);

        webTestClient.patch()
                .uri("/api/v1/vendors/testId")
                .body(vendorMonoPatched, Vendor.class)
                .exchange()
                .expectBody(Vendor.class)
                .value(vendor -> vendor.getLastName().equals(vendorMonoPatched.block().getLastName()));

        verify(vendorRepository).save(any());
    }

    @Test
    public void testPatchVendorNoChanges() {
        //given
        Mono<Vendor> vendorToUpdate = Mono.just(Vendor.builder().build());

        //when
        given(vendorRepository.findById(anyString()))
                .willReturn(vendorToUpdate);
        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        webTestClient.patch()
                .uri("/api/v1/vendors/someId")
                .body(vendorToUpdate, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();

        verify(vendorRepository, never()).save(any());
    }
}