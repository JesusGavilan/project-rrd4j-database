package roundrobin.database;

import java.io.IOException;

import org.rrd4j.core.RrdDb;
import org.rrd4j.core.RrdDef;
import org.rrd4j.core.Sample;
import org.rrd4j.core.Util;

import roundrobin.database.pojos.PortStat;
import roundrobin.database.pojos.StatCollection;
import roundrobin.database.pojos.StatResponse;
import roundrobin.database.pojos.SwitchStat;
import roundrobin.database.queries.QueryController;
import roundrobin.database.queries.QueryPort;
import roundrobin.database.queries.QuerySwitch;

//LLamada a la base de datos.
public class StatManager {
	private DBAccess db;
	private static StatManager instance;

	private StatManager() {
		this.db = new DBAccess();
	}

	public static StatManager getInstance() {
		if (instance == null)
			instance = new StatManager();
		return instance;
	}

	public void pushStat(StatCollection statcollection) throws IOException {

		// Pushing controllers statistics
		// Creating round robin database for Controller
		
		if (!db.rrdFileExists(db.rrdPath + "floodLight.rrd")) {
			RrdDef rrdDef = db.createRrdDefController("floodLight",
					Util.getTime());
			RrdDb rrdInit = new RrdDb(rrdDef);
			rrdInit.close();
		}

		// Updating round robin database for controller
		else {
			RrdDb rrdDb = db.getRrdDbPool().requestRrdDb(db.rrdPath + "floodLight.rrd");
			
			Sample sample = rrdDb.createSample();
			sample.setValue("CpuAvg", statcollection.getControllerStat().getCpuAvg());
			sample.setValue("MemoryPCT", statcollection.getControllerStat().getMemoryPct());
			sample.update();
			db.getRrdDbPool().release(rrdDb);
		}

		// Pushing Switch statitistcs
		for (SwitchStat sw : statcollection.getSwitchStatCollection()) {
			if (!db.rrdFileExists(db.rrdPath + db.convertId(sw.getSwitchId()) + ".rrd")) {
				
				RrdDef rrdDef = db.createRrdDefSwitch(
						db.convertId(sw.getSwitchId()), Util.getTime());
				RrdDb rrdInit = new RrdDb(rrdDef);
				rrdInit.close();
			} else {
				RrdDb rrdDb = db.getRrdDbPool().requestRrdDb(db.rrdPath + db.convertId(sw.getSwitchId() + ".rrd"));
				Sample sample = rrdDb.createSample();
				sample.setValue("packetCount", sw.getPacketCount());
				sample.setValue("byteCount", sw.getByteCount());
				sample.setValue("flowCount", sw.getFlowCount());
				sample.update();
				db.getRrdDbPool().release(rrdDb);
			}
		}

		// Pushing puerto statistics
		for (PortStat port : statcollection.getPortStatCollection()) {
			if (!db.rrdFileExists(db.rrdPath + db.convertId(port.getPortId())+ ".rrd")) {
				RrdDef rrdDef = db.createRrdDefPuerto(
						db.convertId(port.getPortId()), Util.getTime());
				RrdDb rrdInit = new RrdDb(rrdDef);
				rrdInit.close();
			} else {
				RrdDb rrdDb = db.getRrdDbPool().requestRrdDb(db.rrdPath + db.convertId(port.getPortId() + ".rrd"));
				Sample sample = rrdDb.createSample();
				sample.setValue("receivePackets", port.getReceivePackets());
				sample.setValue("transmitPackets", port.getTransmitPackets());
				sample.setValue("receiveBytes", port.getReceiveBytes());
				sample.setValue("transmitBytes", port.getTransmitBytes());
				sample.setValue("receiveDropped", port.getReceiveDropped());
				sample.setValue("transmitDropped", port.getTransmitDropped());
				sample.setValue("receiveErrors", port.getReceiveErrors());
				sample.setValue("transmitErrors", port.getTransmitErrors());
				sample.setValue("receiveFrameErrors",
						port.getReceiveFrameErrors());
				sample.setValue("receiveOverrunErrors",
						port.getReceiveOverrunErrors());
				sample.setValue("receiveCRCErrors", port.getReceiveCRCErrors());
				sample.setValue("collisions", port.getCollisions());
				sample.update();
				db.getRrdDbPool().release(rrdDb);
			}

		}

	}

