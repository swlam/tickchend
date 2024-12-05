package com.sjm.test.yahdata.analy.conts.ta;

public enum CandleEventTagEnum {
	EVENT_NA("N/A",0),
	EVNT_PRICE_ENGULFING_21_SMALL_VOL ("低量破腳穿頭陽過陰21",	2),//***//
	EVNT_PRICE_ENGULFING_PAST_2DAYS_TOP ("穿頭陽過2天頂",	2),//***//
	EVNT_PRICE_ENGULFING_NORMAL_VOL ("增量破腳穿頭陽過陰",	2),
	EVNT_PRICE_ENGULFING_21_NORMAL_VOL ("增量破腳穿頭陽過陰21",2),
	EVNT_PRICE_OVER_DOUBLE_VOL ("過昨燭身高收倍量陽",4),
	EVNT_PRICE_OVER_NORMAL_VOL ("過昨燭身高收",3),
	EVNT_FALL_BODY_VOLATILE_VOL ("燭身跌超過-3%",	-3),
	EVNT_FALL_BODY_VOLATILE_LV2_VOL ("燭身跌超過-5%",	-3),
	EVNT_RAISE_BODY_VOLATILE_VOL ("燭身漲超過3%",	3),
	EVNT_MONTHLY_LOW_VOL_THEN_RAISE ("月內低量後增量陽過陰21",	1),
	
	EVNT_QUARTERLY_H_CLOSE ("季新高收", 2),
	EVNT_QUARTERLY_L_CLOSE ("季新低收", -2),
	EVNT_MONTHLY_H ("月新高",	2),
	EVNT_MONTHLY_H_CLOSE ("月新高收", 2),
	EVNT_MONTHLY_L_CLOSE ("月新低收", -2),
	
	
	EVNT_WEEKLY_H_FIRSTTIME ("週新高(初)",	1),
	EVNT_WEEKLY_H_CLOSE_FIRSTTIME ("週新高收(初)", 1),
	EVNT_WEEKLY_L_FIRSTTIME ("週新低(初)",	1),
	EVNT_WEEKLY_L_CLOSE_FIRSTTIME("週新低收(初)", -1),
	
	
	EVNT_FALL_BODY_LOWER_LOW ("陰燭並最低低於昨天最低",	-3),
	
	EVNT_GAP_UP_2_PCT_PLUS ("裂口上升2%+",	4),
	EVNT_GAP_UP_3_PCT_PLUS ("裂口上升3%+",	4),
	EVNT_GAP_UP_4_PCT_PLUS ("裂口上升4%+",	4),
	EVNT_GAP_UP_5_PCT_PLUS ("裂口上升5%+",	4),
	EVNT_GAP_UP_6_PCT_PLUS ("裂口上升6%+",	4),
	EVNT_GAP_UP_8_PCT_PLUS ("裂口上升8%+",	4),
	EVNT_GAP_UP_10_PCT_PLUS ("裂口上升10%+",	4),
	
	EVNT_GAP_UP  ("裂口上升",	3),
	EVNT_GAP_DOWN  ("裂口下跌",	-3),
	EVNT_GAP_DOWN_LARGE  ("大裂口下跌",	-4),
	
	EVNT_GAP_DOWN_AND_STAND_UP("裂口下跌數日內企上",	-4),
	EVNT_BIG_DROP_AND_STAND_UP("大陰下跌數日內企上",	-4),
	
