package pl.edu.wszib.springwithtests;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.dozer.Mapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.edu.wszib.springwithtests.dao.ProductDao;
import pl.edu.wszib.springwithtests.dao.ShoppingBasketDao;
import pl.edu.wszib.springwithtests.dao.ShoppingBasketItemDao;
import pl.edu.wszib.springwithtests.dto.ProductDTO;
import pl.edu.wszib.springwithtests.dto.ShoppingBasketDTO;
import pl.edu.wszib.springwithtests.model.Product;
import pl.edu.wszib.springwithtests.model.ShoppingBasket;
import pl.edu.wszib.springwithtests.model.ShoppingBasketItem;
import pl.edu.wszib.springwithtests.model.Vat;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SpringMvcTest {

    @Autowired
    MockMvc mockMvc;


    @Autowired
    Mapper mapper;

    @Autowired
    ShoppingBasketDao shoppingBasketDao;

    @Autowired
    ProductDao productDao;

    @Autowired
    ShoppingBasketItemDao shoppingBasketItemDao;

    @Test
    public void testShoppingBasketNotExist() throws Exception {

        int testShoppingBasketId = 654;
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("test");
        productDTO.setCost(32d);
        productDTO.setId(1);
        productDTO.setVat(Vat.VALUE_8);


        mockMvc.perform(MockMvcRequestBuilders
                .post("/shoppingBasket/add")
                .contentType("application/json")
                .content(new Gson().toJson(productDTO))
                .param("shoppingBasketId", String.valueOf(testShoppingBasketId)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testShoppingBasketExistProductNotExist() throws Exception {


        ShoppingBasket shoppingBasket = new ShoppingBasket();
        shoppingBasket = shoppingBasketDao.save(shoppingBasket);

        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("test");
        productDTO.setCost(32d);
        productDTO.setId(1);
        productDTO.setVat(Vat.VALUE_8);


        mockMvc.perform(MockMvcRequestBuilders
                .post("/shoppingBasket/add")
                .contentType("application/json")
                .content(new Gson().toJson(productDTO))
                .param("shoppingBasketId", String.valueOf(shoppingBasket.getId())))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testShoppingBasketExistProductExistShoppingBasketItemExist() throws Exception {
        ShoppingBasket shoppingBasket = new ShoppingBasket();
        shoppingBasket = shoppingBasketDao.save(shoppingBasket);

        Product product = new Product();
        product.setCost(68d);
        product.setName("product");
        product.setVat(Vat.VALUE_8);

        product = productDao.save(product);

        ShoppingBasketItem shoppingBasketItem = new ShoppingBasketItem();
        shoppingBasketItem.setProduct(product);
        shoppingBasketItem.setAmount(1);
        shoppingBasketItem.setShoppingBasket(shoppingBasket);

        shoppingBasketItem = shoppingBasketItemDao.save(shoppingBasketItem);

        ProductDTO productDTO = mapper.map(product, ProductDTO.class);

        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/shoppingBasket/add")
                        .contentType("application/json")
                        .content(new Gson().toJson(productDTO))
                        .param("shoppingBasketId", String.valueOf(shoppingBasket.getId())))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn();

        ShoppingBasketDTO shoppingBasketDTO = new Gson()
                .fromJson(result.getResponse()
                        .getContentAsString(),
                        ShoppingBasketDTO.class);

        Assert.assertEquals(shoppingBasket.getId(), shoppingBasketDTO.getId());
    }

    @Test
    public void testShoppingBasketExistProductExistShoppingBasketItemNotExist() throws Exception {
        ShoppingBasket shoppingBasket = new ShoppingBasket();
        shoppingBasket = shoppingBasketDao.save(shoppingBasket);

        Product product = new Product();
        product.setCost(68d);
        product.setName("product");
        product.setVat(Vat.VALUE_8);

        product = productDao.save(product);

        ProductDTO productDTO = mapper.map(product, ProductDTO.class);

        MvcResult result = mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/shoppingBasket/add")
                        .contentType("application/json")
                        .content(new Gson().toJson(productDTO))
                        .param("shoppingBasketId", String.valueOf(shoppingBasket.getId())))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andReturn();

        ShoppingBasketDTO shoppingBasketDTO = new Gson()
                .fromJson(result.getResponse()
                                .getContentAsString(),
                        ShoppingBasketDTO.class);

        Assert.assertEquals(shoppingBasket.getId(), shoppingBasketDTO.getId());
    }
}
