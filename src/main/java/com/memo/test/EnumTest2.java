package com.memo.test;

import java.util.function.Function;

public class EnumTest2 {

//	public enum CalcType {
//		// 열거형 정의
//		CALC_A(value -> value),
//		CALC_B(value -> value * 10),
//		CALC_C(value -> value * 3),
//		CALC_ETC(value -> 0);
//		
//		// 필드 정의 -> enum에 정의 된 function
//		private Function<Integer, Integer> expression;
//		
//		// 생성자
//		CalcType(Function<Integer, Integer> expression) {
//			this.expression = expression;
//		}
//		
//		// 계산 적용 메소드
//		public int calculate(int value) {
//			return expression.apply(value);
//		}
//	}
//	
//	@Test
//	void 계산테스트() {
//		// given
//		CalcType ctype = CalcType.CALC_C;
//		
//		// when
//		int result = ctype.calculate(500);
//		
//		// then
//		assertEquals(1500, result);
//	}
}
