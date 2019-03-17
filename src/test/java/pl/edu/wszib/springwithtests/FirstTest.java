package pl.edu.wszib.springwithtests;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.util.*;

@RunWith(JUnit4.class)
public class FirstTest {

    @BeforeClass
    public static void przedWszystkimi(){
        //odpali się dokładnie raz

    }

    @Before
    public void przed(){
        //odpali się przed każdym testem
    }

    @Test
    public void test(){
        assert true; //sprawdza czy to co jest po niej jest prawda, false nie zadziala
        Assert.assertEquals("cos poszlo nie tak", true, false);
    }

    @Test
    public void test2(){
        //final przy klasie nie pozwala po niej dziedziczyc
        //stad aby utworzyc mock klasa nie moze byc final, metoda tez nie moze byc final
        List list = Mockito.mock(List.class);
        Mockito.when(list.size()).thenReturn(1);
//        Mockito.when(list.size()).thenThrow(NullPointerException.class);
        Assert.assertEquals("Nie ma jednego elementu", 1, list.size());

    }


    @After
    public void po(){

    }

    @AfterClass
    public static void poWszystkim(){

    }
}
