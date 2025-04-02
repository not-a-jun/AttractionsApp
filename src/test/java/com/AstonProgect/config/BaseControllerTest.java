package com.AstonProgect.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

public abstract class BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

   protected String asJsonString(final Object obj) throws Exception {
       return objectMapper.writeValueAsString(obj);
   }
}