	EVNT_OPEN_HIGH_CLOSE_HIGH_VOL_INCREMENTAL  ("增量高開高收",	2),//Incremental *** volume size 1.05X - 2.0X
	EVNT_OPEN_HIGH_CLOSE_HIGH  ("高開高收",	2),//Incremental *** volume size 1.05X - 2.0X
	EVNT_BEARISH_ENGULFING ("穿頭破腳",	-2),	//BearishEngulfing	
	EVNT_BULLISH_ENGULFING ("破腳穿頭",	2), //BullishEngulfing
	EVNT_GAP_UP_BEARISH_SHOOTING ("裂口上升射擊", 2),
	EVNT_OPEN_HIGH_CLOSE_LOW_ABV_BODY_TOP  ("燭身頂高開低收",	2),
	EVNT_OPEN_HIGH_CLOSE_LOW_VOL_INCREMENTAL_ABV_BODY_TOP  ("燭身頂增量高開低收",	2),//Incremental *** volume size 1.05X - 2.0X
	EVNT_OPEN_LOW_CLOSE_HIGH_IN_RED_BODY  ("陽身低開高收",	2),
	
//	EVNT_FAKE_BLACK_TRUE_WHITE_BODY ("假陰真陽", 1),
//	EVNT_FAKE_RED_TRUE_BLACK ("假陽真陰", -1),
	EVNT_CROSS_UP_5D_10D ("5X10上",	1),
	EVNT_CROSS_UP_3D_18D ("3X18上",	1),
	EVNT_CROSS_UP_2D_19D ("2X19上",	1),
	EVNT_CROSS_UP_10D_20D ("10X20上",	1),
	EVNT_CROSS_UP_10D_50D ("10X50上", 1),
	EVNT_CROSS_UP_50D_200D ("50X200上",	2),
	EVNT_CROSS_UP_50D_250D ("50X250上",	2),
	EVNT_CROSS_UP_150D_200D ("150X200上",	2),
	EVNT_CROSS_DOWN_5D_10D ("5X10下",	-1),
	EVNT_CROSS_DOWN_2D_19D ("2X19下",	-1),
	EVNT_CROSS_DOWN_3D_18D ("3X18下",	-1),
	EVNT_CROSS_DOWN_10D_50D ("10X50下", -1),
	EVNT_CROSS_DOWN_50D_200D ("50X200下",	-1),
	EVNT_CROSS_DOWN_50D_250D ("50X250下",	-1),	
	EVNT_CROSS_UP_3MA ("一陽穿3線",	2),
	
	EVNT_UP_BREAK_50D ("升穿50D",	2),
	EVNT_UP_BREAK_50_3DAYS ("升穿50D3天",	2),
	
	EVNT_FULL_LONG_ARRANGE ("全多頭排列(5>10>20>50>200)出現",	2	),
	EVNT_SMALL_LONG_ARRANGE ("小多頭排列(5>10>20)出現",	2	),
	EVNT_MID_TERM_LONG_ARRANGE ("中多頭排列(10>20>50)出現",	2	),
	EVNT_SHORT_ARRANGE ("空頭排列出現",	2	),
	
	EVNT_INCREASE_DOUBLE_VOL_BLACK ("價跌量增倍",0),
	EVNT_INCREASE_DOUBLE_VOL_RED ("價升量增倍",0),
	EVNT_INCREASE_OVER_2X_VOL_BLACK ("價跌量增超2倍",0), //incremental
	EVNT_INCREASE_OVER_2X_VOL_RED ("價升量增超2倍",0), //incremental 
	EVNT_DECREASE_HALF_VOL_BLACK ("價跌縮半量",0), //decrease
	EVNT_DECREASE_HALF_VOL_RED ("價升縮半量",0), //decrease
	EVNT_DECREASE_MOST_VOL_BLACK ("價跌縮超一半量",0), //decrease
	EVNT_DECREASE_MOST_VOL_RED ("價升縮超一半量",0), //decrease
	
	EVNT_PRICE_VOL_PUSH_UP ("推升量(連3紅)",0),//連3紅
	
	EVNT_CLOSE_ABV_UPPER_SHADOW ("上影線上收",2), //decrease
	EVNT_CLOSE_BELOW_LOWER_SHADOW ("下影線下收",-2), //decrease
	
	EVNT_GAP_DOWN_HAMMER ("裂口下跌鎚頭",	2), //BullishEngulfing
	EVNT_TWO_WHITE_WITH_BLACK ("兩陽夾一陰",	2), //BullishEngulfing

	
	EVNT_GBUB ("GBUB", 2),
	EVNT_GAPUP_AND_GO ("GAPUPAndGO", 2),
	
	EVNT_VOLATILE_CLOSE_UP_3_PCT_PLUS ("波幅LH3%+",0), //incremental
	EVNT_VOLATILE_CLOSE_UP_4_PCT_PLUS ("波幅LH4%+",0), //incremental
	EVNT_VOLATILE_CLOSE_UP_6_PCT_PLUS ("波幅LH6%+",0), //incremental
	EVNT_VOLATILE_CLOSE_UP_8_PCT_PLUS ("波幅LH8%+",0), //incremental
	EVNT_VOLATILE_CLOSE_UP_10_PLUS_PCT ("波幅LH10%+",0), //incremental
	
