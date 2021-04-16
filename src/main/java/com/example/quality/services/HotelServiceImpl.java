package com.example.quality.services;


import com.example.quality.dtos.HotelDTO;
import com.example.quality.exceptions.InvalidDateException;
import com.example.quality.exceptions.InvalidDestinationException;
import com.example.quality.repositories.HotelRepository;
import com.example.quality.utils.DateUtils;
import com.example.quality.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.function.DoublePredicate;
import java.util.stream.Collectors;


@Service
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;

//    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public HotelServiceImpl(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    @Override
    public List<HotelDTO> getAvailableHotels() {
        return hotelRepository.getHotelList().stream()
                .filter(hotel -> hotel.getReserved().equals(false))
                .collect(Collectors.toList());
    }

    @Override
    public List<HotelDTO> getFilteredAvailableHotels(String dateFrom, String dateTo, String destination) throws Exception {

        List<HotelDTO> allAvailableHotels = getAvailableHotels();

        LocalDate fromDate = DateUtils.parseDate(dateFrom);
        LocalDate toDate = DateUtils.parseDate(dateTo);

        DateUtils.validateDateRange(fromDate, toDate);

        String normalizedDestination = StringUtils.normalizeString(destination);

        validateDestination(normalizedDestination);

        return allAvailableHotels.stream()
                .filter(hotel -> fromDate.isEqual(hotel.getAvailableFrom())
                        || fromDate.isAfter(hotel.getAvailableFrom()))
                .filter(hotel -> toDate.isEqual(hotel.getAvailableTo())
                        || toDate.isBefore(hotel.getAvailableTo()))
                .filter(hotel -> StringUtils.normalizeString(hotel.getCity())
                        .equals(normalizedDestination))
                .collect(Collectors.toList());
    }

    private void validateDestination(String destination) throws InvalidDestinationException {

        Optional<HotelDTO> hotelByLocation = hotelRepository.getHotelList().stream()
                .filter(hotel -> StringUtils.normalizeString(hotel.getCity()).equals(destination))
                .findAny();

        if (hotelByLocation.isEmpty()) {
            throw new InvalidDestinationException("Destination not found");
        }
    }

