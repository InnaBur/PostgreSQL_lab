package com.level3_2;

import com.level3_2.dao.ProductTypeDAO;
import com.level3_2.dao.ProductsDAO;
import com.level3_2.dao.ProductsInShopsDAO;
import com.level3_2.dao.ShopDAO;
import com.level3_2.dto.ProductDto;
import com.mongodb.client.*;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.*;

import java.util.List;
import java.util.Properties;

public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws IOException, SQLException {

        logger.debug("Start program");
        Properties properties = new FileProcessing().loadProperties();
        CollectionsCreator collectionsCreator = new CollectionsCreator();
        ProductTypeDAO productTypeDAO = new ProductTypeDAO();
        ShopDAO shopDAO = new ShopDAO();
        ProductsDAO productsDAO = new ProductsDAO();
        ProductsInShopsDAO productsInShopsDAO = new ProductsInShopsDAO(properties);

        try (MongoClient mongoClient = new ConnectionCreator().createConnection()) {
            logger.debug("MongoDB was created");

            MongoDatabase database = mongoClient.getDatabase("myMongo");
            logger.debug("DB was got");

            collectionsCreator.createCollectionsReference(database);

            shopDAO.insertDataIntoCollection(database);
            productTypeDAO.insertDataIntoCollection(database);

            productsDAO.insertDataIntoCollection(database);

            List<ProductDto> productDtos = productsDAO.getProductsIntoList(database);
            List<String> shops = shopDAO.getShopIntoList(database);

            productsInShopsDAO.insertWithThreads(database, productDtos, shops);
            productsInShopsDAO.findShopByProductType(database);
        }

        logger.info("Program finished");
    }


}
