package org.samcrow.data.provider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.samcrow.colonynavigator.data.Colony;
import org.samcrow.colonynavigator.data.ColonyList;

/**
 * Provides colonies from a hard-coded list.
 * 
 * This implementation does not support the
 * {@link ColonyProvider#updateColonies()} and
 * {@link ColonyProvider#updateColony(Colony)} methods. Calling either of these
 * will immediately throw an {@link UnsupportedOperationException}.
 * 
 * This class is a singleton. Use it by accessing the {@link #instance instance field}.
 * 
 * @author Sam Crow
 */
public class HardCodedColonyProvider implements ColonyProvider {

	/**
	 * The set of colonies
	 */
	private static final ColonyList colonies = new ColonyList();

	static {
		final String colonyString = "{\"colonies\":[{\"active\":true,\"id\":78,\"visited\":false,\"x\":371.0,\"y\":629.0},{\"active\":true,\"id\":98,\"visited\":false,\"x\":471.0,\"y\":330.0},{\"active\":true,\"id\":99,\"visited\":false,\"x\":361.0,\"y\":298.0},{\"active\":true,\"id\":142,\"visited\":false,\"x\":468.0,\"y\":693.0},{\"active\":true,\"id\":154,\"visited\":false,\"x\":639.0,\"y\":664.0},{\"active\":true,\"id\":158,\"visited\":false,\"x\":925.0,\"y\":300.0},{\"active\":true,\"id\":181,\"visited\":false,\"x\":388.0,\"y\":724.0},{\"active\":true,\"id\":190,\"visited\":false,\"x\":1250.0,\"y\":611.0},{\"active\":true,\"id\":197,\"visited\":false,\"x\":140.0,\"y\":72.0},{\"active\":true,\"id\":199,\"visited\":false,\"x\":1034.0,\"y\":711.0},{\"active\":true,\"id\":253,\"visited\":false,\"x\":184.0,\"y\":678.0},{\"active\":true,\"id\":259,\"visited\":false,\"x\":65.0,\"y\":268.0},{\"active\":true,\"id\":271,\"visited\":false,\"x\":835.0,\"y\":487.0},{\"active\":true,\"id\":273,\"visited\":false,\"x\":365.0,\"y\":483.0},{\"active\":true,\"id\":282,\"visited\":false,\"x\":927.0,\"y\":760.0},{\"active\":true,\"id\":283,\"visited\":false,\"x\":870.0,\"y\":529.0},{\"active\":true,\"id\":286,\"visited\":false,\"x\":674.0,\"y\":554.0},{\"active\":true,\"id\":300,\"visited\":false,\"x\":183.0,\"y\":408.0},{\"active\":true,\"id\":303,\"visited\":false,\"x\":390.0,\"y\":670.0},{\"active\":true,\"id\":330,\"visited\":false,\"x\":-53.0,\"y\":601.0},{\"active\":true,\"id\":342,\"visited\":false,\"x\":129.0,\"y\":522.0},{\"active\":true,\"id\":347,\"visited\":false,\"x\":432.0,\"y\":457.0},{\"active\":true,\"id\":367,\"visited\":false,\"x\":82.0,\"y\":512.0},{\"active\":true,\"id\":393,\"visited\":false,\"x\":395.0,\"y\":550.0},{\"active\":true,\"id\":415,\"visited\":false,\"x\":38.0,\"y\":114.0},{\"active\":true,\"id\":428,\"visited\":false,\"x\":655.0,\"y\":769.0},{\"active\":true,\"id\":429,\"visited\":false,\"x\":726.0,\"y\":748.0},{\"active\":true,\"id\":442,\"visited\":false,\"x\":1101.0,\"y\":782.0},{\"active\":true,\"id\":452,\"visited\":false,\"x\":1013.0,\"y\":601.0},{\"active\":true,\"id\":462,\"visited\":false,\"x\":333.0,\"y\":779.0},{\"active\":true,\"id\":486,\"visited\":false,\"x\":764.0,\"y\":833.0},{\"active\":true,\"id\":495,\"visited\":false,\"x\":280.0,\"y\":200.0},{\"active\":true,\"id\":501,\"visited\":false,\"x\":542.0,\"y\":675.0},{\"active\":true,\"id\":509,\"visited\":false,\"x\":976.0,\"y\":774.0},{\"active\":true,\"id\":519,\"visited\":false,\"x\":326.0,\"y\":719.0},{\"active\":true,\"id\":520,\"visited\":false,\"x\":-9.0,\"y\":176.0},{\"active\":true,\"id\":537,\"visited\":false,\"x\":645.0,\"y\":225.0},{\"active\":true,\"id\":539,\"visited\":false,\"x\":61.0,\"y\":229.0},{\"active\":true,\"id\":542,\"visited\":false,\"x\":829.0,\"y\":779.0},{\"active\":true,\"id\":547,\"visited\":false,\"x\":762.0,\"y\":674.0},{\"active\":true,\"id\":549,\"visited\":false,\"x\":389.0,\"y\":344.0},{\"active\":true,\"id\":550,\"visited\":false,\"x\":1131.0,\"y\":730.0},{\"active\":true,\"id\":553,\"visited\":false,\"x\":163.0,\"y\":233.0},{\"active\":true,\"id\":562,\"visited\":false,\"x\":106.0,\"y\":200.0},{\"active\":true,\"id\":567,\"visited\":false,\"x\":110.0,\"y\":110.0},{\"active\":true,\"id\":572,\"visited\":false,\"x\":815.0,\"y\":464.0},{\"active\":true,\"id\":573,\"visited\":false,\"x\":833.0,\"y\":613.0},{\"active\":true,\"id\":576,\"visited\":false,\"x\":237.0,\"y\":123.0},{\"active\":true,\"id\":577,\"visited\":false,\"x\":352.0,\"y\":573.0},{\"active\":true,\"id\":579,\"visited\":false,\"x\":1101.0,\"y\":678.0},{\"active\":true,\"id\":582,\"visited\":false,\"x\":839.0,\"y\":670.0},{\"active\":true,\"id\":583,\"visited\":false,\"x\":487.0,\"y\":781.0},{\"active\":true,\"id\":587,\"visited\":false,\"x\":275.0,\"y\":279.0},{\"active\":true,\"id\":588,\"visited\":false,\"x\":894.0,\"y\":694.0},{\"active\":true,\"id\":590,\"visited\":false,\"x\":380.0,\"y\":226.0},{\"active\":true,\"id\":592,\"visited\":false,\"x\":247.0,\"y\":507.0},{\"active\":true,\"id\":599,\"visited\":false,\"x\":25.0,\"y\":270.0},{\"active\":true,\"id\":600,\"visited\":false,\"x\":445.0,\"y\":189.0},{\"active\":true,\"id\":607,\"visited\":false,\"x\":1100.0,\"y\":288.0},{\"active\":true,\"id\":612,\"visited\":false,\"x\":1280.0,\"y\":513.0},{\"active\":true,\"id\":613,\"visited\":false,\"x\":1028.0,\"y\":385.0},{\"active\":true,\"id\":615,\"visited\":false,\"x\":1051.0,\"y\":625.0},{\"active\":true,\"id\":621,\"visited\":false,\"x\":254.0,\"y\":184.0},{\"active\":true,\"id\":627,\"visited\":false,\"x\":885.0,\"y\":821.0},{\"active\":false,\"id\":628,\"visited\":false,\"x\":324.0,\"y\":246.0},{\"active\":true,\"id\":629,\"visited\":false,\"x\":1139.0,\"y\":791.0},{\"active\":true,\"id\":631,\"visited\":false,\"x\":598.0,\"y\":201.0},{\"active\":true,\"id\":635,\"visited\":false,\"x\":-68.0,\"y\":535.0},{\"active\":true,\"id\":646,\"visited\":false,\"x\":128.0,\"y\":271.0},{\"active\":true,\"id\":649,\"visited\":false,\"x\":1216.0,\"y\":632.0},{\"active\":true,\"id\":650,\"visited\":false,\"x\":1225.0,\"y\":728.0},{\"active\":true,\"id\":651,\"visited\":false,\"x\":328.0,\"y\":216.0},{\"active\":true,\"id\":664,\"visited\":false,\"x\":972.0,\"y\":608.0},{\"active\":true,\"id\":665,\"visited\":false,\"x\":457.0,\"y\":632.0},{\"active\":true,\"id\":666,\"visited\":false,\"x\":127.0,\"y\":585.0},{\"active\":true,\"id\":671,\"visited\":false,\"x\":699.0,\"y\":666.0},{\"active\":true,\"id\":672,\"visited\":false,\"x\":943.0,\"y\":688.0},{\"active\":true,\"id\":683,\"visited\":false,\"x\":317.0,\"y\":184.0},{\"active\":true,\"id\":685,\"visited\":false,\"x\":612.0,\"y\":574.0},{\"active\":true,\"id\":686,\"visited\":false,\"x\":597.0,\"y\":279.0},{\"active\":true,\"id\":691,\"visited\":false,\"x\":488.0,\"y\":667.0},{\"active\":true,\"id\":692,\"visited\":false,\"x\":743.0,\"y\":631.0},{\"active\":true,\"id\":695,\"visited\":false,\"x\":243.0,\"y\":447.0},{\"active\":true,\"id\":699,\"visited\":false,\"x\":666.0,\"y\":352.0},{\"active\":true,\"id\":720,\"visited\":false,\"x\":487.0,\"y\":482.0},{\"active\":true,\"id\":726,\"visited\":false,\"x\":6.0,\"y\":324.0},{\"active\":true,\"id\":728,\"visited\":false,\"x\":401.0,\"y\":593.0},{\"active\":true,\"id\":732,\"visited\":false,\"x\":151.0,\"y\":476.0},{\"active\":true,\"id\":735,\"visited\":false,\"x\":453.0,\"y\":546.0},{\"active\":true,\"id\":737,\"visited\":false,\"x\":512.0,\"y\":193.0},{\"active\":true,\"id\":740,\"visited\":false,\"x\":283.0,\"y\":738.0},{\"active\":true,\"id\":742,\"visited\":false,\"x\":513.0,\"y\":250.0},{\"active\":true,\"id\":743,\"visited\":false,\"x\":526.0,\"y\":356.0},{\"active\":true,\"id\":747,\"visited\":false,\"x\":732.0,\"y\":481.0},{\"active\":true,\"id\":757,\"visited\":false,\"x\":1194.0,\"y\":576.0},{\"active\":true,\"id\":758,\"visited\":false,\"x\":726.0,\"y\":405.0},{\"active\":true,\"id\":769,\"visited\":false,\"x\":391.0,\"y\":470.0},{\"active\":true,\"id\":770,\"visited\":false,\"x\":298.0,\"y\":381.0},{\"active\":true,\"id\":781,\"visited\":false,\"x\":648.0,\"y\":611.0},{\"active\":true,\"id\":783,\"visited\":false,\"x\":578.0,\"y\":260.0},{\"active\":true,\"id\":786,\"visited\":false,\"x\":326.0,\"y\":586.0},{\"active\":true,\"id\":787,\"visited\":false,\"x\":213.0,\"y\":781.0},{\"active\":true,\"id\":789,\"visited\":false,\"x\":1164.0,\"y\":480.0},{\"active\":true,\"id\":795,\"visited\":false,\"x\":299.0,\"y\":328.0},{\"active\":true,\"id\":801,\"visited\":false,\"x\":60.0,\"y\":150.0},{\"active\":true,\"id\":806,\"visited\":false,\"x\":226.0,\"y\":738.0},{\"active\":true,\"id\":810,\"visited\":false,\"x\":544.0,\"y\":406.0},{\"active\":true,\"id\":812,\"visited\":false,\"x\":1192.0,\"y\":454.0},{\"active\":true,\"id\":816,\"visited\":false,\"x\":958.0,\"y\":631.0},{\"active\":true,\"id\":817,\"visited\":false,\"x\":876.0,\"y\":617.0},{\"active\":true,\"id\":821,\"visited\":false,\"x\":1136.0,\"y\":233.0},{\"active\":true,\"id\":823,\"visited\":false,\"x\":1234.0,\"y\":280.0},{\"active\":true,\"id\":825,\"visited\":false,\"x\":415.0,\"y\":493.0},{\"active\":true,\"id\":833,\"visited\":false,\"x\":777.0,\"y\":514.0},{\"active\":true,\"id\":834,\"visited\":false,\"x\":374.0,\"y\":239.0},{\"active\":true,\"id\":839,\"visited\":false,\"x\":636.0,\"y\":456.0},{\"active\":true,\"id\":848,\"visited\":false,\"x\":230.0,\"y\":314.0},{\"active\":true,\"id\":855,\"visited\":false,\"x\":503.0,\"y\":599.0},{\"active\":true,\"id\":858,\"visited\":false,\"x\":77.0,\"y\":756.0},{\"active\":true,\"id\":859,\"visited\":false,\"x\":155.0,\"y\":344.0},{\"active\":true,\"id\":863,\"visited\":false,\"x\":542.0,\"y\":279.0},{\"active\":true,\"id\":866,\"visited\":false,\"x\":504.0,\"y\":630.0},{\"active\":true,\"id\":867,\"visited\":false,\"x\":48.0,\"y\":658.0},{\"active\":true,\"id\":868,\"visited\":false,\"x\":514.0,\"y\":290.0},{\"active\":true,\"id\":869,\"visited\":false,\"x\":186.0,\"y\":277.0},{\"active\":true,\"id\":871,\"visited\":false,\"x\":284.0,\"y\":451.0},{\"active\":true,\"id\":872,\"visited\":false,\"x\":557.0,\"y\":592.0},{\"active\":true,\"id\":881,\"visited\":false,\"x\":1014.0,\"y\":551.0},{\"active\":true,\"id\":887,\"visited\":false,\"x\":298.0,\"y\":213.0},{\"active\":true,\"id\":890,\"visited\":false,\"x\":320.0,\"y\":518.0},{\"active\":true,\"id\":898,\"visited\":false,\"x\":902.0,\"y\":460.0},{\"active\":true,\"id\":900,\"visited\":false,\"x\":1047.0,\"y\":462.0},{\"active\":true,\"id\":901,\"visited\":false,\"x\":1.0,\"y\":140.0},{\"active\":true,\"id\":904,\"visited\":false,\"x\":780.0,\"y\":598.0},{\"active\":true,\"id\":905,\"visited\":false,\"x\":1137.0,\"y\":571.0},{\"active\":true,\"id\":907,\"visited\":false,\"x\":844.0,\"y\":236.0},{\"active\":true,\"id\":908,\"visited\":false,\"x\":138.0,\"y\":166.0},{\"active\":true,\"id\":911,\"visited\":false,\"x\":650.0,\"y\":499.0},{\"active\":true,\"id\":913,\"visited\":false,\"x\":750.0,\"y\":427.0},{\"active\":true,\"id\":919,\"visited\":false,\"x\":497.0,\"y\":528.0},{\"active\":true,\"id\":920,\"visited\":false,\"x\":737.0,\"y\":291.0},{\"active\":true,\"id\":922,\"visited\":false,\"x\":463.0,\"y\":589.0},{\"active\":true,\"id\":925,\"visited\":false,\"x\":1296.0,\"y\":277.0},{\"active\":true,\"id\":927,\"visited\":false,\"x\":1055.0,\"y\":509.0},{\"active\":true,\"id\":928,\"visited\":false,\"x\":1227.0,\"y\":777.0},{\"active\":true,\"id\":933,\"visited\":false,\"x\":906.0,\"y\":585.0},{\"active\":true,\"id\":934,\"visited\":false,\"x\":589.0,\"y\":391.0},{\"active\":true,\"id\":936,\"visited\":false,\"x\":-8.0,\"y\":304.0},{\"active\":true,\"id\":939,\"visited\":false,\"x\":428.0,\"y\":241.0},{\"active\":true,\"id\":940,\"visited\":false,\"x\":324.0,\"y\":435.0},{\"active\":true,\"id\":941,\"visited\":false,\"x\":260.0,\"y\":483.0},{\"active\":true,\"id\":944,\"visited\":false,\"x\":239.0,\"y\":228.0},{\"active\":true,\"id\":945,\"visited\":false,\"x\":229.0,\"y\":279.0},{\"active\":true,\"id\":948,\"visited\":false,\"x\":366.0,\"y\":678.0},{\"active\":true,\"id\":949,\"visited\":false,\"x\":287.0,\"y\":628.0},{\"active\":true,\"id\":954,\"visited\":false,\"x\":1207.0,\"y\":400.0},{\"active\":true,\"id\":960,\"visited\":false,\"x\":186.0,\"y\":478.0},{\"active\":true,\"id\":961,\"visited\":false,\"x\":548.0,\"y\":327.0},{\"active\":true,\"id\":962,\"visited\":false,\"x\":68.0,\"y\":707.0},{\"active\":true,\"id\":964,\"visited\":false,\"x\":1116.0,\"y\":523.0},{\"active\":true,\"id\":965,\"visited\":false,\"x\":743.0,\"y\":367.0},{\"active\":true,\"id\":966,\"visited\":false,\"x\":1185.0,\"y\":659.0},{\"active\":true,\"id\":967,\"visited\":false,\"x\":977.0,\"y\":330.0},{\"active\":true,\"id\":969,\"visited\":false,\"x\":515.0,\"y\":337.0},{\"active\":true,\"id\":977,\"visited\":false,\"x\":1141.0,\"y\":662.0},{\"active\":true,\"id\":978,\"visited\":false,\"x\":1164.0,\"y\":630.0},{\"active\":true,\"id\":979,\"visited\":false,\"x\":1264.0,\"y\":362.0},{\"active\":true,\"id\":980,\"visited\":false,\"x\":1324.0,\"y\":289.0},{\"active\":true,\"id\":981,\"visited\":false,\"x\":1025.0,\"y\":667.0},{\"active\":true,\"id\":982,\"visited\":false,\"x\":956.0,\"y\":576.0},{\"active\":true,\"id\":985,\"visited\":false,\"x\":676.0,\"y\":610.0},{\"active\":true,\"id\":986,\"visited\":false,\"x\":798.0,\"y\":619.0},{\"active\":true,\"id\":988,\"visited\":false,\"x\":678.0,\"y\":865.0},{\"active\":true,\"id\":990,\"visited\":false,\"x\":908.0,\"y\":630.0},{\"active\":true,\"id\":993,\"visited\":false,\"x\":527.0,\"y\":548.0},{\"active\":true,\"id\":995,\"visited\":false,\"x\":642.0,\"y\":409.0},{\"active\":true,\"id\":996,\"visited\":false,\"x\":768.0,\"y\":181.0},{\"active\":true,\"id\":997,\"visited\":false,\"x\":693.0,\"y\":450.0},{\"active\":true,\"id\":3,\"visited\":false,\"x\":333.0,\"y\":146.0},{\"active\":true,\"id\":6,\"visited\":false,\"x\":159.0,\"y\":455.0},{\"active\":true,\"id\":7,\"visited\":false,\"x\":434.0,\"y\":517.0},{\"active\":true,\"id\":8,\"visited\":false,\"x\":1253.0,\"y\":575.0},{\"active\":true,\"id\":10,\"visited\":false,\"x\":758.0,\"y\":575.0},{\"active\":true,\"id\":14,\"visited\":false,\"x\":415.0,\"y\":622.0},{\"active\":true,\"id\":15,\"visited\":false,\"x\":593.0,\"y\":428.0},{\"active\":true,\"id\":16,\"visited\":false,\"x\":989.0,\"y\":682.0},{\"active\":true,\"id\":17,\"visited\":false,\"x\":613.0,\"y\":703.0},{\"active\":true,\"id\":18,\"visited\":false,\"x\":290.0,\"y\":252.0},{\"active\":true,\"id\":22,\"visited\":false,\"x\":979.0,\"y\":520.0},{\"active\":true,\"id\":23,\"visited\":false,\"x\":1144.0,\"y\":523.0},{\"active\":true,\"id\":24,\"visited\":false,\"x\":594.0,\"y\":312.0},{\"active\":true,\"id\":25,\"visited\":false,\"x\":463.0,\"y\":264.0},{\"active\":true,\"id\":26,\"visited\":false,\"x\":597.0,\"y\":673.0},{\"active\":true,\"id\":28,\"visited\":false,\"x\":431.0,\"y\":661.0},{\"active\":true,\"id\":29,\"visited\":false,\"x\":853.0,\"y\":306.0},{\"active\":true,\"id\":30,\"visited\":false,\"x\":130.0,\"y\":130.0},{\"active\":true,\"id\":32,\"visited\":false,\"x\":326.0,\"y\":483.0},{\"active\":true,\"id\":35,\"visited\":false,\"x\":221.0,\"y\":602.0},{\"active\":false,\"id\":38,\"visited\":false,\"x\":1275.0,\"y\":334.0},{\"active\":true,\"id\":39,\"visited\":false,\"x\":928.0,\"y\":386.0},{\"active\":true,\"id\":42,\"visited\":false,\"x\":1171.0,\"y\":297.0},{\"active\":true,\"id\":45,\"visited\":false,\"x\":530.0,\"y\":751.0},{\"active\":true,\"id\":46,\"visited\":false,\"x\":12.0,\"y\":502.0},{\"active\":true,\"id\":48,\"visited\":false,\"x\":35.0,\"y\":474.0},{\"active\":true,\"id\":49,\"visited\":false,\"x\":68.0,\"y\":416.0},{\"active\":true,\"id\":50,\"visited\":false,\"x\":41.0,\"y\":342.0},{\"active\":true,\"id\":51,\"visited\":false,\"x\":-42.0,\"y\":355.0},{\"active\":true,\"id\":52,\"visited\":false,\"x\":574.0,\"y\":486.0},{\"active\":true,\"id\":53,\"visited\":false,\"x\":688.0,\"y\":400.0},{\"active\":true,\"id\":55,\"visited\":false,\"x\":269.0,\"y\":705.0},{\"active\":true,\"id\":56,\"visited\":false,\"x\":696.0,\"y\":634.0},{\"active\":true,\"id\":57,\"visited\":false,\"x\":878.0,\"y\":431.0},{\"active\":true,\"id\":58,\"visited\":false,\"x\":291.0,\"y\":554.0},{\"active\":true,\"id\":63,\"visited\":false,\"x\":603.0,\"y\":348.0},{\"active\":true,\"id\":64,\"visited\":false,\"x\":621.0,\"y\":315.0},{\"active\":true,\"id\":68,\"visited\":false,\"x\":710.0,\"y\":214.0},{\"active\":true,\"id\":74,\"visited\":false,\"x\":289.0,\"y\":593.0},{\"active\":true,\"id\":82,\"visited\":false,\"x\":853.0,\"y\":576.0},{\"active\":false,\"id\":83,\"visited\":false,\"x\":796.0,\"y\":754.0},{\"active\":true,\"id\":85,\"visited\":false,\"x\":1066.0,\"y\":240.0},{\"active\":true,\"id\":86,\"visited\":false,\"x\":1008.0,\"y\":454.0},{\"active\":false,\"id\":89,\"visited\":false,\"x\":1032.0,\"y\":474.0},{\"active\":true,\"id\":90,\"visited\":false,\"x\":638.0,\"y\":184.0},{\"active\":true,\"id\":91,\"visited\":false,\"x\":331.0,\"y\":342.0},{\"active\":true,\"id\":92,\"visited\":false,\"x\":331.0,\"y\":372.0},{\"active\":true,\"id\":101,\"visited\":false,\"x\":1231.0,\"y\":677.0},{\"active\":false,\"id\":102,\"visited\":false,\"x\":1188.0,\"y\":360.0},{\"active\":false,\"id\":103,\"visited\":false,\"x\":902.0,\"y\":527.0},{\"active\":true,\"id\":104,\"visited\":false,\"x\":1080.0,\"y\":419.0},{\"active\":true,\"id\":105,\"visited\":false,\"x\":579.0,\"y\":287.0},{\"active\":true,\"id\":106,\"visited\":false,\"x\":374.0,\"y\":401.0},{\"active\":true,\"id\":107,\"visited\":false,\"x\":613.0,\"y\":251.0},{\"active\":true,\"id\":108,\"visited\":false,\"x\":271.0,\"y\":412.0},{\"active\":true,\"id\":110,\"visited\":false,\"x\":114.0,\"y\":457.0},{\"active\":false,\"id\":111,\"visited\":false,\"x\":868.0,\"y\":286.0},{\"active\":true,\"id\":112,\"visited\":false,\"x\":33.0,\"y\":586.0},{\"active\":true,\"id\":113,\"visited\":false,\"x\":231.0,\"y\":61.0},{\"active\":true,\"id\":114,\"visited\":false,\"x\":109.0,\"y\":762.0},{\"active\":true,\"id\":115,\"visited\":false,\"x\":159.0,\"y\":734.0},{\"active\":true,\"id\":116,\"visited\":false,\"x\":1068.0,\"y\":561.0},{\"active\":true,\"id\":120,\"visited\":false,\"x\":875.0,\"y\":203.0},{\"active\":true,\"id\":121,\"visited\":false,\"x\":1051.0,\"y\":263.0},{\"active\":true,\"id\":126,\"visited\":false,\"x\":777.0,\"y\":325.0},{\"active\":true,\"id\":127,\"visited\":false,\"x\":293.0,\"y\":230.0},{\"active\":false,\"id\":129,\"visited\":false,\"x\":215.0,\"y\":217.0},{\"active\":true,\"id\":130,\"visited\":false,\"x\":455.0,\"y\":430.0},{\"active\":true,\"id\":131,\"visited\":false,\"x\":563.0,\"y\":636.0},{\"active\":true,\"id\":132,\"visited\":false,\"x\":108.0,\"y\":370.0},{\"active\":true,\"id\":133,\"visited\":false,\"x\":1256.0,\"y\":666.0},{\"active\":true,\"id\":134,\"visited\":false,\"x\":343.0,\"y\":649.0},{\"active\":true,\"id\":135,\"visited\":false,\"x\":54.0,\"y\":404.0},{\"active\":true,\"id\":136,\"visited\":false,\"x\":56.0,\"y\":368.0},{\"active\":true,\"id\":137,\"visited\":false,\"x\":143.0,\"y\":605.0},{\"active\":true,\"id\":138,\"visited\":false,\"x\":122.0,\"y\":335.0},{\"active\":true,\"id\":139,\"visited\":false,\"x\":79.0,\"y\":352.0},{\"active\":false,\"id\":140,\"visited\":false,\"x\":1250.0,\"y\":611.0},{\"active\":true,\"id\":141,\"visited\":false,\"x\":1283.0,\"y\":450.0},{\"active\":true,\"id\":144,\"visited\":false,\"x\":826.0,\"y\":588.0},{\"active\":true,\"id\":145,\"visited\":false,\"x\":26.0,\"y\":377.0},{\"active\":true,\"id\":146,\"visited\":false,\"x\":131.0,\"y\":303.0},{\"active\":true,\"id\":147,\"visited\":false,\"x\":1116.0,\"y\":411.0},{\"active\":true,\"id\":149,\"visited\":false,\"x\":890.0,\"y\":254.0},{\"active\":true,\"id\":150,\"visited\":false,\"x\":820.0,\"y\":193.0},{\"active\":true,\"id\":151,\"visited\":false,\"x\":686.0,\"y\":274.0},{\"active\":true,\"id\":152,\"visited\":false,\"x\":670.0,\"y\":147.0},{\"active\":true,\"id\":153,\"visited\":false,\"x\":250.0,\"y\":384.0},{\"active\":true,\"id\":155,\"visited\":false,\"x\":213.0,\"y\":361.0},{\"active\":true,\"id\":156,\"visited\":false,\"x\":195.0,\"y\":383.0},{\"active\":true,\"id\":157,\"visited\":false,\"x\":44.0,\"y\":429.0},{\"active\":true,\"id\":159,\"visited\":false,\"x\":101.0,\"y\":434.0},{\"active\":true,\"id\":160,\"visited\":false,\"x\":201.0,\"y\":203.0},{\"active\":true,\"id\":163,\"visited\":false,\"x\":218.0,\"y\":174.0},{\"active\":true,\"id\":164,\"visited\":false,\"x\":174.0,\"y\":126.0},{\"active\":true,\"id\":165,\"visited\":false,\"x\":-6.0,\"y\":545.0},{\"active\":true,\"id\":166,\"visited\":false,\"x\":324.0,\"y\":638.0},{\"active\":true,\"id\":167,\"visited\":false,\"x\":583.0,\"y\":534.0},{\"active\":true,\"id\":171,\"visited\":false,\"x\":628.0,\"y\":731.0},{\"active\":true,\"id\":172,\"visited\":false,\"x\":916.0,\"y\":811.0},{\"active\":true,\"id\":173,\"visited\":false,\"x\":976.0,\"y\":412.0},{\"active\":true,\"id\":174,\"visited\":false,\"x\":1078.0,\"y\":393.0},{\"active\":true,\"id\":175,\"visited\":false,\"x\":1230.0,\"y\":427.0},{\"active\":true,\"id\":176,\"visited\":false,\"x\":92.0,\"y\":602.0},{\"active\":true,\"id\":177,\"visited\":false,\"x\":1037.0,\"y\":205.0},{\"active\":true,\"id\":178,\"visited\":false,\"x\":933.0,\"y\":216.0},{\"active\":true,\"id\":179,\"visited\":false,\"x\":703.0,\"y\":511.0},{\"active\":true,\"id\":180,\"visited\":false,\"x\":695.0,\"y\":487.0},{\"active\":true,\"id\":185,\"visited\":false,\"x\":241.0,\"y\":652.0},{\"active\":true,\"id\":186,\"visited\":false,\"x\":251.0,\"y\":797.0},{\"active\":true,\"id\":187,\"visited\":false,\"x\":157.0,\"y\":798.0},{\"active\":false,\"id\":191,\"visited\":false,\"x\":922.0,\"y\":528.0},{\"active\":false,\"id\":192,\"visited\":false,\"x\":696.0,\"y\":375.0},{\"active\":false,\"id\":193,\"visited\":false,\"x\":793.0,\"y\":362.0},{\"active\":false,\"id\":194,\"visited\":false,\"x\":688.0,\"y\":483.0},{\"active\":false,\"id\":195,\"visited\":false,\"x\":181.0,\"y\":779.0},{\"active\":false,\"id\":196,\"visited\":false,\"x\":341.0,\"y\":712.0},{\"active\":false,\"id\":198,\"visited\":false,\"x\":197.0,\"y\":435.0},{\"active\":false,\"id\":201,\"visited\":false,\"x\":483.0,\"y\":444.0},{\"active\":false,\"id\":203,\"visited\":false,\"x\":414.0,\"y\":333.0},{\"active\":false,\"id\":204,\"visited\":false,\"x\":391.0,\"y\":269.0},{\"active\":false,\"id\":205,\"visited\":false,\"x\":173.0,\"y\":159.0},{\"active\":false,\"id\":206,\"visited\":false,\"x\":630.0,\"y\":288.0},{\"active\":false,\"id\":207,\"visited\":false,\"x\":405.0,\"y\":136.0},{\"active\":false,\"id\":208,\"visited\":false,\"x\":1117.0,\"y\":472.0},{\"active\":false,\"id\":209,\"visited\":false,\"x\":842.0,\"y\":351.0},{\"active\":false,\"id\":210,\"visited\":false,\"x\":245.0,\"y\":772.0},{\"active\":false,\"id\":225,\"visited\":false,\"x\":251.0,\"y\":555.0},{\"active\":false,\"id\":226,\"visited\":false,\"x\":433.0,\"y\":590.0},{\"active\":false,\"id\":228,\"visited\":false,\"x\":427.0,\"y\":563.0},{\"active\":false,\"id\":229,\"visited\":false,\"x\":288.0,\"y\":504.0}]}";

		try {
			// Parse the JSON into the set of colonies
			JSONObject jsonRoot = new JSONObject(colonyString);

			JSONArray array = jsonRoot.getJSONArray("colonies");

			for (int i = 0, max = array.length(); i < max; i++) {
				JSONObject colonyJson = array.getJSONObject(i);

				Colony colony = new Colony();
				colony.fromJSON(colonyJson);

				colonies.add(colony);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.samcrow.data.provider.ColonyProvider#getColonies()
	 */
	@Override
	public ColonyList getColonies() {
		return colonies;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.samcrow.data.provider.ColonyProvider#updateColonies()
	 */
	@Override
	public void updateColonies() throws UnsupportedOperationException {

		throw new UnsupportedOperationException(
				"The hard-coded colony provider, as it uses a hard-coded set of colonies, does not support updating colony information.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.samcrow.data.provider.ColonyProvider#updateColony(org.samcrow.data
	 * .Colony)
	 */
	@Override
	public void updateColony(Colony colony)
			throws UnsupportedOperationException {

		throw new UnsupportedOperationException(
				"The hard-coded colony provider, as it uses a hard-coded set of colonies, does not support updating colony information.");

	}

	//Singleton
	private HardCodedColonyProvider() {}
	public static final HardCodedColonyProvider instance = new HardCodedColonyProvider();
}