//    private final ArticleRepository articleRepository;
//    private List<ShoppingCartDTO> shoppingCartsList = new ArrayList<>();
//
//    private final AtomicLong ticketIdCounter = new AtomicLong(1);
//    private final AtomicLong shoppingCartIdCounter = new AtomicLong(1);
//
//    @Autowired
//    public ServiceImpl(ArticleRepository articleRepository) {
//        this.articleRepository = articleRepository;
//    }
//
//    @Override
//    public List<ArticleDTO> getUnfilteredArticles() {
//        return articleRepository.getArticleList();
//    }
//
//    @Override
//    public List<ArticleDTO> getArticles(Map<String, String> allFilters) throws Exception {
//
//        List<ArticleDTO> catalog = getUnfilteredArticles();
//
//        if (allFilters.size() < 1) {
//
//            return catalog;
//
//        } else if (allFilters.size() <= 2
//                || (allFilters.size() == 3 && allFilters.get("order") != null)) {
//
//            // Validate value of non-string filters
//            validateFilters(allFilters);
//
//            return filterArticles(catalog, allFilters);
//
//        } else {
//            throw new InvalidFilterException("A maximum of 2 filters can be applied at the same time");
//        }
//    }
//
//    @Override
//    public PurchaseResponseDTO PurchaseArticles(Long cartId, PurchaseRequestDTO articles) throws Exception {
//
//        List<PurchaseArticleDTO> articleList = articles.getArticles();
//
//        long total = 0L;
//
//        for (PurchaseArticleDTO article : articleList) {
//
//            if (article.getQuantity() < 1) {
//                throw new InvalidArticleException("Quantity for article with product id " + article.getProductId() + " is invalid");
//            }
//
//            ArticleDTO itemInStock = articleRepository.getArticleById(article.getProductId());
//
//            if (itemInStock == null) {
//                throw new InvalidArticleException("Article with product id " + article.getProductId() + " is invalid");
//            }
//
//            // Validate name and brand for article ID
//            validateArticle(article, itemInStock);
//
//            if (article.getQuantity() > itemInStock.getQuantity()) {
//                throw new InsufficientStockException("Stock available for article with product id " + itemInStock.getProductId() + " is " + itemInStock.getQuantity());
//            }
//
//            total += (article.getQuantity() * (long) itemInStock.getPrice());
//        }
//
//        if (cartId == null) {
//            ShoppingCartDTO shoppingCart = new ShoppingCartDTO(shoppingCartIdCounter.getAndIncrement(), total, articleList);
//            shoppingCartsList.add(shoppingCart);
//            cartId = shoppingCart.getCartId();
//
//        } else {
//            ShoppingCartDTO shoppingCart = getCartById(cartId);
//
//            if (shoppingCart == null)
//                throw new InvalidShopingCartException("Shopping cart number " + cartId + " not found");
//
//            shoppingCart.addItems(articleList, total);
//            total = shoppingCart.getTotal();
//        }
//
//        // Update catalog stock
//        for (PurchaseArticleDTO article : articleList) {
//            articleRepository.subtractStock(article.getProductId(), article.getQuantity());
//        }
//
//        articleRepository.updateDatabase();
//
//        TicketDTO ticket = new TicketDTO(ticketIdCounter.getAndIncrement(), articleList, total);
//        StatusDTO status = new StatusDTO(200, "La solicitud de compra se completó con éxito");
//
//        return new PurchaseResponseDTO(cartId, ticket, status);
//    }
//
//    private void validateArticle(PurchaseArticleDTO reqArticle, ArticleDTO catalogArticle) throws InvalidArticleException {
//
//        if (!reqArticle.getName().equalsIgnoreCase(catalogArticle.getName())
//                || !reqArticle.getBrand().equalsIgnoreCase(catalogArticle.getBrand())) {
//            throw new InvalidArticleException("Invalid article data. Product id: " + reqArticle.getProductId());
//        }
//    }
//
//    private void validateFilters(Map<String, String> filterList) throws Exception {
//
//        for (Map.Entry<String, String> filter : filterList.entrySet()) {
//
//            String key = filter.getKey();
//
//            if (key.equals("price") || key.equals("prestige") || key.equals("order")) {
//                try {
//                    Integer.parseInt(filter.getValue());
//
//                } catch (NumberFormatException e) {
//                    throw new InvalidFilterException(key + " filter accepts only numeric values");
//                }
//            }
//
//            if (key.equals("freeShipping")
//                    && !filter.getValue().equals("true")
//                    && !filter.getValue().equals("false")) {
//
//                throw new InvalidFilterException("FreeShipping must be boolean");
//            }
//        }
//    }
//
//    private List<ArticleDTO> filterArticles(List<ArticleDTO> catalog, Map<String, String> filters) throws Exception {
//
//        for (Map.Entry<String, String> filter : filters.entrySet()) {
//
//            String key = filter.getKey();
//            String value = filter.getValue();
//
//            switch (key) {
//                case "name":
//                    catalog = filterByName(catalog, value);
//                    break;
//                case "category":
//                    catalog = filterByCategory(catalog, value);
//                    break;
//                case "brand":
//                    catalog = filterByBrand(catalog, value);
//                    break;
//                case "price":
//                    catalog = filterByPrice(catalog, Integer.parseInt(value));
//                    break;
//                case "freeShipping":
//                    catalog = filterByShipping(catalog, value.equals("true"));
//                    break;
//                case "prestige":
//                    catalog = filterByPrestige(catalog, Integer.parseInt(value));
//                    break;
//                case "order":
//                    catalog = sortArticles(catalog, Integer.parseInt(value));
//                    break;
//                default:
//                    throw new InvalidFilterException("Filter '" + key + "' is invalid");
//            }
//        }
//        return catalog;
//    }
//
//    private List<ArticleDTO> filterByName(List<ArticleDTO> catalog, String filter) {
//
//        return catalog.stream().filter(item -> item.getName()
//                .equalsIgnoreCase(filter))
//                .collect(Collectors.toList());
//    }
//
//    private List<ArticleDTO> filterByCategory(List<ArticleDTO> catalog, String filter) {
//
//        return catalog.stream().filter(item -> item.getCategory()
//                .equalsIgnoreCase(filter))
//                .collect(Collectors.toList());
//    }
//
//    private List<ArticleDTO> filterByBrand(List<ArticleDTO> catalog, String filter) {
//
//        return catalog.stream().filter(item -> item.getBrand()
//                .equalsIgnoreCase(filter))
//                .collect(Collectors.toList());
//    }
//
//    private List<ArticleDTO> filterByPrice(List<ArticleDTO> catalog, Integer filter) {
//
//        return catalog.stream().filter(item -> item.getPrice()
//                .equals(filter))
//                .collect(Collectors.toList());
//    }
//
//    private List<ArticleDTO> filterByShipping(List<ArticleDTO> catalog, Boolean filter) {
//
//        return catalog.stream().filter(item -> item.getFreeShipping()
//                .equals(filter))
//                .collect(Collectors.toList());
//    }
//
//    private List<ArticleDTO> filterByPrestige(List<ArticleDTO> catalog, Integer filter) {
//
//        return catalog.stream().filter(item -> item.getPrestige()
//                .equals(filter))
//                .collect(Collectors.toList());
//    }
//
//    private List<ArticleDTO> sortArticles(List<ArticleDTO> catalog, Integer order) throws InvalidFilterException {
//
//        switch (order) {
//            case 0:
//                return catalog.stream().sorted(Comparator.comparing(ArticleDTO::getName)).collect(Collectors.toList());
//            case 1:
//                return catalog.stream().sorted((a, b) -> b.getName().compareToIgnoreCase(a.getName())).collect(Collectors.toList());
//            case 2:
//                return catalog.stream().sorted((a, b) -> b.getPrice() - a.getPrice()).collect(Collectors.toList());
//            case 3:
//                return catalog.stream().sorted(Comparator.comparingInt(ArticleDTO::getPrice)).collect(Collectors.toList());
//            default:
//                throw new InvalidFilterException("Order filter accepted values: 0-3");
//        }
//    }
//
//    public ShoppingCartDTO getCartById(Long id) {
//        return shoppingCartsList.stream()
//                .filter(a -> a.getCartId().equals(id))
//                .findFirst()
//                .orElse(null);
//    }
}
