package com.codegym.controller;

import com.codegym.model.category.Category;
import com.codegym.model.product.Product;
import com.codegym.model.product.ProductForm;
import com.codegym.service.category.ICategoryService;
import com.codegym.service.product.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/product")
public class ProductController {
    @Value("${file-upload}")
    private String fileUpload;

    @Autowired
    public IProductService productService;

    @Autowired
    private ICategoryService categoryService;

    @ModelAttribute("categories")
    public Iterable<Category> categories() {
        return categoryService.findAll();
    }


    @GetMapping("")
    public ModelAndView showList(@RequestParam("search") Optional<String> search, @PageableDefault(sort = {"name"}, direction = Sort.Direction.ASC, size = 2) Pageable pageable) {
        Page<Product> products;
        if (search.isPresent()) {
            products = productService.findAllByNameContaining(search.get(), pageable);
        } else {
            products = productService.findAll(pageable);
        }
        ModelAndView modelAndView = new ModelAndView("/product/list");
        modelAndView.addObject("products", products);
        return modelAndView;
    }

    @GetMapping("/create-product")
    public ModelAndView showFormCreate() {
        ModelAndView modelAndView = new ModelAndView("/product/create");
        modelAndView.addObject("product", new Product());
        return modelAndView;
    }

//    @PostMapping("/create-product")
//    public ModelAndView saveProduct(@ModelAttribute("product") Product product) {
//        productService.save(product);
//        ModelAndView modelAndView = new ModelAndView("/product/create");
//        modelAndView.addObject("product", product);
//        modelAndView.addObject("message", "New product created successfully");
//        return modelAndView;
//    }

    @PostMapping("/create-product")
    public ModelAndView saveProduct(@ModelAttribute ProductForm productForm) {
        MultipartFile multipartFile = productForm.getImage();
        String fileName = multipartFile.getOriginalFilename();
        try {
            FileCopyUtils.copy(productForm.getImage().getBytes(), new File(fileUpload + fileName));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        Product product = new Product(productForm.getId(), productForm.getName(), productForm.getPrice(), fileName, productForm.getCategory());
        productService.save(product);
        ModelAndView modelAndView = new ModelAndView("/product/create");
        modelAndView.addObject("productForm", productForm);
        modelAndView.addObject("message", "Created new product successfully !");
        return modelAndView;
    }

    @GetMapping("/edit-product/{id}")
    public ModelAndView showEditForm(@PathVariable Long id) {
        Optional<Product> product = productService.findById(id);
        if (product.isPresent()) {
            ModelAndView modelAndView = new ModelAndView("/product/edit");
            modelAndView.addObject("product", product.get());
            return modelAndView;

        } else {
            ModelAndView modelAndView = new ModelAndView("/error.404");
            return modelAndView;
        }
    }

    @PostMapping("/edit-product/{id}")
    public ModelAndView updateBlog(@ModelAttribute ProductForm productForm, @PathVariable Long id) {
        MultipartFile multipartFile = productForm.getImage();
        Product product = productService.findById(id).get();
        String fileName = multipartFile.getOriginalFilename();
        if (fileName.equals("")) {
             fileName = product.getImage();
            product = new Product(productForm.getId(), productForm.getName(), productForm.getPrice(), fileName, productForm.getCategory());
        } else {
             fileName = multipartFile.getOriginalFilename();
            try {
                FileCopyUtils.copy(productForm.getImage().getBytes(), new File(fileUpload + fileName));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            product = new Product(productForm.getId(), productForm.getName(), productForm.getPrice(), fileName, productForm.getCategory());

        }
        productService.save(product);
        ModelAndView modelAndView = new ModelAndView("/product/edit");
        modelAndView.addObject("product", product);
        modelAndView.addObject("message", "Product updated successfully");
        return modelAndView;
    }

    @GetMapping("/delete-product/{id}")
    public ModelAndView showDeleteForm(@PathVariable Long id) {
        Optional<Product> product = productService.findById(id);
        if (product.isPresent()) {
            ModelAndView modelAndView = new ModelAndView("/product/delete");
            modelAndView.addObject("product", product.get());
            return modelAndView;

        } else {
            ModelAndView modelAndView = new ModelAndView("/error.404");
            return modelAndView;
        }
    }

    @PostMapping("/delete-product")
    public ModelAndView deleteProduct(@ModelAttribute("product") Product product) {
        productService.remove(product.getId());
        ModelAndView modelAndView = new ModelAndView("/product/list");
        modelAndView.addObject("product", new Product());
        return modelAndView;
    }

    @GetMapping("/sortByPrice")
    public ModelAndView sortByPrice() {
        Iterable<Product> products = productService.sortByPrice();
        ModelAndView modelAndView = new ModelAndView("/product/list");
        modelAndView.addObject("products", products);
        return modelAndView;
    }


}
