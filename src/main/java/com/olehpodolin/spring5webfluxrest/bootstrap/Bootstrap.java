package com.olehpodolin.spring5webfluxrest.bootstrap;

import com.olehpodolin.spring5webfluxrest.domain.Category;
import com.olehpodolin.spring5webfluxrest.domain.Vendor;
import com.olehpodolin.spring5webfluxrest.repositories.CategoryRepository;
import com.olehpodolin.spring5webfluxrest.repositories.VendorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Bootstrap implements CommandLineRunner {

    private VendorRepository vendorRepository;
    private CategoryRepository categoryRepository;

    public Bootstrap(VendorRepository vendorRepository, CategoryRepository categoryRepository) {
        this.vendorRepository = vendorRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        if (vendorRepository.count().block() == 0) {
            //injecting data
            System.out.println("Loading Vendors Initial Data from Bootstap class");
            loadVendors();
        }

        if (categoryRepository.count().block() == 0) {
            //injecting data
            System.out.println("Loading Categories Initial Data from Bootstap class");
            loadCategories();
        }

    }

    private void loadVendors() {

        vendorRepository.save(Vendor.builder()
                .firstName("Jack")
                .lastName("Bone").build()).block();

        vendorRepository.save(Vendor.builder()
                .firstName("Nikki")
                .lastName("Chuck").build()).block();

        vendorRepository.save(Vendor.builder()
                .firstName("Man")
                .lastName("Marlboro").build()).block();

        vendorRepository.save(Vendor.builder()
                .firstName("Henry")
                .lastName("Sharp").build()).block();

        System.out.println("Loaded Vendors from Bootstrap class: " + vendorRepository.count().block());
    }

    private void loadCategories() {

        categoryRepository.save(Category.builder()
                .description("Tropical").build()).block();

        categoryRepository.save(Category.builder()
                .description("American").build()).block();

        categoryRepository.save(Category.builder()
                .description("European").build()).block();

        categoryRepository.save(Category.builder()
                .description("Slavic").build()).block();

        System.out.println("Loaded Categories from Bootstrap class: " + categoryRepository.count().block());
    }
}
