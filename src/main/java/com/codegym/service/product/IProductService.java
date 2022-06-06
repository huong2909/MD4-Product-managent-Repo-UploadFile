package com.codegym.service.product;

import com.codegym.model.category.Category;
import com.codegym.model.product.Product;
import com.codegym.service.IGeneralService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProductService extends IGeneralService<Product> {

    Iterable<Product> findAllByCategory(Category category);

    Page<Product> findAll(Pageable pageable);

    Page<Product> findAllByNameContaining(String name, Pageable pageable);

    Iterable<Product> sortByPrice();
}
