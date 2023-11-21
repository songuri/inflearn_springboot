package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);


	}

	/**
	 * 지연 로딩 된 애들 일단 사용가능하게 하는 코드인데... 뭐지 시발..
	 * 이거 자체를 쓰는거 자체가 좋지 않음.
	 * @return
	 */
	@Bean Hibernate5Module hibernate5Module (){
		// 이렇게 해서 해당 객체 return 하면 강제로 Lazy로딩 해버린다.
//		Hibernate5Module hibernate5Module = new Hibernate5Module();
//		hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);
//		return hibernate5Module;


		return new Hibernate5Module();
		//지연 로딩인애들 전부 NULL로 온다.
		/**
		 * {
		 *         "id": 4,
		 *         "member": null,
		 *         "orderItems": null,
		 *         "delivery": null,
		 *         "orderDate": "2023-11-20T16:10:53.63197",
		 *         "status": "ORDER",
		 *         "totalPrice": 50000
		 *     },
		 *     {
		 *         "id": 11,
		 *         "member": null,
		 *         "orderItems": null,
		 *         "delivery": null,
		 *         "orderDate": "2023-11-20T16:10:53.661969",
		 *         "status": "ORDER",
		 *         "totalPrice": 100000
		 *     }
		 */
	}

}
