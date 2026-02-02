//package com.zero9platform.global;
//
//import com.zero9platform.common.enums.Category;
//import com.zero9platform.common.enums.GppProgressStatus;
//import com.zero9platform.common.enums.ProductPostProgressStatus;
//import com.zero9platform.common.enums.ProductPostStatus;
//import com.zero9platform.domain.admin.entity.Influencer;
//import com.zero9platform.domain.admin.repository.InfluencerRepository;
//import com.zero9platform.domain.grouppurchase_post.entity.GroupPurchasePost;
//import com.zero9platform.domain.grouppurchase_post.repository.GroupPurchasePostRepository;
//import com.zero9platform.domain.product.entity.Product;
//import com.zero9platform.domain.product.repository.ProductRepository;
//import com.zero9platform.domain.product_post.entity.ProductPost;
//import com.zero9platform.domain.product_post.repository.ProductPostRepository;
//import com.zero9platform.domain.product_post_option.entity.ProductPostOption;
//import com.zero9platform.domain.user.entity.User;
//import com.zero9platform.domain.user.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//
//@Component
////@Profile("local")
//@RequiredArgsConstructor
//public class DevDataInitializer implements CommandLineRunner {
//
//    private final UserRepository userRepository;
//    private final GroupPurchasePostRepository gppRepository;
//    private final InfluencerRepository influencerRepository;
//    private final ProductPostRepository productPostRepository;
//    private final ProductRepository productRepository;
//    private final PasswordEncoder passwordEncoder;
//
//    @Override
//    @Transactional
//    public void run(String... args) throws Exception {
//
//        if (userRepository.count() > 0) {
//            return;
//        }
//
//        // 중복방지
////        if (userRepository.count() > 0) {
////            return;
////        }
//
//         /* =======================
//           User 더미 데이터
//        ======================= */
//        User user = userRepository.save(
//                new User(
//                        "testid1",
//                        passwordEncoder.encode("awdfth1324"),
//                        "test@email.com",
//                        "곽두철",
//                        "USER",
//                        "010-1112-1112",
//                        "곽두철닉네임"
//                )
//        );
//
//        User user1 = userRepository.save(
//                new User(
//                        "user01",
//                        passwordEncoder.encode("user1@1324"),
//                        "user01@test.com",
//                        "홍길동",
//                        "USER",
//                        "010-1111-1111",
//                        "길동이"
//                )
//        );
//
//        User user2 = userRepository.save(
//                new User(
//                        "user02",
//                        passwordEncoder.encode("user2@1324"),
//                        "user02@test.com",
//                        "김철수",
//                        "USER",
//                        "010-2222-2222",
//                        "철수형"
//                )
//        );
//
//        User user3 = userRepository.save(
//                new User(
//                        "user03",
//                        passwordEncoder.encode("user3@1324"),
//                        "user03@test.com",
//                        "이영희",
//                        "USER",
//                        "010-3333-3333",
//                        "영희짱"
//                )
//        );
//
//        User user4 = userRepository.save(
//                new User(
//                        "user04",
//                        passwordEncoder.encode("user4@1324"),
//                        "user04@test.com",
//                        "홍금보",
//                        "USER",
//                        "010-4444-4444",
//                        "금보형"
//                )
//        );
//
//        User admin = userRepository.save(
//                new User(
//                        "admin01",
//                        passwordEncoder.encode("admin1@1324"),
//                        "admin@test.com",
//                        "관리자",
//                        "ADMIN",
//                        "010-5555-5555",
//                        "관리자계정"
//                )
//        );
//
//         /* =======================
//           Influencer 더미 데이터
//        ======================= */
//        Influencer influencer = influencerRepository.save(
//                new Influencer(user, "https://zep.us/play/nL9kqr")
//        );
//
//        Influencer influencer1 = influencerRepository.save(
//                new Influencer(user1, "https://zep.us/play/nL9kqr")
//        );
//
//        Influencer influencer2 = influencerRepository.save(
//                new Influencer(user2, "https://zep.us/play/nL9kqr")
//        );
//
//        Influencer influencer3 = influencerRepository.save(
//                new Influencer(user3, "https://zep.us/play/nL9kqr")
//        );
//
//        User influencerUser = userRepository.save(
//                new User(
//                        "influencer01",
//                        passwordEncoder.encode("influencer1@1324"),
//                        "influencer@test.com",
//                        "박인플",
//                        "INFLUENCER",
//                        "010-6666-6666",
//                        "인플루언서박"
//                )
//        );
//
//        Influencer approvedInfluencer = influencerRepository.save(
//                new Influencer(influencerUser, "https://zep.us/play/nL9kqr")
//        );
//
//        // 승인 처리
//        approvedInfluencer.influencerApprove(true);
//
//
//        // 승인 처리 (테스트용)
//        influencer.influencerApprove(true);   // 승인 O
//        influencer1.influencerApprove(true);   // 승인 O
//        influencer2.influencerApprove(true);   // 승인 O
//        influencer3.influencerApprove(false);  // 승인 X
//
//         /* =======================
//           Product 더미
//        ======================= */
//        Product product1 = productRepository.save(new Product("대박곱창 밀키트", "집에서 먹는 대박곱창 밀키트", 25000L));
//        Product product2 = productRepository.save(new Product("서울 두쫀쿠 세트", "서울제과", 25000L));
//
//         /* =======================
//           ProductPost 더미
//        ======================= */
//        ProductPost productPost = productPostRepository.save(
//                new ProductPost(
//                        user,
//                        product1,
//                        "대박곱창 밀키트 공구",
//                        "집에서 먹는 대박곱창 밀키트 공동구매",
//                        100,
//                        "https://image.test/daebak-gopchang.jpg",
//                        Category.ETC.toString(),
//                        ProductPostProgressStatus.DOING.toString(),
//                        ProductPostStatus.ACTIVE.toString(),
//                        LocalDateTime.now(),
//                        LocalDateTime.now().plusDays(9)
//                )
//        );
//
//
//        /* ====================
//        ProductPostOption 더미
//        ==================== */
//        ProductPostOption option1 =
//                new ProductPostOption(productPost,"기본 구성", 1000L, 100);
//
//        ProductPostOption option2 =
//                new ProductPostOption(productPost, "곱창 2팩 구성", 5000L, 50);
//
//        productPost.addOption(option1);
//        productPost.addOption(option2);
//
//        /* =======================
//           GroupPurchasePost 더미
//        ======================= */
//        gppRepository.save(
//                new GroupPurchasePost(
//                        user,
//                        "대박곱창",
//                        "곱창 계의 전설, 오늘만 이 가격",
//                        "https://image.test/daebak-gopchang.jpg",
//                        20000L,
//                        "https://buy.test/daebak-gopchang",
//                        Category.ETC.name(),
//                        GppProgressStatus.DOING.name(),
//                        LocalDateTime.now(),
//                        LocalDateTime.now().plusDays(7)
//                )
//        );
//
//        gppRepository.save(
//                new GroupPurchasePost(
//                        user1,
//                        "서울 두쫀쿠",
//                        "두쫀 두쫀 서울 두쫀쿠 공동구매",
//                        "https://image.test/duzzoncu-seoul.jpg",
//                        15000L,
//                        "https://buy.test/seoul",
//                        Category.ETC.name(),
//                        GppProgressStatus.DOING.name(),
//                        LocalDateTime.now(),
//                        LocalDateTime.now().plusDays(7)
//                )
//        );
//
//        gppRepository.save(
//                new GroupPurchasePost(
//                        user1,
//                        "인천 두쫀쿠",
//                        "두쫀 두쫀 인천 두쫀쿠 공동구매",
//                        "https://image.test/duzzoncu-incheon.jpg",
//                        20000L,
//                        "https://buy.test/incheon",
//                        Category.ETC.name(),
//                        GppProgressStatus.DOING.name(),
//                        LocalDateTime.now(),
//                        LocalDateTime.now().plusDays(7)
//                )
//        );
//
//        gppRepository.save(
//                new GroupPurchasePost(
//                        user2,
//                        "아산 두쫀쿠",
//                        "두쫀 두쫀 아산 두쫀쿠 공동구매",
//                        "https://image.test/duzzoncu-asan.jpg",
//                        8000L,
//                        "https://buy.test/asan",
//                        Category.ETC.name(),
//                        GppProgressStatus.DOING.name(),
//                        LocalDateTime.now(),
//                        LocalDateTime.now().plusDays(5)
//                )
//        );
//
//        gppRepository.save(
//                new GroupPurchasePost(
//                        user3,
//                        "부산 두쫀쿠",
//                        "두쫀 두쫀 부산 두쫀쿠 공동구매",
//                        "https://image.test/duzzoncu-busan.jpg",
//                        10000L,
//                        "https://buy.test/busan",
//                        Category.ETC.name(),
//                        GppProgressStatus.DOING.name(),
//                        LocalDateTime.now(),
//                        LocalDateTime.now().plusDays(3)
//                )
//        );
//
//        gppRepository.save(
//                new GroupPurchasePost(
//                        user3,
//                        "응급실 떡볶이",
//                        "응급실 떡볶이 공동구매",
//                        "https://image.test/tteokbokki.jpg",
//                        9000L,
//                        "https://buy.test/tteokbokki",
//                        Category.ETC.name(),
//                        GppProgressStatus.DOING.name(),
//                        LocalDateTime.now(),
//                        LocalDateTime.now().plusDays(10)
//                )
//        );
//
//        gppRepository.save(
//                new GroupPurchasePost(
//                        influencerUser,
//                        "응급실 떡볶이",
//                        "응급실 떡볶이 공동구매",
//                        "https://image.test/tteokbokki.jpg",
//                        9000L,
//                        "https://buy.test/tteokbokki",
//                        Category.INSTANT.name(),
//                        GppProgressStatus.DOING.name(),
//                        LocalDateTime.now(),
//                        LocalDateTime.now().plusDays(10)
//                )
//        );
//    }
//}