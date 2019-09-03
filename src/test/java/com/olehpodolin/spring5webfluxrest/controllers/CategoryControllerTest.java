package com.olehpodolin.spring5webfluxrest.controllers;

import com.olehpodolin.spring5webfluxrest.domain.Category;
import com.olehpodolin.spring5webfluxrest.repositories.CategoryRepository;
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

public class CategoryControllerTest {

    WebTestClient webTestClient;
    CategoryRepository categoryRepository;
    CategoryController categoryController;

    @Before
    public void setUp() throws Exception {
        categoryRepository = Mockito.mock(CategoryRepository.class);
        categoryController = new CategoryController(categoryRepository);
        webTestClient = WebTestClient.bindToController(categoryController).build();
    }

    @Test
    public void list() {
        given(categoryRepository.findAll())
                .willReturn(Flux.just(Category.builder().description("Category1").build(),
                                                    Category.builder().description("Category2").build()));

        webTestClient.get()
                .uri("/api/v1/categories/")
                .exchange()
                .expectBodyList(Category.class)
                .hasSize(2);
    }

    @Test
    public void getById() {
        given(categoryRepository.findById("someId"))
                .willReturn(Mono.just(Category.builder().description("Category").build()));

        webTestClient.get()
                .uri("/api/v1/categories/someId/")
                .exchange()
                .expectBody(Category.class);
    }

    @Test
    public void testCreateCategory() {
        given(categoryRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Category.builder().build()));

        Mono<Category> categoryToSave = Mono.just(Category.builder().description("Cat 1").build());

        webTestClient.post()
                .uri("/api/v1/categories/")
                .body(categoryToSave, Category.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    public void testUpdateCategory() {
        //given
        Mono<Category> categoryToUpdate = Mono.just(Category.builder().id("testId").description("Test Update").build());
        Mono<Category> updatedCategory = Mono.just(Category.builder().id("testId").description("Updated Category").build());

        //when
        given(categoryRepository.save(any(Category.class)))
                .willReturn(updatedCategory);

        webTestClient.put()
                .uri("/api/v1/categories/testId")
                .body(categoryToUpdate, Category.class)
                .exchange()
                .expectBody(Category.class)
                .value(category -> category.getDescription().equals(updatedCategory.block().getDescription()));
    }

    @Test
    public void testPatchCategoryWithChanges() {
        //given
        Mono<Category> categoryToPatch = Mono.just(Category.builder().id("testId").description("Test Patch").build());
        Mono<Category> patchedCategory = Mono.just(Category.builder().id("testId").description("Patched Category").build());

        //when
        given(categoryRepository.findById(anyString()))
                .willReturn(categoryToPatch);
        given(categoryRepository.save(any(Category.class)))
                .willReturn(patchedCategory);

        webTestClient.patch()
                .uri("/api/v1/categories/testId")
                .body(patchedCategory, Category.class)
                .exchange()
                .expectBody(Category.class)
                .value(category -> category.getDescription().equals(patchedCategory.block().getDescription()));

        verify(categoryRepository).save(any());
    }

    @Test
    public void testPatchCategoryWithoutChanges() {
        //given
        Mono<Category> categoryToPatch = Mono.just(Category.builder().build());

        //when
        given(categoryRepository.findById(anyString()))
                .willReturn(Mono.just(Category.builder().build()));
        given(categoryRepository.save(any(Category.class)))
                .willReturn(Mono.just(Category.builder().build()));

        webTestClient.patch()
                .uri("/api/v1/categories/testId")
                .body(categoryToPatch, Category.class)
                .exchange()
                .expectStatus()
                .isOk();

        verify(categoryRepository, never()).save(any());
    }
}