	EVNT_VOLATILE_CLOSE_DOWN_3_PCT_PLUS ("波幅HL-3%~",0), //incremental
	EVNT_VOLATILE_CLOSE_DOWN_4_PCT_PLUS ("波幅HL-4%~",0), //incremental
	EVNT_VOLATILE_CLOSE_DOWN_6_PCT_PLUS ("波幅HL-6%~",0), //incremental
	EVNT_VOLATILE_CLOSE_DOWN_8_PCT_PLUS ("波幅HL-8%~",0), //incremental
	EVNT_VOLATILE_CLOSE_DOWN_10_PLUS_PCT ("波幅HL-10%~",0), //incremental
	
	
	EVNT_MA_CROSS_DOWN_THEN_UP_BREAK ("短下叉後站回", 2),
	
	//day before thanksgiving
	
	EVNT_DAY_BEFORE_THANKSGIVING("感恩節前一天", 0),
	
	OCCUR_EVNT_WHITE_BODY_WITH_LOWER_SHADOW_IN_YESTERDAY_BODY ("下影線在昨日燭身內*",0), 
	OCCUR_EVNT_GAPUP_WHITE_BODY_WITH_LOWER_SHADOW ("GAPUP出下影線*",0), 
	
	COMBINE_GAP_UP_BEARISH_SHOOTING_WEEKLY_H_CLOSE ("裂口上升射擊+週新高收",	0	),
	COMBINE_3D_ABOVE_18D__CLOSE_ABOVE_50D__10D_ABOVE_50D ("收市>50D+MA3XMA18UP+10D>50D",	0	),
	COMBINE_2X19_CROSSUP__CLOSE_ABOVE_50D ("MA2MA19上交叉+高於MA50",	0	),
	COMBINE_10X20_CROSSUP__CLOSE_ABOVE_50D ("MA10MA20上交叉+高於MA50",	0	),
	COMBINE_BELOW_10D__GAP_DOWN ("收市<10D+裂口下跌",	0	),
	COMBINE_2X19_CROSSDOWN__CLOSE_BELOW_50D ("MA2MA19下交叉+低於MA50",	0	),

	EVNT_BULLISH_ABANDONED_BABY("上揚棄嬰",0),
	EVNT_BEARISH_ABANDONED_BABY("看跌棄嬰",0),
	
	EVNT_BEARISH_ENGULFING_2("向淡鯨吞",0),
	EVNT_BULLISH_ENGULFING_2("向好鯨吞",0),
	
	EVNT_MORNING_STAR("MorningStar",0),	
	EVNT_EVENING_STAR("EveningStar",0),
	EVNT_UP_BREAKOUT_FAILURE("向上突破失敗(3D)",0),	
	EVNT_DOWNWARD_BREAKOUT_FAILURE("向下突破失敗(3D)",0),
	EVNT_GO_STRONG("轉強(週新低後)",0),
	EVNT_GO_WEAK("轉弱(週新高後)",0),
	
	EVNT_STATS_LOWER_SHADOW("出下影線",0),
	
	EVNT_MA_BULISSH_CROSSING("MA牛信號",0),
	
//	EVNT_LONG_BOOM ("L-BOOM",	2),
	// status
	
	EVNT_WEEKLY_H_CLOSE ("週新高收", 1),//status only
	EVNT_WEEKLY_L_CLOSE ("週新低收", -1),//status only	
	EVNT_WEEKLY_H ("週新高",	1),//status only
	EVNT_WEEKLY_L ("週新低",	1),//status only
	
	;
	
	
	private String name;
	private int strength;// weak -5 To 5 strong

	private CandleEventTagEnum(String name, int strength) {
		this.name = name;
		this.strength = strength;

	}

//	public static String getName(int index) {
//		for (CandleTagEnum c : CandleTagEnum.values()) {
//			if (c.getStrength() == index) {
//				return c.name;
//			}
//		}
//		return null;
//	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Deprecated
	public int getStrength() {
		return strength;
	}

	@Deprecated
	public void setStrength(int strength) {
		this.strength = strength;
	}
	
	public String getDescription() {
		return this.getName() ;
	}
	public String toString() {
		return this.getName();
	}
}