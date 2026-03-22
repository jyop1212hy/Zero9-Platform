-- 1. ProductPost (일반 판매 게시글) 더미 데이터
INSERT INTO product_posts (
    title, name, content, original_price, user_id,
    start_date, end_date, category, progress_status,
    image, created_at, updated_at
) VALUES
      ('두바이 초콜릿 쫀득 쿠키', '두바이쿠키', '요즘 대세인 두바이 초콜릿 맛을 그대로 살린 쫀득한 쿠키입니다.', 15000, 3, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 'FOOD', 'ON_SALE', 'default.jpg', NOW(), NOW()),
      ('수제 초코 쿠키 10종 세트', '수제쿠키', '원재료에 두바이산 카다이프가 포함되어 바삭합니다.', 22000, 4, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 'FOOD', 'ON_SALE', 'default.jpg', NOW(), NOW()),
      ('나이키 에어포스 1 화이트', '에어포스', '유행을 타지 않는 나이키의 스테디셀러 에어포스입니다.', 129000, 3, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 'FASHION', 'ON_SALE', 'default.jpg', NOW(), NOW()),
      ('아디다스 포럼 로우 스니커즈', '아디다스포럼', '나이키 에어포스보다 발볼이 넓어 편안한 착화감을 자랑합니다.', 95000, 4, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 'FASHION', 'ON_SALE', 'default.jpg', NOW(), NOW()),
      ('나이 들어 보이지 않는 힙한 원피스', '힙원피스', '트렌디한 디자인으로 나이대에 상관없이 입기 좋습니다.', 45000, 3, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 'FASHION', 'ON_SALE', 'default.jpg', NOW(), NOW()),
      ('나이키 기능성 바람막이', '나이키자켓', '운동할 때 입기 좋은 나이키 정품 바람막이 자켓.', 89000, 4, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 'FASHION', 'ON_SALE', 'default.jpg', NOW(), NOW()),
      ('삼성 갤럭시 버즈 3 프로', '갤럭시버즈3', '최고의 음질을 자랑하는 삼성의 최신 무선 이어폰.', 250000, 3, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 'ELECTRONICS', 'ON_SALE', 'default.jpg', NOW(), NOW()),
      ('아이폰 15 케이스 (두바이 한정판)', '아이폰케이스', '두바이 몰에서만 판매하던 희귀 아이템입니다.', 35000, 4, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 'FASHION', 'ON_SALE', 'default.jpg', NOW(), NOW());


-- 2. GroupPurchasePost (공동구매 게시글) 더미 데이터
-- 필드명(product_name, price 등)은 하륜님의 엔티티 구조에 맞춰 확인 필요!
INSERT INTO group_purchase_posts (
    product_name, content, price, user_id,
    start_date, end_date, category, gpp_progress_status,
    view_count, link_url, image, created_at, updated_at
) VALUES
      (
          '[GPP] 두바이 카다이프 면 500g',
          '공동구매로 저렴하게 두바이 초콜릿 재료 준비하세요. 정식 통관 제품입니다.',
          8500, 3, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 'FOOD', 'ON_SALE',
          0, 'https://zero9.com/gpp/dubai-noodle', 'dubai_noodle.jpg', NOW(), NOW()
      ),
      (
          '[공구] 나이키 양말 3팩 세트',
          '데일리로 신기 좋은 나이키 스포츠 양말 공구 진행합니다. 정품 보장!',
          12000, 4, NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 'FASHION', 'ON_SALE',
          0, 'https://zero9.com/gpp/nike-socks', 'nike_socks.jpg', NOW(), NOW()
      );