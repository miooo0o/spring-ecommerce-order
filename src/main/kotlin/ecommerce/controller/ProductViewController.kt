package ecommerce.controller

import ecommerce.dto.ProductFormDto
import ecommerce.service.ProductService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ProductViewController(private val productService: ProductService) {
    @GetMapping("/admin/products")
    fun table(model: Model): String {
        model.addAttribute("products", productService.read())
        model.addAttribute("product", ProductFormDto())
        return "products"
    }
}
