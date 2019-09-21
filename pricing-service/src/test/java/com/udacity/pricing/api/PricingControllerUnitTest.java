package com.udacity.pricing.api;


import com.udacity.pricing.domain.price.Price;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestParam;

import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.List;


@RunWith(SpringRunner.class)
@WebMvcTest(PricingController.class)
@TestPropertySource(properties = "server.port=8082")
public class PricingControllerUnitTest {

    @MockBean
    PricingController pricingController;
    @Autowired
    private MockMvc mockMvc;
    Long id = Long.valueOf(3); ;

    @Test
    public  void getPrice() throws Exception {

            mockMvc.perform(get("/services/price?vehicleId=3 "))
                    .andExpect(status().isOk());

        verify(pricingController, times(1)).get(id);

        }





    }

