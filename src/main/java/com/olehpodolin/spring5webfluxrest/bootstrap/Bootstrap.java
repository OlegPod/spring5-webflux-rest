package com.olehpodolin.spring5webfluxrest.bootstrap;

import com.olehpodolin.spring5webfluxrest.domain.Category;
import com.olehpodolin.spring5webfluxrest.domain.Vendor;
import com.olehpodolin.spring5webfluxrest.repositories.CategoryRepository;
import com.olehpodolin.spring5webfluxrest.repositories.VendorRepository;
import org.springframework.boot.CommandLineRunner;

public class Bootstrap implements CommandLineRunner {

    private VendorRepository vendorRepository;
    private CategoryRepository categoryRepository;

    public Bootstrap(VendorRepository vendorRepository, CategoryRepository categoryRepository) {
        this.vendorRepository = vendorRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        loadVendors();
        loadCategories();
    }

    private void loadVendors() {

        Vendor vendor1 = new Vendor();
        vendor1.setFirstName("Eric");
        vendor1.setLastName("Covalenko");

        Vendor vendor2 = new Vendor();
        vendor2.setFirstName("Jeff");
        vendor2.setLastName("Jones");
    }

    private void loadCategories() {

        Category category1 = new Category();
        category1.setDescription("Tropical");

        Category category2 = new Category();
        category2.setDescription("Mexican");

        Category category3 = new Category();
        category3.setDescription("American");
    }
}
