//package com.zero9platform.rankingDummy;
//
//import com.zero9platform.common.enums.OrderStatus;
//import com.zero9platform.domain.clickLog.entity.ClickLog;
//
//import com.zero9platform.domain.clickLog.response.ClickLogRepository;
//import com.zero9platform.domain.gpp_comment.entity.GppComment;
//import com.zero9platform.domain.gpp_comment.repository.GppCommentRepository;
//import com.zero9platform.domain.gpp_follow.entity.GppFollow;
//
//import com.zero9platform.domain.gpp_follow.repository.FollowRepository;
//import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
//import com.zero9platform.domain.grouppurchase_post.repository.GroupPurchasePostRepository;
//import com.zero9platform.domain.order.entity.Order;
//import com.zero9platform.domain.order.repository.OrderRepository;
//import com.zero9platform.domain.orderitem.entity.OrderItem;
//import com.zero9platform.domain.orderitem.repository.OrderItemRepository;
//import com.zero9platform.domain.product.entity.Product;
//import com.zero9platform.domain.product.repository.ProductRepository;
//import com.zero9platform.domain.product_post.entity.ProductPost;
//import com.zero9platform.domain.product_post.repository.ProductPostRepository;
//import com.zero9platform.domain.product_post_favorite.entity.ProductPostFavorite;
//import com.zero9platform.domain.product_post_favorite.repository.ProductPostFavoriteRepository;
//import com.zero9platform.domain.product_post_option.entity.ProductPostOption;
//import com.zero9platform.domain.product_post_option.repository.ProductPostOptionRepository;
//
//import com.zero9platform.domain.searchLog.SearchContext;
//import com.zero9platform.domain.searchLog.entity.SearchLog;
//import com.zero9platform.domain.searchLog.repository.SearchContextRepository;
//import com.zero9platform.domain.searchLog.repository.SearchLogRepository;
//import com.zero9platform.domain.user.entity.User;
//import com.zero9platform.domain.user.repository.UserRepository;
//import jakarta.annotation.PostConstruct;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Profile;
//import org.springframework.stereotype.Component;
//
//import java.time.LocalDateTime;
//
////@Profile("local")
//@Component
//@RequiredArgsConstructor
//public class DummyDataInitializer {
//
//    private final UserRepository userRepository;
//    private final ProductRepository productRepository;
//    private final ProductPostRepository productPostRepository;
//    private final ProductPostOptionRepository productPostOptionRepository;
//    private final ProductPostFavoriteRepository productPostFavoriteRepository;
//    private final GroupPurchasePostRepository groupPurchasePostRepository;
//    private final FollowRepository followRepository;
//    private final GppCommentRepository gppCommentRepository;
//    private final SearchLogRepository searchLogRepository;
//    private final SearchContextRepository searchContextRepository;
//    private final ClickLogRepository clickLogRepository;
//    private final OrderRepository orderRepository;
//    private final OrderItemRepository orderItemRepository;
//
//    @PostConstruct
//    void init() {
//
//        if (userRepository.count() > 0) return;
//
//        /* 유저 */
//        User user = userRepository.save(new User("user01", "pw", "user@test.com", "일반유저", "USER", "01011112222", "user1"));
//        User user1 = userRepository.save(new User("user02", "pw", "user1@test.com", "일반유저1", "USER", "01021112222", "user2"));
//        User user2 = userRepository.save(new User("user03", "pw", "user2@test.com", "일반유저2", "USER", "01031112222", "user3"));
//        User user3 = userRepository.save(new User("user04", "pw", "user3@test.com", "일반유저3", "USER", "01041112222", "user4"));
//        User user4 = userRepository.save(new User("user05", "pw", "user4@test.com", "일반유저4", "USER", "01051112222", "user5"));
//        User user5 = userRepository.save(new User("user06", "pw", "user5@test.com", "일반유저5", "USER", "01061112222", "user6"));
//        User user6 = userRepository.save(new User("user07", "pw", "user6@test.com", "일반유저6", "USER", "01071112222", "user7"));
//        User user7 = userRepository.save(new User("user08", "pw", "user7@test.com", "일반유저7", "USER", "01081112222", "user8"));
//
//        User influencer = userRepository.save(new User("infl01", "pw", "infl@test.com", "인플루언서", "INFLUENCER", "01033334444", "infl"));
//
//        /* 상품 + 게시물 */
//        Product product = productRepository.save(new Product("치킨", "테스트 상품", 15000L));
//        Product product1 = productRepository.save(new Product("치킨1", "테스트 상품", 15000L));
//        Product product2 = productRepository.save(new Product("치킨2", "테스트 상품", 15000L));
//        Product product3 = productRepository.save(new Product("치킨3", "테스트 상품", 15000L));
//        Product product4 = productRepository.save(new Product("치킨4", "테스트 상품", 15000L));
//        Product product5 = productRepository.save(new Product("치킨5", "테스트 상품", 15000L));
//        Product product6 = productRepository.save(new Product("치킨6", "테스트 상품", 15000L));
//        Product product7 = productRepository.save(new Product("치킨7", "테스트 상품", 15000L));
//
//        ProductPost post = productPostRepository.save(new ProductPost(influencer, product, "교촌치킨 공구", "치킨 공구합니다", 100, null, "FOOD", "READY", "ACTIVE", LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(3)));
//        ProductPost post1 = productPostRepository.save(new ProductPost(influencer, product1, "교촌치킨1 공구", "치킨 공구합니다", 100, null, "FOOD", "READY", "ACTIVE", LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(3)));
//        ProductPost post2 = productPostRepository.save(new ProductPost(influencer, product2, "교촌치킨2 공구", "치킨 공구합니다", 100, null, "FOOD", "READY", "ACTIVE", LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(3)));
//        ProductPost post3 = productPostRepository.save(new ProductPost(influencer, product3, "교촌치킨3 공구", "치킨 공구합니다", 100, null, "FOOD", "READY", "ACTIVE", LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(3)));
//        ProductPost post4 = productPostRepository.save(new ProductPost(influencer, product4, "교촌치킨4 공구", "치킨 공구합니다", 100, null, "FOOD", "READY", "ACTIVE", LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(3)));
//        ProductPost post5 = productPostRepository.save(new ProductPost(influencer, product5, "교촌치킨5 공구", "치킨 공구합니다", 100, null, "FOOD", "READY", "ACTIVE", LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(3)));
//        ProductPost post6 = productPostRepository.save(new ProductPost(influencer, product6, "교촌치킨6 공구", "치킨 공구합니다", 100, null, "FOOD", "READY", "ACTIVE", LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(3)));
//        ProductPost post7 = productPostRepository.save(new ProductPost(influencer, product7, "교촌치킨7 공구", "치킨 공구합니다", 100, null, "FOOD", "READY", "ACTIVE", LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(3)));
//
//        ProductPostOption option = productPostOptionRepository.save(new ProductPostOption(post, "1마리", 15000L, 1));
//        ProductPostOption option1 = productPostOptionRepository.save(new ProductPostOption(post1, "2마리", 15000L, 1));
//        ProductPostOption option2 = productPostOptionRepository.save(new ProductPostOption(post2, "3마리", 15000L, 1));
//        ProductPostOption option3 = productPostOptionRepository.save(new ProductPostOption(post3, "4마리", 15000L, 1));
//        ProductPostOption option4 = productPostOptionRepository.save(new ProductPostOption(post4, "5마리", 15000L, 1));
//        ProductPostOption option5 = productPostOptionRepository.save(new ProductPostOption(post5, "6마리", 15000L, 1));
//        ProductPostOption option6 = productPostOptionRepository.save(new ProductPostOption(post6, "7마리", 15000L, 1));
//        ProductPostOption option7 = productPostOptionRepository.save(new ProductPostOption(post7, "8마리", 15000L, 1));
//
//        productPostFavoriteRepository.save(new ProductPostFavorite(user, post));
//        productPostFavoriteRepository.save(new ProductPostFavorite(user, post1));
//        productPostFavoriteRepository.save(new ProductPostFavorite(user, post2));
//        productPostFavoriteRepository.save(new ProductPostFavorite(user, post3));
//        productPostFavoriteRepository.save(new ProductPostFavorite(user, post4));
//        productPostFavoriteRepository.save(new ProductPostFavorite(user, post5));
//        productPostFavoriteRepository.save(new ProductPostFavorite(user, post6));
//        productPostFavoriteRepository.save(new ProductPostFavorite(user, post7));
//
//        GroupPurchasePost gpp = groupPurchasePostRepository.save(new GroupPurchasePost(influencer, "교촌치킨", "공동구매", null, 15000L, "https://test.link", "FOOD", "DOING", LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(5)));
//
//        followRepository.save(new GppFollow(user, gpp));
//        gppCommentRepository.save(new GppComment(gpp, user, "댓글 테스트"));
//
//        SearchLog searchLog = searchLogRepository.save(new SearchLog("치킨"));
//
//        /* 🔥 랭킹용 대량 데이터 */
//        for (int i = 0; i < 50; i++) {
//
//            // post
//            productPostFavoriteRepository.save(new ProductPostFavorite(user, post));
//            searchContextRepository.save(new SearchContext("치킨", post.getId()));
//            clickLogRepository.save(new ClickLog(post, "치킨"));
//            searchLog.increaseCount();
//            orderRepository.save(new Order(orderItemRepository.save(new OrderItem(user, post, option, 1)),
//                            "ORDER-P0-" + i,
//                            15000L,
//                            OrderStatus.PENDING.name()
//                    )
//            );
//
//            // post1
//            if (i % 2 == 0) {
//                productPostFavoriteRepository.save(new ProductPostFavorite(user1, post1));
//                searchContextRepository.save(new SearchContext("치킨1", post1.getId()));
//                clickLogRepository.save(new ClickLog(post1, "치킨1"));
//                searchLog.increaseCount();
//                orderRepository.save(new Order(orderItemRepository.save(new OrderItem(user1, post1, option1, 1)),
//                                "ORDER-P1-" + i,
//                                15000L,
//                                OrderStatus.PENDING.name()
//                        )
//                );
//            }
//
//            // post2
//            if (i % 3 == 0) {
//                productPostFavoriteRepository.save(new ProductPostFavorite(user2, post2));
//                searchContextRepository.save(new SearchContext("치킨2", post2.getId()));
//                clickLogRepository.save(new ClickLog(post2, "치킨2"));
//                searchLog.increaseCount();
//                orderRepository.save(new Order(orderItemRepository.save(new OrderItem(user2, post2, option2, 1)),
//                                15000L + "",
//                                15000L,
//                                OrderStatus.PENDING.name()
//                        )
//                );
//            }
//
//            // post3
//            if (i % 4 == 0) {
//                productPostFavoriteRepository.save(new ProductPostFavorite(user3, post3));
//                searchContextRepository.save(new SearchContext("치킨3", post3.getId()));
//                clickLogRepository.save(new ClickLog(post3, "치킨3"));
//                searchLog.increaseCount();
//                orderRepository.save(new Order(orderItemRepository.save(new OrderItem(user3, post3, option3, 1)),
//                                "ORDER-P3-" + i,
//                                15000L,
//                                OrderStatus.PENDING.name()
//                        )
//                );
//            }
//
//            // post4
//            if (i % 5 == 0) {
//                productPostFavoriteRepository.save(new ProductPostFavorite(user4, post4));
//                searchContextRepository.save(new SearchContext("치킨4", post4.getId()));
//                clickLogRepository.save(new ClickLog(post4, "치킨4"));
//                searchLog.increaseCount();
//                orderRepository.save(new Order(orderItemRepository.save(new OrderItem(user4, post4, option4, 1)),
//                                "ORDER-P4-" + i,
//                                15000L,
//                                OrderStatus.PENDING.name()
//                        )
//                );
//            }
//
//            // post5
//            if (i % 6 == 0) {
//                productPostFavoriteRepository.save(new ProductPostFavorite(user5, post5));
//                searchContextRepository.save(new SearchContext("치킨5", post5.getId()));
//                clickLogRepository.save(new ClickLog(post5, "치킨5"));
//                searchLog.increaseCount();
//                orderRepository.save(new Order(orderItemRepository.save(new OrderItem(user5, post5, option5, 1)),
//                                "ORDER-P5-" + i,
//                                15000L,
//                                OrderStatus.PENDING.name()
//                        )
//                );
//            }
//
//            // post6
//            if (i % 7 == 0) {
//                productPostFavoriteRepository.save(new ProductPostFavorite(user6, post6));
//                searchContextRepository.save(new SearchContext("치킨6", post6.getId()));
//                clickLogRepository.save(new ClickLog(post6, "치킨6"));
//                searchLog.increaseCount();
//                orderRepository.save(new Order(orderItemRepository.save(new OrderItem(user6, post6, option6, 1)),
//                                "ORDER-P6-" + i,
//                                15000L,
//                                OrderStatus.PENDING.name()
//                        )
//                );
//            }
//
//            // post7
//            if (i % 8 == 0) {
//                productPostFavoriteRepository.save(new ProductPostFavorite(user7, post7));
//                searchContextRepository.save(new SearchContext("치킨7", post7.getId()));
//                clickLogRepository.save(new ClickLog(post7, "치킨7"));
//                searchLog.increaseCount();
//                orderRepository.save(new Order(orderItemRepository.save(new OrderItem(user7, post7, option7, 1)),
//                                "ORDER-P7-" + i,
//                                15000L,
//                                OrderStatus.PENDING.name()
//                        )
//                );
//            }
//
//            // 공구 공통 (팔로우/댓글은 누적)
//            followRepository.save(new GppFollow(user, gpp));
//            gppCommentRepository.save(new GppComment(gpp, user, "댓글-" + i));
//        }
//
//
//
//
////        searchLog.increaseCount();
////
////        searchContextRepository.save(
////                new SearchContext("치킨", post.getId())
////        );
////
////        clickLogRepository.save(
////                new ClickLog(post, "치킨")
////        );
////
////        // 1️⃣ OrderItem 먼저 생성
////        OrderItem orderItem = orderItemRepository.save(
////                new OrderItem(
////                        user,
////                        post,
////                        option,
////                        1
////                )
////        );
////
////        // 2️⃣ Order 생성 (OrderItem을 생성자에 주입)
////        Order order = orderRepository.save(
////                new Order(
////                        orderItem,
////                        "ORDER-TEST-001",
////                        15000L,
////                        OrderStatus.PENDING.name()
////                )
////        );
//    }
//}