package com.eodigo

import com.eodigo.util.DotenvInitializer
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration

@SpringBootTest
@ContextConfiguration(initializers = [DotenvInitializer::class])
@ActiveProfiles("test")
class EodigoApplicationTests {

    @Test fun contextLoads() {}
}