	public StatResponse getStat(String itemId, String statParameter,String typeOfStat, String granularity) throws IOException{

		String resourcePath = db.rrdPath + db.convertId(itemId) + ".rrd";
		StatResponse response = new StatResponse();
		long actualTimeStamp = Util.getTime();
		long start = getTimeStampGran(granularity);
		long end   = actualTimeStamp;
		
		RrdDb rrdDb = db.getRrdDbPool().requestRrdDb(resourcePath);
		//String parameter = statParameter;
		
		response.setIdObject(itemId);
		response.setParameter(statParameter);
		response.setTimeAxxis(QueryController.getTimevalues(rrdDb, start, end, typeOfStat));
		
		if (db.typeObject(itemId).equals("controller")){
		
			switch (statParameter){
				case "CpuAvg":	response.setValueAxxis(QueryController.getCPUAvg(rrdDb, start, end, typeOfStat));
								db.getRrdDbPool().release(rrdDb);
								break;
				case "MemoryPCT":	response.setValueAxxis(QueryController.getMemoryPct(rrdDb, start, end, typeOfStat));
									db.getRrdDbPool().release(rrdDb);
									break;
			}					
		}
		
		else{

			
			if(db.typeObject(itemId).equals("sw")){
				
				switch (statParameter){
				
					case "packetCount": response.setValueAxxis(QuerySwitch.getPacketCount(rrdDb, start, end, typeOfStat));
										db.getRrdDbPool().release(rrdDb);
										break;
					case "byteCount":	response.setValueAxxis(QuerySwitch.getByteCount(rrdDb, start, end, typeOfStat));
						  				db.getRrdDbPool().release(rrdDb);
										break;
					case "flowCount":	response.setValueAxxis(QuerySwitch.getFlowCount(rrdDb, start, end, typeOfStat));
										db.getRrdDbPool().release(rrdDb);
										break;
				}
			}
			else{
								
				switch (statParameter){
					case "receivePackets":	response.setValueAxxis(QueryPort.getReceivePackets(rrdDb, start, end, typeOfStat));
											db.getRrdDbPool().release(rrdDb);
											break;
					case "transmitPackets":	response.setValueAxxis(QueryPort.getTransmitPackets(rrdDb, start, end, typeOfStat));
											db.getRrdDbPool().release(rrdDb);
											break;
					case "receiveBytes": 	response.setValueAxxis(QueryPort.getReceiveBytes(rrdDb, start, end, typeOfStat));
											db.getRrdDbPool().release(rrdDb);
											break;
					case "transmitBytes":	response.setValueAxxis(QueryPort.getTransmitBytes(rrdDb, start, end, typeOfStat));
											db.getRrdDbPool().release(rrdDb);
											break;
					case "receiveDropped":	response.setValueAxxis(QueryPort.getReceiveDropped(rrdDb, start, end, typeOfStat));
											db.getRrdDbPool().release(rrdDb);
											break;
					case "transmitDropped":	response.setValueAxxis(QueryPort.getTransmitDropped(rrdDb, start, end, typeOfStat));
											db.getRrdDbPool().release(rrdDb);
											break;
					case "receiveErrors":	response.setValueAxxis(QueryPort.getReceiveErrors(rrdDb, start, end, typeOfStat));
											db.getRrdDbPool().release(rrdDb);
											break;
					case "transmitErrors":	response.setValueAxxis(QueryPort.getTransmitErrors(rrdDb, start, end, typeOfStat));
											db.getRrdDbPool().release(rrdDb);
											break;
					case "receiveFrameErrors":	response.setValueAxxis(QueryPort.getReceiveFrameErrors(rrdDb, start, end, typeOfStat));
												db.getRrdDbPool().release(rrdDb);
												break;
					case "receiveOverrunErrors":	response.setValueAxxis(QueryPort.getReceiveOverrunErrors(rrdDb, start, end, typeOfStat));
													db.getRrdDbPool().release(rrdDb);
													break;
					case "receiveCRCErrors":	response.setValueAxxis(QueryPort.getReceiveCRCErrors(rrdDb, start, end, typeOfStat));
												db.getRrdDbPool().release(rrdDb);
												break;
					case "collisions":	response.setValueAxxis(QueryPort.getCollisions(rrdDb, start, end, typeOfStat));
										db.getRrdDbPool().release(rrdDb);
										break;
			}
				
				
			}
			
		}
	
	return response;
		
	}

	private long getTimeStampGran(String granularity) {
		long value = 0;
		switch (granularity) {
		case "second":
			value = (long) 1;
			break;
		case "minute":
			value = (long) 60;
			break;
		case "hour":
			value = (long) 3600;
			break;
		case "day":
			value = (long) 3600 * 24;
			break;
		case "week":
			value = (long) 3600 * 24 * 7;
			break;
		case "month":
			value = (long) 3600 * 24 * 30;
			break;
		case "year":
			value = (long) 3600 * 24 * 30 * 365;
		}
		return value;

	}
}
