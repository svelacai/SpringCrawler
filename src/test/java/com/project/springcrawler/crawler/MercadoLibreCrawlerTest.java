package com.project.springcrawler.crawler;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MercadoLibreCrawlerTest {

    private CrawlerMercadoLibre crawler;

    @BeforeEach
    void setUp() {
        crawler = new CrawlerMercadoLibre();
    }

    @Test
    void testpuedeManear_MercadoLibreUrl() {
        String url = "https://www.mercadolibre.com.ar/sierra-circular-7-14-185-190mm-1600w-hs7010-makita/p/MLA19813486";
        boolean result = crawler.puedeManear(url);
        assertTrue(result);
    }

    @Test
    void testpuedeManear_NonMercadoLibreUrl() {
        String url = "https://www.amazon.com/product";
        boolean result = crawler.puedeManear(url);
        assertFalse(result);
    }



}