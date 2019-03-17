package pl.edu.wszib.springwithtests.service.impl;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.*;
import pl.edu.wszib.springwithtests.NotFoundException;
import pl.edu.wszib.springwithtests.dao.ProductDao;
import pl.edu.wszib.springwithtests.dao.ShoppingBasketDao;
import pl.edu.wszib.springwithtests.dao.ShoppingBasketItemDao;
import pl.edu.wszib.springwithtests.dto.ProductDTO;
import pl.edu.wszib.springwithtests.dto.ShoppingBasketDTO;
import pl.edu.wszib.springwithtests.model.Product;
import pl.edu.wszib.springwithtests.model.ShoppingBasket;
import pl.edu.wszib.springwithtests.model.ShoppingBasketItem;
import pl.edu.wszib.springwithtests.model.Vat;

import java.util.Collections;
import java.util.Optional;

@RunWith(JUnit4.class)
public class ShoppingBasketServiceImplTest {

    @InjectMocks
    ShoppingBasketServiceImpl shoppingBasketService;

    @Mock
    ProductDao productDao;
    @Mock
    ShoppingBasketDao shoppingBasketDao;
    @Mock
    ShoppingBasketItemDao shoppingBasketItemDao;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Spy
    Mapper mapper = new DozerBeanMapper();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testShoppingBasketIdNotExist() {

        Integer testShoppingBasketId = 34;
        ProductDTO productDTO = Mockito.mock(ProductDTO.class);

        expectedException.expect(NotFoundException.class);

        shoppingBasketService.addProduct(testShoppingBasketId, productDTO);

    }

    @Test
    public void testShoppingBasketExistProductNotExist() {
        Integer testShoppingBasketId = 34;
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Test");
        productDTO.setId(34);
        productDTO.setCost(691d);
        productDTO.setVat(Vat.VALUE_8);

        ShoppingBasket shoppingBasket = new ShoppingBasket();
        shoppingBasket.setId(testShoppingBasketId);

        Mockito.when(shoppingBasketDao.findById(testShoppingBasketId))
                .thenReturn(Optional.of(shoppingBasket));

        expectedException.expect(NotFoundException.class);
        shoppingBasketService.addProduct(testShoppingBasketId, productDTO);
    }

    @Test
    public void testShoppingBasketExistProductExistShoppingBasketItemExist() {
        Integer testShoppingBasketId = 34;
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Test");
        productDTO.setId(34);
        productDTO.setCost(691d);
        productDTO.setVat(Vat.VALUE_8);

        ShoppingBasket shoppingBasket = new ShoppingBasket();
        shoppingBasket.setId(testShoppingBasketId);

        Mockito.when(shoppingBasketDao.findById(testShoppingBasketId))
                .thenReturn(Optional.of(shoppingBasket));

        Mockito.when(productDao.existsById(productDTO.getId()))
                .thenReturn(true);

        ShoppingBasketItem shoppingBasketItem = new ShoppingBasketItem();
        shoppingBasketItem.setId(654);
        shoppingBasketItem.setShoppingBasket(shoppingBasket);
        shoppingBasketItem.setAmount(3);
        shoppingBasketItem.setProduct(mapper.map(productDTO, Product.class));

        Mockito.when(shoppingBasketItemDao
                .findByProductIdAndShoppingBasketId(
                        productDTO.getId(), testShoppingBasketId))
                .thenReturn(shoppingBasketItem);


        Mockito.when(shoppingBasketItemDao.
                findAllByShoppingBasketId(testShoppingBasketId))
                .thenReturn(Collections.singletonList(shoppingBasketItem));

        ShoppingBasketDTO result = shoppingBasketService
                .addProduct(testShoppingBasketId, productDTO);

        Mockito.verify(shoppingBasketItemDao).save(shoppingBasketItem);

        Assert.assertEquals(testShoppingBasketId, result.getId());
        Assert.assertEquals(1, result.getItems().size());
        Assert.assertEquals(productDTO.getId(), result.getId());

        Assert.assertTrue(result.getItems()
                .stream()
                .anyMatch(i -> i.getProduct().getId()
                        .equals(productDTO.getId())));

        Assert.assertTrue(result.getItems()
                .stream()
                .filter(i -> i.getProduct().getId()
                        .equals(productDTO.getId()))
                .findFirst()
                .map(i -> i.getAmount() == 4)
                .orElse(false));
    }

    @Test
    public void testShoppingBasketExistProductExistShoppingBasketItemNotExist() {
        Integer testShoppingBasketId = 34;
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("Test");
        productDTO.setId(34);
        productDTO.setCost(691d);
        productDTO.setVat(Vat.VALUE_8);

        ShoppingBasket shoppingBasket = new ShoppingBasket();
        shoppingBasket.setId(testShoppingBasketId);

        Mockito.when(shoppingBasketDao.findById(testShoppingBasketId))
                .thenReturn(Optional.of(shoppingBasket));

        Mockito.when(productDao.existsById(productDTO.getId()))
                .thenReturn(true);

        ShoppingBasketItem shoppingBasketItem = new ShoppingBasketItem();
        shoppingBasketItem.setId(654);
        shoppingBasketItem.setShoppingBasket(shoppingBasket);
        shoppingBasketItem.setAmount(1);
        shoppingBasketItem.setProduct(mapper.map(productDTO, Product.class));

        Mockito.when(shoppingBasketItemDao.
                findAllByShoppingBasketId(testShoppingBasketId))
                .thenReturn(Collections.singletonList(shoppingBasketItem));

        ShoppingBasketDTO result = shoppingBasketService
                .addProduct(testShoppingBasketId, productDTO);

        Assert.assertEquals(testShoppingBasketId, result.getId());
        Assert.assertEquals(1, result.getItems().size());
        Assert.assertEquals(productDTO.getId(), result.getId());

        Assert.assertTrue(result.getItems()
                .stream()
                .anyMatch(i -> i.getProduct().getId()
                        .equals(productDTO.getId())));

        Assert.assertTrue(result.getItems()
                .stream()
                .filter(i -> i.getProduct().getId()
                        .equals(productDTO.getId()))
                .findFirst()
                .map(i -> i.getAmount() == 1)
                .orElse(false));
    }
}
