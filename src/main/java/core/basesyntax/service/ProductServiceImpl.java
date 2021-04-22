package core.basesyntax.service;

import core.basesyntax.dao.ProductDao;
import core.basesyntax.operations.Operations;
import core.basesyntax.strategy.OperationsStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductServiceImpl implements ProductService {
    public static final String TITLE = "fruit,quantity";
    public static final String SEPARATOR = ",";
    public static final int TYPE_INDEX = 0;
    public static final int PRODUCT_NAME_INDEX = 1;
    public static final int AMOUNT_INDEX = 2;
    private final OperationsStrategy operationStrategy;
    private final ProductDao productDao;

    public ProductServiceImpl(OperationsStrategy operationStrategy, ProductDao productDao) {
        this.operationStrategy = operationStrategy;
        this.productDao = productDao;
    }

    @Override
    public void addToStorage(List<String> dataFromFile) {
        for (String line : dataFromFile) {
            String[] data = line.split(SEPARATOR);
            Product product = new Product(data[PRODUCT_NAME_INDEX]);
            int oldAmount = productDao.get(product);

            String operationType = data[TYPE_INDEX].toUpperCase();
            if (!Operations.contains(operationType)) {
                throw new RuntimeException("There is no operation of such type " + operationType);
            }
            int newAmount = operationStrategy.get(Operations.valueOf(operationType))
                    .calculateAmount(oldAmount, Integer.parseInt(data[AMOUNT_INDEX]));

            productDao.add(new Product(data[PRODUCT_NAME_INDEX]), newAmount);
        }
    }

    @Override
    public List<String> getFromStorage() {
        List<String> data = new ArrayList<>();
        data.add(TITLE);
        Map<Product, Integer> products = productDao.getMap();
        for (Map.Entry<Product, Integer> productIntegerEntry : products.entrySet()) {
            data.add(System.lineSeparator());
            data.add(productIntegerEntry.getKey().getName()
                    + SEPARATOR + productIntegerEntry.getValue());
        }
        return data;
    }
}
